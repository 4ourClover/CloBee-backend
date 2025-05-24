package com.fourclover.clobee.event.service;

import com.fourclover.clobee.common.ComCode;
import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import com.fourclover.clobee.event.domain.*;
import com.fourclover.clobee.event.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    private static final int MAX_ROULETTE_COUNT = 3;
    private static final int CURRENT_EVENT_ID = 1;
    private static final int CURRENT_EVENT_TYPE_CD = 603;

    @Override
    public List<String> getTotalAttend(long userId, String month) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("month", month);
        return eventRepository.getTotalAttend(params);
    }

    @Override
    public Long addAttend(EventAttendanceDetail eventAttendanceDetail) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", eventAttendanceDetail.getUserId());
        params.put("createdAt", LocalDate.now());

        // 이미 출석했을 시
        if (!eventRepository.getTotalAttend(params).isEmpty()) {
            throw new ApiException(ErrorCode.CONFLICT_USER_ATTENDANCE);
        }

        EventInfo eventInfo = eventRepository.selectEventInfoByTypeCd(ComCode.ATTEND_EVENT.getCodeId());
        EventAttendanceDetail eventDetail = EventAttendanceDetail.builder()
                .userId(eventAttendanceDetail.getUserId())
                .eventInfoId(eventInfo.getEventInfoId()).build();

        return eventRepository.addAttendDay(eventDetail);
    }


    @Override
    public List<EventInfo> getCardEvents(Long userId, int pageSize, int pageNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId != null ? userId : 0L);
        params.put("comCodeId", ComCode.CARD_EVENT.getCodeId());
        params.put("size", pageSize);
        params.put("offset", (pageNumber - 1) * pageSize);
        return eventRepository.getEventInfo(params);
    }

    // 배치 프로그램 || 클로버 다음날되면 참여 여부 초기화
    @Override
    public void initCloverGame() {
        List<EventFindingCloverDetail> userList = eventRepository.selectCloverDetailByUserList();

        for (EventFindingCloverDetail eventFindingCloverDetail : userList) {
            eventFindingCloverDetail.setEventFindingCloverParticipationStatus(false);
            eventRepository.updateCloverDetail(eventFindingCloverDetail);
        }
    }

    // 클로버 찾기 이벤트
    // 게임 시작 혹은 새로운 날짜에 자동 초기화
    @Transactional
    @Override
    public EventFindingCloverDetail startCloverGame(Long userId, boolean invited) {
        // 스크립트로 미리 넣어둔 event_info 를 조회
        EventInfo info = eventRepository.selectEventInfoByTypeCd(ComCode.CLOVER_FIND_EVENT.getCodeId());
        if (info == null) {
            throw new ApiException(ErrorCode.EVENT_INFO_NOT_FOUND);
        }

        EventFindingCloverDetail d = eventRepository.selectCloverDetailByUserId(userId);

        // 첫 참여 혹은 날짜가 바뀐 경우
        if (d == null
                || d.getCreatedAt().toLocalDate().isBefore(LocalDate.now())) {

            d = new EventFindingCloverDetail();
            d.setEventInfoId(info.getEventInfoId());
            d.setUserId(userId);
            d.setEventFindingCloverParticipationStatus(false);
            d.setEventFindingCloverCurrentStage(1);
            d.setEventFindingCloverReceiveCoupon(false);
            d.setEventFindingCloverAttemptsLeft(30);
            eventRepository.insertCloverDetail(d);

        } else if (invited) {
            // 친구 초대 시: 당일 참여 여부 완전 초기화 (시도 횟수 5회로)
            d.setEventFindingCloverParticipationStatus(false);
            d.setEventFindingCloverCurrentStage(1);
            d.setEventFindingCloverAttemptsLeft(30);
            eventRepository.updateCloverDetail(d);
        }

        // 오늘 이미 참여한 경우(생성일이 오늘이면서 participation_status=true)
        // 비초대 상태일때 오늘 이미 참여한 사람은 예외처리
        if (!invited
                && Boolean.TRUE.equals(d.getEventFindingCloverParticipationStatus())) {
            throw new ApiException(ErrorCode.ALREADY_PARTICIPATED);
        }

        // 그 외(이미 오늘 참여 중이고 초대 아님)는 아무 변경 없이 반환
        return d;
    }


    // 유저의 카드 클릭 처리(성공/실패)
    // 예외를 던져도 DB 업데이트가 유지
    @Transactional(noRollbackFor = ApiException.class)
    @Override
    public EventFindingCloverDetail processCloverAttempt(Long userId, boolean success) {
        EventFindingCloverDetail d = eventRepository.selectCloverDetailByUserId(userId);

        // 이미 참여 여부 체크 (true면 더 이상 시도 불가)
        if (Boolean.TRUE.equals(d.getEventFindingCloverParticipationStatus())) {
            throw new ApiException(ErrorCode.ALREADY_PARTICIPATED);
        }

        // 이미 쿠폰을 받은 상태에서 3단계를 클리어 시
        if (success
                && Boolean.TRUE.equals(d.getEventFindingCloverReceiveCoupon())
                && Integer.valueOf(3).equals(d.getEventFindingCloverCurrentStage())) {
            d.setEventFindingCloverCurrentStage(1);
            d.setEventFindingCloverParticipationStatus(true);
            eventRepository.updateCloverDetail(d);
            throw new ApiException(ErrorCode.COUPON_ALREADY_RECEIVED);
        }

        // 시도 차감
        int left = d.getEventFindingCloverAttemptsLeft() - 1;
        d.setEventFindingCloverAttemptsLeft(left);

        if (!success && left <= 0) {
            // 게임 종료 설정
            d.setEventFindingCloverCurrentStage(1);
            d.setEventFindingCloverParticipationStatus(true);
            // 상태 저장
            eventRepository.updateCloverDetail(d);
            throw new ApiException(ErrorCode.ATTEMPTS_EXHAUSTED);
        }

        if (success) {
            // 스테이지 클리어
            int nextStage = d.getEventFindingCloverCurrentStage() + 1;
            d.setEventFindingCloverCurrentStage(nextStage);

            // 다음 스테이지 준비
            d.setEventFindingCloverAttemptsLeft(30);

            if (nextStage > 3 && !d.getEventFindingCloverReceiveCoupon()) {
                // 3단계 최초 클리어 -> 쿠폰 지급
                d.setEventFindingCloverReceiveCoupon(true);

                // 해당 이벤트의 쿠폰 템플릿 조회
                List<CouponTemplate> templates = eventRepository
                        .selectCouponTemplatesByEventType(ComCode.CLOVER_FIND_EVENT.getCodeId());
                if (!templates.isEmpty()) {
                    CouponTemplate tpl = templates.get(0);

                    // 쿠폰 발급 정보 생성
                    CouponInfo coupon = new CouponInfo();
                    coupon.setUserId(d.getUserId());
                    coupon.setTemplateId(tpl.getTemplateId());
                    coupon.setCouponUsedYn(false);
                    coupon.setCouponDoneYn(false);
                    coupon.setCreatedAt(LocalDateTime.now());
                    coupon.setUpdatedAt(LocalDateTime.now());
                    eventRepository.insertCouponInfo(coupon);
                }

            }

            // 성공으로 3단계 돌파 시 게임 종료
            if (d.getEventFindingCloverCurrentStage() > 3) {
                d.setEventFindingCloverCurrentStage(1);
                d.setEventFindingCloverParticipationStatus(true);
            }
        }
        eventRepository.updateCloverDetail(d);
        return d;
    }


    // 사용자의 클로버 찾기 게임 현재 상태 조회
    @Override
    public EventFindingCloverDetail getCloverStatus(Long userId) {
        EventFindingCloverDetail d = eventRepository.selectCloverDetailByUserId(userId);
        if (d == null) {
            throw new ApiException(ErrorCode.GAME_INFO_NOT_FOUND);
        }
        return d;
    }




}
