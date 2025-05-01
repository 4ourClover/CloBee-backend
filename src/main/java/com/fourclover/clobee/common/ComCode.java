package com.fourclover.clobee.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ComCode {
    // 100 - 로그인
    LOGIN_KAKAO(101, "카카오"),
    LOGIN_EMAIL(102, "이메일"),

    // 200 - 매장 카테고리
    STORE_RESTAURANT(201, "음식점"),
    STORE_CAFE(202, "카페"),
    STORE_CONVENIENCE(203, "편의점"),
    STORE_MART(204, "마트"),
    STORE_GAS_STATION(205, "주유소"),
    STORE_CINEMA(206, "영화관"),

    // 300 - 카드사 종류
    CARD_WOORI(301, "우리카드"),
    CARD_SHINHAN(302, "신한카드"),
    CARD_IBK(303, "IBK기업은행"),
    CARD_KB(304, "KB국민카드"),
    CARD_HANA(305, "하나카드"),
    CARD_SAMSUNG(306, "삼성카드"),
    CARD_HYUNDAI(307, "현대카드"),
    CARD_LOTTE(308, "롯데카드"),
    CARD_NH(309, "NH농협카드"),
    CARD_BC(310, "BC 바로카드"),
    CARD_CITI(311, "씨티카드"),
    CARD_KG_MOBILIANS(312, "KG모빌리언스"),
    CARD_MG(313, "MG새마을금고"),
    CARD_POST(314, "우체국"),
    CARD_KAKAO_BANK(315, "카카오뱅크"),
    CARD_KBANK(316, "케이뱅크"),
    CARD_KAKAO_PAY(317, "카카오페이"),
    CARD_NAVER_PAY(318, "네이버페이"),
    CARD_TOSS_PAY(319, "토스페이"),
    CARD_TOSS_BANK(320, "토스뱅크"),
    CARD_HYUNDAI_DEPT(321, "현대백화점"),
    CARD_KWANGJU(322, "광주은행"),
    CARD_SHINHYEOP(323, "신협"),
    CARD_JEJU(324, "제주은행"),
    CARD_BNK_BUSAN(325, "BNK부산은행"),
    CARD_BNK_GYEONGNAM(326, "BNK경남은행"),
    CARD_SH_SUHYUP(327, "Sh수협은행"),
    CARD_SSGPAY(328, "SSGPAY. CARD"),
    CARD_IM_BANK(329, "iM뱅크"),
    CARD_JEONBUK(330, "전북은행"),
    CARD_SC(331, "SC제일은행"),
    CARD_CHAI(332, "차이"),
    CARD_PAYCO(333, "엔에이치엔페이코"),
    CARD_KB_SECURITIES(334, "KB증권"),
    CARD_MIRAE(335, "미래에셋증권"),
    CARD_KONA(336, "코나카드"),
    CARD_TRAVEL_WALLET(337, "트래블월렛"),
    CARD_KDB(338, "KDB산업은행"),
    CARD_KOREA_INVEST(339, "한국투자증권"),
    CARD_HANPASS(340, "한패스"),
    CARD_DB(341, "DB금융투자"),
    CARD_NH_INVEST(342, "NH투자증권"),
    CARD_SBI(343, "SBI저축은행"),
    CARD_YUANTA(344, "유안타증권"),
    CARD_EUGENE(345, "유진투자증권"),
    CARD_TOSS(346, "토스"),
    CARD_FINCK(347, "핀크카드"),
    CARD_SK(348, "SK증권"),
    CARD_DANAL(349, "다날"),
    CARD_MONEYTREE(350, "머니트리"),
    CARD_KYOBO(351, "교보증권"),
    CARD_IORORA(352, "아이오로라"),
    CARD_FINT(353, "핀트"),

    // 400 - 카드 종류
    CARD_TYPE_CREDIT(401, "신용카드"),
    CARD_TYPE_CHECK(402, "체크카드"),
    CARD_TYPE_PAY(403, "페이"),

    // 500 - 카드 혜택 카테고리
    BENEFIT_TRANSPORT(501, "대중교통"),
    BENEFIT_DEPARTMENT_STORE(502, "백화점"),
    BENEFIT_RESTAURANT(503, "음식점"),

    // 600 - 이벤트 종류
    CARD_EVENT(601, "카드 이벤트"),
    ATTEND_EVENT(602, "출석 이벤트"),
    INVITE_EVENT(603, "친구 초대 이벤트"),
    CLOVER_FIND_EVENT(604, "클로버 찾기 이벤트"),
    CLOVER_FILL_EVENT(605, "클로버 채우기 이벤트"),

    // 700 - 이벤트 진행 상태
    EVENT_BEFORE(701, "진행 전"),
    EVENT_ONGOING(702, "진행 중"),
    EVENT_EXPIRED(703, "만료");

    private int codeId;
    private String message;
}
