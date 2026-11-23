package com.realtimebid.auction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realtimebid.user.User;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByStatus(AuctionStatus status);

    List<Auction> findBySeller(User seller);

    List<Auction> findByStatusAndStartTimeBefore(AuctionStatus status, LocalDateTime time);

    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime time);

    List<Auction> findAllByOrderByCreatedAtDesc();

    List<Auction> findBySellerOrderByCreatedAtDesc(User seller);

    List<Auction> findByWinnerOrderByEndTimeDesc(User winner);
}