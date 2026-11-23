package com.realtimebid.bid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MyBidResponse {
    private Long bidId;
    private Long auctionId;
    private String auctionTitle;
    private String auctionStatus;
    private String auctionImageUrl;
    private LocalDateTime auctionEndTime;
    private BigDecimal amount;
    private BigDecimal currentHighestBid;
    private LocalDateTime createdAt;
}