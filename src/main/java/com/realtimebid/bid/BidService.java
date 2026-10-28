package com.realtimebid.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtimebid.auction.Auction;
import com.realtimebid.auction.AuctionRepository;
import com.realtimebid.bid.dto.BidEvent;
import com.realtimebid.bid.dto.BidRequest;
import com.realtimebid.exception.AuctionNotFoundException;
import com.realtimebid.exception.InvalidBidException;
import com.realtimebid.user.User;

@Service
public class BidService {

    private static final int MAX_RETRIES = 3;
    private static final int ANTI_SNIPE_WINDOW_MINUTES = 2;
    private static final int EXTENSION_MINUTES = 2;

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Bid placeBid(BidRequest request, User bidder) {
        int attempts = 0;
        while (true) {
            try {
                Bid savedBid = doPlaceBid(request, bidder);

                BidEvent event = new BidEvent();
                event.setAuctionId(request.getAuctionId());
                event.setAmount(savedBid.getAmount());
                event.setBidderName(bidder.getName());
                event.setTimestamp(LocalDateTime.now());

                messagingTemplate.convertAndSend("/topic/auctions/" + request.getAuctionId(), event);

                return savedBid;
            } catch (OptimisticLockingFailureException e) {
                attempts++;
                if (attempts >= MAX_RETRIES) {
                    throw new InvalidBidException("Too many concurrent bids, please try again");
                }
            }
        }
    }

    @Transactional
    protected Bid doPlaceBid(BidRequest request, User bidder) {
        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + request.getAuctionId()));

        if (auction.getSeller().getId().equals(bidder.getId())) {
            throw new InvalidBidException("Cannot bid on your own auction");
        }

        BigDecimal floor = auction.getCurrentHighestBid() != null
                ? auction.getCurrentHighestBid()
                : auction.getStartingPrice();

        if (request.getAmount().compareTo(floor) <= 0) {
            throw new InvalidBidException("Bid must be higher than " + floor);
        }

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setAmount(request.getAmount());
        Bid savedBid = bidRepository.save(bid);

        auction.setCurrentHighestBid(request.getAmount());

        // Anti-sniping: if this bid landed within the final window before
        // closing, push endTime forward from NOW, giving others a fresh
        // chance to respond.
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime antiSnipeThreshold = auction.getEndTime().minusMinutes(ANTI_SNIPE_WINDOW_MINUTES);
        if (now.isAfter(antiSnipeThreshold)) {
            auction.setEndTime(now.plusMinutes(EXTENSION_MINUTES));
        }

        auctionRepository.save(auction);

        return savedBid;
    }

    public List<Bid> getBidsForAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + auctionId));
        return bidRepository.findByAuctionOrderByCreatedAtDesc(auction);
    }
}