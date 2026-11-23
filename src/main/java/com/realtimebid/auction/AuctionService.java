package com.realtimebid.auction;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.realtimebid.auction.dto.AuctionRequest;
import com.realtimebid.auction.dto.AuctionResponse;
import com.realtimebid.bid.BidRepository;
import com.realtimebid.exception.AuctionNotFoundException;
import com.realtimebid.user.User;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public AuctionService(AuctionRepository auctionRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
    }

    public AuctionResponse createAuction(AuctionRequest request, User seller) {
        Auction auction = new Auction();
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setImageUrl(request.getImageUrl());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setReservePrice(request.getReservePrice());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setSeller(seller);
        auction.setStatus(AuctionStatus.SCHEDULED);

        return toResponse(auctionRepository.save(auction));
    }

    public List<AuctionResponse> getActiveAuctions() {
        return toResponses(auctionRepository.findByStatus(AuctionStatus.ACTIVE));
    }

    public List<AuctionResponse> getAllAuctions() {
        return toResponses(auctionRepository.findAllByOrderByCreatedAtDesc());
    }

    public List<AuctionResponse> getAuctionsBySeller(User seller) {
        return toResponses(auctionRepository.findBySellerOrderByCreatedAtDesc(seller));
    }

    public List<AuctionResponse> getWonAuctions(User winner) {
        return toResponses(auctionRepository.findByWinnerOrderByEndTimeDesc(winner));
    }

    public AuctionResponse getAuctionById(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found: " + id));
        return toResponse(auction);
    }

    private List<AuctionResponse> toResponses(List<Auction> auctions) {
        return auctions.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private AuctionResponse toResponse(Auction auction) {
        AuctionResponse response = new AuctionResponse();
        response.setId(auction.getId());
        response.setTitle(auction.getTitle());
        response.setDescription(auction.getDescription());
        response.setImageUrl(auction.getImageUrl());
        response.setStartingPrice(auction.getStartingPrice());
//        response.setReservePrice(auction.getReservePrice());
        response.setCurrentHighestBid(auction.getCurrentHighestBid());
        response.setBidCount(bidRepository.countByAuction(auction));
        response.setStartTime(auction.getStartTime());
        response.setEndTime(auction.getEndTime());
        response.setStatus(auction.getStatus().name());
        response.setSellerName(auction.getSeller().getName());
        response.setWinnerName(auction.getWinner() != null ? auction.getWinner().getName() : null);
        return response;
    }
}