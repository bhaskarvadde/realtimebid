package com.realtimebid.auction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realtimebid.user.User;

/*
 * Repository layer for the Auction entity.
 *
 * This interface is responsible for database operations
 * related to auctions.
 *
 * Spring Data JPA automatically provides the implementation,
 * so we don't need to write the implementation class or
 * SQL queries for these methods.
 */
public interface AuctionRepository extends JpaRepository<Auction, Long> {

	/*
	 * Finds all auctions having the given status.
	 *
	 * Spring Data JPA derives the query from the method name.
	 *
	 * Conceptually:
	 *
	 * SELECT * FROM auctions WHERE status = ?
	 *
	 * Example:
	 *
	 * findByStatus(AuctionStatus.ACTIVE)
	 *
	 * This can be used to retrieve all currently active auctions.
	 */
	List<Auction> findByStatus(AuctionStatus status);

	/*
	 * Finds all auctions created by a particular seller.
	 *
	 * Spring Data JPA automatically understands the "seller" field from the Auction
	 * entity.
	 *
	 * Conceptually:
	 *
	 * SELECT * FROM auctions WHERE seller_id = ?
	 *
	 * Example:
	 *
	 * findBySeller(user)
	 *
	 * This can be used when a user wants to see all the auctions they have created.
	 */
	List<Auction> findBySeller(User seller);
}