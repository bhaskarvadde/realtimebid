package com.realtimebid.bid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.realtimebid.auction.Auction;
import com.realtimebid.auction.AuctionRepository;
import com.realtimebid.auction.AuctionStatus;
import com.realtimebid.bid.dto.BidRequest;
import com.realtimebid.exception.AuctionNotFoundException;
import com.realtimebid.exception.InvalidBidException;
import com.realtimebid.user.User;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

	@Mock private BidRepository bidRepository;
	@Mock private AuctionRepository auctionRepository;
	@Mock private SimpMessagingTemplate messagingTemplate;

	@InjectMocks private BidService bidService;

	private User seller;
	private User bidder;
	private Auction auction;

	@BeforeEach
	void setUp() {
		seller = new User();
		seller.setId(1L);

		bidder = new User();
		bidder.setId(2L);

		auction = new Auction();
		auction.setId(100L);
		auction.setSeller(seller);
		auction.setStartingPrice(new BigDecimal("100"));
		auction.setEndTime(LocalDateTime.now().plusDays(1)); // far from anti-snipe window by default
		auction.setStatus(AuctionStatus.ACTIVE);
	}

	@Test
	void bidTooLow_throwsInvalidBidException() {
		when(auctionRepository.findById(100L)).thenReturn(Optional.of(auction));

		BidRequest request = new BidRequest();
		request.setAuctionId(100L);
		request.setAmount(new BigDecimal("50"));

		assertThrows(InvalidBidException.class, () -> bidService.placeBid(request, bidder));
	}

	@Test
	void sellerBiddingOnOwnAuction_throwsInvalidBidException() {
		when(auctionRepository.findById(100L)).thenReturn(Optional.of(auction));

		BidRequest request = new BidRequest();
		request.setAuctionId(100L);
		request.setAmount(new BigDecimal("150"));

		assertThrows(InvalidBidException.class, () -> bidService.placeBid(request, seller));
	}

	@Test
	void auctionDoesNotExist_throwsAuctionNotFoundException() {
		when(auctionRepository.findById(999L)).thenReturn(Optional.empty());

		BidRequest request = new BidRequest();
		request.setAuctionId(999L);
		request.setAmount(new BigDecimal("150"));

		assertThrows(AuctionNotFoundException.class, () -> bidService.placeBid(request, bidder));
	}

	@Test
	void validBid_savesAndReturnsIt() {
		when(auctionRepository.findById(100L)).thenReturn(Optional.of(auction));
		when(bidRepository.save(ArgumentMatchers.any(Bid.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		BidRequest request = new BidRequest();
		request.setAuctionId(100L);
		request.setAmount(new BigDecimal("150"));

		Bid result = bidService.placeBid(request, bidder);

		assertEquals(new BigDecimal("150"), result.getAmount());
	}

	@Test
	void bidNearClosing_extendsEndTime() {
		LocalDateTime soonToClose = LocalDateTime.now().plusSeconds(60); // inside the 2-min anti-snipe window
		auction.setEndTime(soonToClose);

		when(auctionRepository.findById(100L)).thenReturn(Optional.of(auction));
		when(bidRepository.save(ArgumentMatchers.any(Bid.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		BidRequest request = new BidRequest();
		request.setAuctionId(100L);
		request.setAmount(new BigDecimal("150"));

		bidService.placeBid(request, bidder);

		assertTrue(auction.getEndTime().isAfter(soonToClose));
	}

	@Test
	void bidFarFromClosing_doesNotExtendEndTime() {
		LocalDateTime farFromClosing = LocalDateTime.now().plusDays(1); // well outside the window
		auction.setEndTime(farFromClosing);

		when(auctionRepository.findById(100L)).thenReturn(Optional.of(auction));
		when(bidRepository.save(ArgumentMatchers.any(Bid.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		BidRequest request = new BidRequest();
		request.setAuctionId(100L);
		request.setAmount(new BigDecimal("150"));

		bidService.placeBid(request, bidder);

		assertEquals(farFromClosing, auction.getEndTime());
	}
}