//package com.realtimebid.bid.dto;
//
//import java.math.BigDecimal; 
//
//import lombok.Data;
//
//@Data
//public class BidRequest {
//    private Long auctionId;
//    private BigDecimal amount;
//}

package com.realtimebid.bid.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BidRequest {
    @NotNull(message = "Auction id is required")
    private Long auctionId;

    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "0.01", message = "Bid amount must be positive")
    private BigDecimal amount;
}