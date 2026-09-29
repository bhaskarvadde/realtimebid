package com.realtimebid.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.realtimebid.auction.Auction;
import com.realtimebid.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * @Entity
 *
 * Marks this class as a JPA entity.
 *
 * Hibernate will map this Java class to a database table.
 *
 * Java class:
 * Bid
 *
 * Database table:
 * bids
 */
@Entity

/*
 * Specifies the database table name explicitly.
 */
@Table(name = "bids")

/*
 * Lombok automatically generates getter methods for all fields.
 */
@Getter

/*
 * Lombok automatically generates setter methods for all fields.
 */
@Setter

/*
 * Generates a no-argument constructor.
 *
 * JPA requires a no-argument constructor to create entity objects when reading
 * data from the database.
 */
@NoArgsConstructor
public class Bid {

	/*
	 * Primary key of the bids table.
	 */
	@Id

	/*
	 * The database automatically generates the ID whenever a new bid is inserted.
	 *
	 * Example:
	 *
	 * First bid -> id = 1 Second bid -> id = 2
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/*
	 * Amount offered by the bidder.
	 *
	 * BigDecimal is used because this field represents money.
	 *
	 * precision = 10 -> Maximum total number of digits.
	 *
	 * scale = 2 -> Maximum 2 digits after the decimal point.
	 *
	 * Example:
	 *
	 * 15000.00 15550.50
	 */
	@Column(precision = 10, scale = 2)
	private BigDecimal amount;

	/*
	 * Many bids can belong to one auction.
	 *
	 * Example:
	 *
	 * Auction: MacBook Pro │ ├── Bid 1 → ₹50,000 ├── Bid 2 → ₹52,000 ├── Bid 3 →
	 * ₹55,000 └── Bid 4 → ₹58,000
	 *
	 * Therefore:
	 *
	 * Many Bids -> One Auction
	 */
	@ManyToOne

	/*
	 * Creates a foreign-key column called auction_id in the bids table.
	 *
	 * auction_id points to the ID of the corresponding auction in the auctions
	 * table.
	 *
	 * nullable = false -> Every bid must belong to an auction.
	 */
	@JoinColumn(name = "auction_id", nullable = false)
	private Auction auction;

	/*
	 * Many bids can be placed by one user.
	 *
	 * Example:
	 *
	 * User: Bhaskar │ ├── Bid 1 ├── Bid 2 └── Bid 3
	 *
	 * Therefore:
	 *
	 * Many Bids -> One User
	 */
	@ManyToOne

	/*
	 * Creates a foreign-key column called bidder_id in the bids table.
	 *
	 * bidder_id points to the ID of the User who placed the bid.
	 *
	 * nullable = false -> Every bid must have a bidder.
	 */
	@JoinColumn(name = "bidder_id", nullable = false)
	private User bidder;

	/*
	 * Automatically stores the date and time when the bid is created.
	 *
	 * We don't need to manually set this value.
	 *
	 * Example:
	 *
	 * 2026-09-26T19:30:15
	 */
	@CreationTimestamp
	private LocalDateTime createdAt;
}