package com.fourclover.clobee.card.service;

import com.fourclover.clobee.card.domain.CardPageDTO;

public interface CardService {
    CardPageDTO getCardPage(String type, int page, int size);

}