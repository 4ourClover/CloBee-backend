package com.fourclover.clobee.event.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFriends {
    private int eventInfoId;
    private int inviterUserId;
    private int invitedUserId;
}
