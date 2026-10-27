package com.realtimebid.exception;

public class AuctionNotFoundException extends RuntimeException {
	public AuctionNotFoundException(String message) {
		super(message);
	}
}