package com.fourclover.clobee.card.repository;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardListDTO;
import com.fourclover.clobee.card.domain.UserCardDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CardRepository {
    // 전체 카드 리스트를 가져옴(offset : 몇 번째 카드 부터, size : 몇 개의 카드를 보여줄 지)
    List<CardListDTO> allCardPaging(@Param("cardType") int cardType, @Param("offset") int offset, @Param("size") int size);

    // 전체 카드 개수
    int allCardCount(@Param("cardType") int cardType);

    // 카드 혜택
    List<CardBenefitDetail> getCardBenefit(@Param("cardInfoId") Long cardInfoId);

    // 카드사 url 가져오기
    String getCardBrandURL(@Param("cardBrand") int cardBrand);

    // 카드 신청하기 버튼을 누를 때 마다 card_apply_views 증가
    void updateApplyViews(@Param("cardInfoId") Long cardInfoId);

    // 사용자의 카드 추가
    void insertUserCard(UserCardDetail userCardDetail);

    // 특정 카드 정보 가져오기
    CardListDTO findByCardInfoId(@Param("cardInfoId") Long cardInfoId);

    // 내 카드 리스트 가져오기
    List<CardListDTO> getMyCard(@Param("userId") Long userId);

    // 카드 검색
    List<CardListDTO> searchCard(@Param("cardName") String cardName);
}