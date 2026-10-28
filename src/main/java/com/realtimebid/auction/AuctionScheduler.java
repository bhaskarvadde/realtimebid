package com.realtimebid.auction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;	
import org.springframework.stereotype.Component;
			
import com.realtimebid.bid.Bid;
import com.realtimebid.bid.BidRepository;
import com.realtimebid.bid.dto.AuctionClosedEvent;

@Component
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AuctionScheduler(AuctionRepository auctionRepository, BidRepository bidRepository,
                             SimpMessagingTemplate messagingTemplate) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 30000) // every 30 seconds
    public void activateScheduledAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> toActivate = auctionRepository.findByStatusAndStartTimeBefore(AuctionStatus.SCHEDULED, now);

        for (Auction auction : toActivate) {
            auction.setStatus(AuctionStatus.ACTIVE);
            auctionRepository.save(auction);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void closeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> toClose = auctionRepository.findByStatusAndEndTimeBefore(AuctionStatus.ACTIVE, now);

        for (Auction auction : toClose) {
            List<Bid> bids = bidRepository.findByAuctionOrderByCreatedAtDesc(auction);
            Optional<Bid> winningBid = bids.stream().findFirst(); // newest-first; highest is always most recent due to our bid-validation rule

            auction.setStatus(AuctionStatus.ENDED);
            winningBid.ifPresent(bid -> auction.setWinner(bid.getBidder()));
            auctionRepository.save(auction);

            AuctionClosedEvent event = new AuctionClosedEvent();
            event.setAuctionId(auction.getId());
            event.setWinningBidderName(winningBid.map(b -> b.getBidder().getName()).orElse(null));
            event.setFinalPrice(auction.getCurrentHighestBid());
            messagingTemplate.convertAndSend("/topic/auctions/" + auction.getId(), event);
        }
    }
}