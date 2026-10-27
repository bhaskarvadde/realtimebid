package com.realtimebid.bid;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtimebid.auction.Auction;
import com.realtimebid.auction.AuctionRepository;
import com.realtimebid.bid.dto.BidRequest;
import com.realtimebid.exception.AuctionNotFoundException;
import com.realtimebid.exception.InvalidBidException;
import com.realtimebid.user.User;

@Service
public class BidService {

    private static final int MAX_RETRIES = 3;

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    public Bid placeBid(BidRequest request, User bidder) {
        int attempts = 0;
        while (true) {
            try {
                return doPlaceBid(request, bidder);
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
        auctionRepository.save(auction);

        return savedBid;
    }
    
    public List<Bid> getBidsForAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + auctionId));
        return bidRepository.findByAuctionOrderByCreatedAtDesc(auction);
    }
}