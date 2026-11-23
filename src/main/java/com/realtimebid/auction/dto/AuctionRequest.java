package com.realtimebid.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuctionRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 255, message = "Description must be 255 characters or fewer")
    private String description;

    @Size(max = 1000, message = "Image URL is too long")
    private String imageUrl;

    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.01", message = "Starting price must be positive")
    private BigDecimal startingPrice;

    private BigDecimal reservePrice;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
}