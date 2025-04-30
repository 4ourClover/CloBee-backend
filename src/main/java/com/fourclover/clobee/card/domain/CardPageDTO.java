package com.fourclover.clobee.card.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/*
카드 리스트와 전체 카드 개수 함께 전송
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardPageDTO {
    private List<CardPageListDTO> cardPageList;
    private int totalCount;
}
