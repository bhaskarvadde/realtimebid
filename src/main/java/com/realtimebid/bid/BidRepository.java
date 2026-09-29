package com.realtimebid.bid;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realtimebid.auction.Auction;
import com.realtimebid.user.User;

/*
 * Repository layer for the Bid entity.
 *
 * This interface is responsible for database operations
 * related to bids.
 *
 * Spring Data JPA automatically provides the implementation,
 * so we don't need to write the implementation class.
 */
public interface BidRepository extends JpaRepository<Bid, Long> {

	/*
	 * Finds all bids belonging to a particular auction.
	 *
	 * "findByAuction" -> Search bids using the auction field.
	 *
	 * "OrderByCreatedAtDesc" -> Sort the results using createdAt in descending
	 * order.
	 *
	 * DESC means: Newest bid → Oldest bid
	 *
	 * Example:
	 *
	 * Bid 3 -> 10:30 AM Bid 2 -> 10:25 AM Bid 1 -> 10:20 AM
	 *
	 * This is useful for displaying the bidding history of an auction with the
	 * latest bid first.
	 *
	 * Conceptually:
	 *
	 * SELECT * FROM bids WHERE auction_id = ? ORDER BY created_at DESC;
	 */
	List<Bid> findByAuctionOrderByCreatedAtDesc(Auction auction);

	/*
	 * Finds all bids placed by a particular user.
	 *
	 * "findByBidder" -> Search bids using the bidder field.
	 *
	 * Example:
	 *
	 * User: Bhaskar
	 *
	 * findByBidder(bhaskar)
	 *
	 * returns all bids placed by Bhaskar.
	 *
	 * Conceptually:
	 *
	 * SELECT * FROM bids WHERE bidder_id = ?;
	 */
	List<Bid> findByBidder(User bidder);
}