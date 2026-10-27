package com.realtimebid.auction.dto;

import java.math.BigDecimal; 
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AuctionResponse {
	private Long id;
	private String title;
	private String description;
	private BigDecimal startingPrice;
	private BigDecimal reservePrice;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String status;
	private String sellerName;
}