package com.realtimebid.bid;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realtimebid.auction.Auction;
import com.realtimebid.user.User;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionOrderByCreatedAtDesc(Auction auction);

    List<Bid> findByBidder(User bidder);

    List<Bid> findByBidderOrderByCreatedAtDesc(User bidder);

    long countByAuction(Auction auction);
}