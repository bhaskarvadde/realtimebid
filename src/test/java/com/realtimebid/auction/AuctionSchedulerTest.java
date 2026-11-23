package com.realtimebid.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.realtimebid.auction.Auction;
import com.realtimebid.auction.AuctionRepository;
import com.realtimebid.auction.AuctionScheduler;
import com.realtimebid.auction.AuctionStatus;
import com.realtimebid.bid.Bid;
import com.realtimebid.bid.BidRepository;
import com.realtimebid.user.User;

@ExtendWith(MockitoExtension.class)
class AuctionSchedulerTest {

	@Mock private AuctionRepository auctionRepository;
	@Mock private BidRepository bidRepository;
	@Mock private SimpMessagingTemplate messagingTemplate;

	@InjectMocks private AuctionScheduler scheduler;

	@Test
	void closingAuction_picksHighestMostRecentBidAsWinner() {
		User winner = new User();
		winner.setName("Alice");

		Auction auction = new Auction();
		auction.setId(1L);
		auction.setStatus(AuctionStatus.ACTIVE);
		auction.setCurrentHighestBid(new BigDecimal("200"));

		Bid topBid = new Bid();
		topBid.setBidder(winner);
		topBid.setAmount(new BigDecimal("200"));

		when(auctionRepository.findByStatusAndEndTimeBefore(any(), any()))
				.thenReturn(List.of(auction));
		when(bidRepository.findByAuctionOrderByCreatedAtDesc(auction))
				.thenReturn(List.of(topBid));

		scheduler.closeExpiredAuctions();

		assertEquals(AuctionStatus.ENDED, auction.getStatus());
		assertEquals("Alice", auction.getWinner().getName());
	}
}