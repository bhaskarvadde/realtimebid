package com.realtimebid.bid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BidEvent {
    private Long auctionId;
    private BigDecimal amount;
    private String bidderName;
    private LocalDateTime timestamp;
}