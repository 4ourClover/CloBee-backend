package com.fourclover.clobee.card.controller;

import com.fourclover.clobee.card.domain.*;
import com.fourclover.clobee.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 카드 신청하기 URL 반환 (리다이렉트 대신 문자열 반환)
    @GetMapping("/apply")
    public ResponseEntity<String> applyCard(@RequestParam Long cardInfoId, @RequestParam int cardBrand) {
        String url = cardService.getCardBrandUrlAndIncreaseApplyViews(cardInfoId, cardBrand);
        return ResponseEntity.ok(url);
    }

    // 내 카드 추가하기 - id를 저장하는 방식에 따라 수정됨
    // http://localhost:8080/api/card/addCard
    @PostMapping("/addCard")
    public ResponseEntity<String> addUserCard(@RequestBody UserCardDetail userCardDetail) {
        cardService.addUserCard(userCardDetail.getUserId(), userCardDetail.getCardInfoId() , userCardDetail.getUserCardType());
        return ResponseEntity.ok("카드 등록 완료");
    }

    // 내 카드 리스트 불러오기
    // http://localhost:8080/api/card/getMyCardList?userId=1
    @GetMapping("/getMyCardList")
    public List<UserCardListDTO> getMyCardList(@RequestParam Long userId) {
        return cardService.getMyCardList(userId);
    }

    // 카드 검색 기능(
    // http://localhost:8080/api/card/search?cardName={cardName}
    @GetMapping("/search")
    public List<CardListDTO> searchCards(@RequestParam String cardName) {
        return cardService.searchCard(cardName);
    }

    // 카드 실적 추가 및 업데이트
    // http://localhost:8080/api/card/addPerformance
    @PostMapping("/addPerformance")
    public ResponseEntity<String> addPerformance(@RequestBody UserCardPerformanceDetail detail) {
        cardService.addPerformance(detail);
        return ResponseEntity.ok("실적이 반영되었습니다.");
    }

    // 카드 실적 가져오기
    // http://localhost:8080/api/card/getPerformance?userCardId=1&year=2025&month=5
    @GetMapping("/getPerformance")
    public ResponseEntity<UserCardPerformanceDetail> getPerformance(
            @RequestParam Long userCardId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(cardService.getPerformance(userCardId, year, month));
    }

}


