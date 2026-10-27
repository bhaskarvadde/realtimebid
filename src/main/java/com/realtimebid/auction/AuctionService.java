package com.realtimebid.auction;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.realtimebid.auction.dto.AuctionRequest;
import com.realtimebid.auction.dto.AuctionResponse;
import com.realtimebid.exception.AuctionNotFoundException;
import com.realtimebid.user.User;

@Service
public class AuctionService {
	private final AuctionRepository auctionRepository;

	public AuctionService(AuctionRepository auctionRepository) {
		this.auctionRepository = auctionRepository;
	}

	public AuctionResponse createAuction(AuctionRequest request, User seller) {
		Auction auction = new Auction();

		auction.setTitle(request.getTitle());
		auction.setDescription(request.getDescription());
		auction.setStartingPrice(request.getStartingPrice());
		auction.setReservePrice(request.getReservePrice());
		auction.setStartTime(request.getStartTime());
		auction.setEndTime(request.getEndTime());
		auction.setSeller(seller);
		auction.setStatus(AuctionStatus.SCHEDULED);

		Auction saved = auctionRepository.save(auction);
		return toResponse(saved);
	}
	
	public List<AuctionResponse> getActiveAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
	
	public AuctionResponse getAuctionById(Long id) {
		Auction auction = auctionRepository.findById(id)
				.orElseThrow(()-> new AuctionNotFoundException("Auction not found: " + id));
		
		return toResponse(auction);
	}
	
	 private AuctionResponse toResponse(Auction auction) {
	        AuctionResponse response = new AuctionResponse();
	        response.setId(auction.getId());
	        response.setTitle(auction.getTitle());
	        response.setDescription(auction.getDescription());
	        response.setStartingPrice(auction.getStartingPrice());
	        response.setReservePrice(auction.getReservePrice());
	        response.setStartTime(auction.getStartTime());
	        response.setEndTime(auction.getEndTime());
	        response.setStatus(auction.getStatus().name());
	        response.setSellerName(auction.getSeller().getName());
	        return response;
	 }
}
