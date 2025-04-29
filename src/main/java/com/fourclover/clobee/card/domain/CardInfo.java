package com.fourclover.clobee.card.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
    private Long card_info_id;
    private int card_rank;
    private String card_name;
    private String card_brand;
    private int card_domestic_annual_fee;
    private int card_global_annual_fee;
    private Date card_expiry_date;
    private int card_type;
    private int card_image_url;
    private Timestamp created_at;
    private Timestamp updated_at;

}


