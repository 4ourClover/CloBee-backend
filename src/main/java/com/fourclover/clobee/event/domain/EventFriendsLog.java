package com.fourclover.clobee.event.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EventFriendsLog {
    private Long eventFriendsLogId;
    private Long eventInfoId;
    private Long eventFriendsId;
    private Long eventFriendsInviter_userId;
    private Long eventFriendsInvited_userId;
}
