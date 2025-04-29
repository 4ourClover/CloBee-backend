package com.fourclover.clobee.card.controller;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardPageDTO;
import com.fourclover.clobee.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    // 카드 조회
    // http://localhost:8080/api/card/getCardList?cardType=check&page=2&size=10
    @GetMapping("/getCardList")
    public CardPageDTO getCardPage(@RequestParam(defaultValue = "credit") String cardType,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return cardService.getCardPage(cardType, page, size);
    }

    // 카드 상세 보기
    // http://localhost:8080/api/card/getCardDetail?card_info_id=2497
    @GetMapping("/getCardDetail")
    public List<CardBenefitDetail> getCardBenefit(@RequestParam("card_info_id") Long cardInfoId) {
        return cardService.getCardBenefitDetail(cardInfoId);
    }

}


