package com.fourclover.clobee.card.controller;

import com.fourclover.clobee.card.domain.*;
import com.fourclover.clobee.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public List<CardListDTO> getMyCardList(@RequestParam Long userId) {
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

    // 내 카드에 추가된 카드 삭제하기
    @DeleteMapping("/delCard")
    public ResponseEntity<String> deleteUserCard(@RequestParam Long userId,
                                                 @RequestParam Long cardInfoId) {
        cardService.deleteUserCard(userId, cardInfoId);
        return ResponseEntity.ok("카드가 삭제되었습니다.");
    }

    // 혜택매장 찾기
    @GetMapping("/benefit-stores")
    public ResponseEntity<List<String>> getBenefitStores(@RequestParam Long userId) {
        System.out.println("혜택매장요청");
        return ResponseEntity.ok(cardService.getBenefitStoresByUserId(userId));
    }

    @GetMapping("/benefit-stores-brand")
    public ResponseEntity<Map<String, List<String>>> getCardBrand(@RequestParam Long userId) {
        System.out.println("혜택매장요청: " + userId);
        return ResponseEntity.ok(cardService.getCardBrandByUserId(userId));
    }

    // 매장별 내 카드 혜택을 지도로 보내기
    // http://localhost:8080/api/card/mapMyBenefits?userId=11&cardBenefitStore=스타벅스
    @GetMapping("/mapMyBenefits")
    public ResponseEntity<List<CardBenefitDetail>> getCardBenefits(
            @RequestParam int userId,
            @RequestParam String cardBenefitStore
    ) {
        List<CardBenefitDetail> result = cardService.getCardBenefitsSortedByDiscount(userId, cardBenefitStore);
        return ResponseEntity.ok(result);
    }

    // 매장별 추천 카드를 지도로 보내기
    // http://localhost:8080/api/card/recommendCard?cardBenefitStore=스타벅스
    @GetMapping("/recommendCard")
    public ResponseEntity<List<CardBenefitDetail>> getRecommendations(@RequestParam String cardBenefitStore) {
        return ResponseEntity.ok(cardService.getRecommendedCards(cardBenefitStore));
    }

}


