package com.fourclover.clobee.event.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFriendsDetail {
    private Long eventFriendsId;
    private Long eventInfoId;
    private Long userId;
    private int eventFriendsRouletteCountLimit;
    private int eventFriendsRouletteCurrentCount;
}
