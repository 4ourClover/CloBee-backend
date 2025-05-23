package com.fourclover.clobee.event.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventFriendsInviteInfo {
    private String invitationCode;
    private int rouletteCount;
}
