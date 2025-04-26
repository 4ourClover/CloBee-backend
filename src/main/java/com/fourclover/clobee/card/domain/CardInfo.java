package com.fourclover.clobee.card.domain;

import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
public class CardInfo {
    private Long card_info_id;
    private String card_name;
    private String card_brand;
    private int card_annual_fee;
    private Date card_expiry_date;
    private int card_type;
    private int card_image_url;
    private Timestamp created_at;
    private Timestamp updated_at;
}
