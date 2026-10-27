package com.realtimebid.bid.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BidRequest {
    private Long auctionId;
    private BigDecimal amount;
}