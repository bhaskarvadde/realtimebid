package com.realtimebid.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.realtimebid.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@Table(name = "auctions")

@Getter
@Setter

@NoArgsConstructor
public class Auction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String title;

	@Column
	private String description;

	@Column(precision = 10, scale = 2)
	private BigDecimal startingPrice;

	@Column(precision = 10, scale = 2)
	private BigDecimal reservePrice;

	@Column
	private LocalDateTime startTime;
	
	@Column
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	private AuctionStatus status;

	@ManyToOne
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	@Version
	private Long version;

	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(precision = 10, scale = 2)
	private BigDecimal currentHighestBid;
}