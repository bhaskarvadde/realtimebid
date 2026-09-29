package com.realtimebid.auction;

/*
 * Represents the current state of an auction.
 *
 * Using an enum ensures that an auction can have only
 * one of the predefined statuses.
 */
public enum AuctionStatus {

	/*
	 * The auction has been created but has not started yet.
	 *
	 * Example:
	 *
	 * Current time : 09:00 AM Auction start: 10:00 AM
	 *
	 * Status = SCHEDULED
	 *
	 * Users may be able to view the auction, but bidding should not be allowed yet.
	 */
	SCHEDULED,

	/*
	 * The auction has started and bidding is currently allowed.
	 *
	 * During this state: - Users can place bids. - Highest valid bid can change. -
	 * Real-time updates can be sent to watchers.
	 */
	ACTIVE,

	/*
	 * The auction's end time has been reached.
	 *
	 * New bids should no longer be accepted.
	 *
	 * The system can then determine: - Highest valid bid - Winner - Final auction
	 * result
	 */
	ENDED
}