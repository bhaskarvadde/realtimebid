package com.realtimebid.bid.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AuctionClosedEvent {
    private Long auctionId;
    private String winningBidderName;
    private BigDecimal finalPrice;
}