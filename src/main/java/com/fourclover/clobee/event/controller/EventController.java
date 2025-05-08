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

    @GetMapping("/getCardEvent")
    public ResponseEntity<List<EventInfo>> getCardEvent(@RequestParam(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok(eventService.getCardEvents(userId));
    }

    @GetMapping("/getTotalAttend")
    public ResponseEntity<List<String>> getTotalAttend(@RequestParam("userId") long userId,
                                                       @RequestParam("month") String month) {
        return ResponseEntity.ok(eventService.getTotalAttend(userId, month));
    }

    @PostMapping("/addAttend")
    public ResponseEntity<Object> addAttend(@RequestBody EventAttendanceDetail eventAttendanceDetail) {
        return ResponseEntity.ok(eventService.addAttend(eventAttendanceDetail));
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
}
