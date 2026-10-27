package com.realtimebid.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.realtimebid.auction.Auction;
import com.realtimebid.auction.AuctionRepository;
import com.realtimebid.auction.AuctionStatus;
import com.realtimebid.bid.dto.BidRequest;
import com.realtimebid.user.Role;
import com.realtimebid.user.User;
import com.realtimebid.user.UserRepository;

@SpringBootTest
class BidConcurrencyTest {

    @Autowired private BidService bidService;
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void concurrentBidsShouldNotLoseUpdates() throws InterruptedException {
        long ts = System.currentTimeMillis();

        User seller = new User();
        seller.setEmail("seller_" + ts + "@test.com");
        seller.setPassword("x");
        seller.setName("Seller");
        seller.setRole(Role.USER);
        userRepository.save(seller);

        Auction auction = new Auction();
        auction.setTitle("Concurrency Test Item");
        auction.setStartingPrice(new BigDecimal("100"));
        auction.setStatus(AuctionStatus.SCHEDULED);
        auction.setSeller(seller);
        auctionRepository.save(auction);

        int threadCount = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final int bidAmount = 100 + i; // 101, 102, ... 110
            pool.submit(() -> {
                try {
                    User bidder = new User();
                    bidder.setEmail("bidder" + bidAmount + "_" + ts + "@test.com");
                    bidder.setPassword("x");
                    bidder.setName("Bidder" + bidAmount);
                    bidder.setRole(Role.USER);
                    userRepository.save(bidder);

                    BidRequest request = new BidRequest();
                    request.setAuctionId(auction.getId());
                    request.setAmount(new BigDecimal(bidAmount));

                    bidService.placeBid(request, bidder);
                } catch (Exception e) {
                    System.out.println("Bid failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();

        Auction result = auctionRepository.findById(auction.getId()).orElseThrow();
        System.out.println("Final currentHighestBid: " + result.getCurrentHighestBid());
        System.out.println("Final version: " + result.getVersion());

        assertEquals(0, new BigDecimal("110").compareTo(result.getCurrentHighestBid()));
    }
}
