package com.fourclover.clobee.card.repository;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardPageListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CardRepository {
    // 전체 카드 리스트를 가져옴(offset : 몇 번째 카드 부터, size : 몇 개의 카드를 보여줄 지)
    List<CardPageListDTO> allCardPaging(@Param("cardType") int cardType, @Param("offset") int offset, @Param("size") int size);

    // 전체 카드 개수
    int allCardCount(@Param("cardType") int cardType);

    // 카드 혜택
    List<CardBenefitDetail> cardBenefit(@Param("cardInfoId") Long cardInfoId);

}
