package com.realtimebid.user;

/*
 * Enum representing the different roles that a user
 * can have in the RealTimeBid application.
 *
 * An enum is used when a field should have a fixed
 * set of allowed values.
 */
public enum Role {

	/*
	 * Normal application user.
	 *
	 * A USER can perform normal operations such as: - Register / Login - Create
	 * auction listings - Browse auctions - Place bids - View their auctions and
	 * bids
	 */
	USER,

	/*
	 * Administrator of the application.
	 *
	 * An ADMIN can perform administrative operations such as managing users,
	 * auctions, or other application-level resources.
	 */
	ADMIN
}