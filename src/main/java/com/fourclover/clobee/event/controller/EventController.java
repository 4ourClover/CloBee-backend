package com.fourclover.clobee.event.controller;

import com.fourclover.clobee.event.domain.EventAttendanceDetail;
import com.fourclover.clobee.event.domain.EventFindingCloverDetail;
import com.fourclover.clobee.event.domain.EventInfo;
import com.fourclover.clobee.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    private EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 카드사 이벤트 조회
    @GetMapping("/getCardEvent")
    public ResponseEntity<List<EventInfo>> getCardEvent(@RequestParam(value = "userId", required = false) Long userId,
                                                        @RequestParam int pageSize,
                                                        @RequestParam int pageNumber) {
        return ResponseEntity.ok(eventService.getCardEvents(userId, pageSize, pageNumber));
    }

    // 출석 이벤트 : 출석일수 조회
    @GetMapping("/getTotalAttend")
    public ResponseEntity<List<String>> getTotalAttend(@RequestParam("userId") long userId,
                                                       @RequestParam("month") String month) {
        return ResponseEntity.ok(eventService.getTotalAttend(userId, month));
    }

    // 출석 이벤트 : 출석일수 추가
    @PostMapping("/addAttend")
    public ResponseEntity<Object> addAttend(@RequestBody EventAttendanceDetail eventAttendanceDetail) {
        return ResponseEntity.ok(eventService.addAttend(eventAttendanceDetail));
    }

    // 클로버 채우기 이벤트 : 클로버 불러오기 & 추가
    @GetMapping("/fillClover/init")
    public ResponseEntity<Object> selectCloverCollection(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(eventService.selectCloverCollection(userId));
    }

    // 게임 시작 or 오늘 초대 보너스(친구 초대시 invited=true)
    @PostMapping("/findClover/init")
    public ResponseEntity<EventFindingCloverDetail> initClover(
            @RequestParam("user_id") Long userId,
            @RequestParam(value="invited", defaultValue="false") boolean invited) {
        return ResponseEntity.ok(eventService.startCloverGame(userId, invited));
    }

    // 카드 클릭(성공=true/실패=false)
    @PostMapping("/findClover/attempt")
    public ResponseEntity<EventFindingCloverDetail> attemptClover(
            @RequestParam("user_id") Long userId,
            @RequestParam("success") boolean success) {
        return ResponseEntity.ok(eventService.processCloverAttempt(userId, success));
    }

    // 클로버 찾기 게임 사용자 현재 상태 조회
    @GetMapping("/findClover/status")
    public ResponseEntity<EventFindingCloverDetail> getCloverStatus(
            @RequestParam("user_id") Long userId) {
        return ResponseEntity.ok(eventService.getCloverStatus(userId));
    }

    // 친구 초대 이벤트
    // 친구 초대 처리 (회원가입 시 호출)


}
