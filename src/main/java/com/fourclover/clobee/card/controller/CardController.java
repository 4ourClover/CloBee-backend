package com.fourclover.clobee.card.controller;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardPageDTO;
import com.fourclover.clobee.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
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

    // 카드 신청하기
    // http://localhost:8080/api/card/apply?card_info_id=1470&card_brand=302
    @GetMapping("/apply")
    public ResponseEntity<Void> applyCard(@RequestParam("card_info_id") Long cardInfoId, @RequestParam("card_brand") int cardBrand) {
        String url = cardService.getCardBrandUrlAndIncreaseApplyViews(cardInfoId, cardBrand);
        return ResponseEntity.status(HttpStatus.FOUND)  // HttpStatus.FOUND는 302 Redirect를 의미
                .location(URI.create(url))
                .build();
    }
//    @GetMapping("/apply")
//    public String applyCard(@RequestParam("card_info_id") Long cardInfoId, @RequestParam("card_brand") int cardBrand) {
//
//        return cardService.getCardBrandUrlAndIncreaseApplyViews(cardInfoId, cardBrand);
//    }

}


