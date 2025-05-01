package com.fourclover.clobee.card.controller;

import com.fourclover.clobee.card.domain.CardBenefitDetail;
import com.fourclover.clobee.card.domain.CardListDTO;
import com.fourclover.clobee.card.domain.CardPageDTO;
import com.fourclover.clobee.card.domain.UserCardDetail;
import com.fourclover.clobee.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 카드 상세 보기 (+ 내 카드 상세 보기)
    // http://localhost:8080/api/card/getCardDetail?cardInfoId=2497
    @GetMapping("/getCardDetail")
    public List<CardBenefitDetail> getCardBenefit(@RequestParam Long cardInfoId) {
        return cardService.getCardBenefitDetail(cardInfoId);
    }

    // 카드 신청하기
    // http://localhost:8080/api/card/apply?cardInfoId=1470&cardBrand=302
    @GetMapping("/apply")
    public ResponseEntity<Void> applyCard(@RequestParam Long cardInfoId, @RequestParam int cardBrand) {
        String url = cardService.getCardBrandUrlAndIncreaseApplyViews(cardInfoId, cardBrand);
        return ResponseEntity.status(HttpStatus.FOUND)  // HttpStatus.FOUND는 302 Redirect를 의미
                .location(URI.create(url))
                .build();
    }

    // 내 카드 추가하기 - id를 저장하는 방식에 따라 수정됨
    // http://localhost:8080/api/card/addCard
    @PostMapping("/addCard")
    public ResponseEntity<String> addUserCard(@RequestBody UserCardDetail userCardDetail) {
        cardService.addUserCard(userCardDetail.getUserId(), userCardDetail.getCardInfoId());
        return ResponseEntity.ok("카드 등록 완료");
    }

    // 내 카드 리스트 불러오기
    // http://localhost:8080/api/card/getMyCardList?userId=1
    @GetMapping("/getMyCardList")
    public List<CardListDTO> getMyCardList(@RequestParam Long userId) {
        return cardService.getMyCardList(userId);
    }

    // 카드 검색 기능(
    // http://localhost:8080/api/card/search?cardName={cardName}
    @GetMapping("/search")
    public List<CardListDTO> searchCards(@RequestParam String cardName) {
        return cardService.searchCard(cardName);
    }


}


