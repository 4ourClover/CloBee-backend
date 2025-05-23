package com.fourclover.clobee.event.service;

import com.fourclover.clobee.event.domain.*;

import java.util.List;

public interface EventService {
    List<String> getTotalAttend(long userId, String month);
    Long addAttend(EventAttendanceDetail eventAttendanceDetail);

    List<EventInfo> getCardEvents(Long userId);

    void initCloverGame();
    EventFindingCloverDetail startCloverGame(Long userId, boolean invitedByFriend);
    EventFindingCloverDetail processCloverAttempt(Long userId, boolean success);
    EventFindingCloverDetail getCloverStatus(Long userId);


    void processInvitation(EventFriends eventFriends);

    EventFriendsInviteInfo getInviteInfo(int userId);

    String spinRoulette(int userId);
}
