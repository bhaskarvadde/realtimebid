package com.realtimebid.bid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtimebid.auction.Auction;
import com.realtimebid.bid.dto.BidRequest;
import com.realtimebid.bid.dto.BidResponse;
import com.realtimebid.bid.dto.MyBidResponse;
import com.realtimebid.exception.InvalidCredentialsException;
import com.realtimebid.user.User;
import com.realtimebid.user.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class BidController {

    private final BidService bidService;
    private final UserRepository userRepository;

    public BidController(BidService bidService, UserRepository userRepository) {
        this.bidService = bidService;
        this.userRepository = userRepository;
    }

    @PostMapping("/bids")
    public ResponseEntity<BidResponse> placeBid(@Valid @RequestBody BidRequest request) {
        Bid bid = bidService.placeBid(request, currentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(bid));
    }

    @GetMapping("/bids/mine")
    public ResponseEntity<List<MyBidResponse>> getMyBids() {
        List<MyBidResponse> bids = bidService.getBidsForUser(currentUser()).stream()
                .map(this::toMyBidResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/auctions/{auctionId}/bids")
    public ResponseEntity<List<BidResponse>> getBidsForAuction(@PathVariable Long auctionId) {
        List<BidResponse> bids = bidService.getBidsForAuction(auctionId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bids);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    }

    private BidResponse toResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setAmount(bid.getAmount());
        response.setBidderName(bid.getBidder().getName());
        response.setCreatedAt(bid.getCreatedAt());
        return response;
    }

    private MyBidResponse toMyBidResponse(Bid bid) {
        Auction auction = bid.getAuction();
        MyBidResponse response = new MyBidResponse();
        response.setBidId(bid.getId());
        response.setAuctionId(auction.getId());
        response.setAuctionTitle(auction.getTitle());
        response.setAuctionStatus(auction.getStatus().name());
        response.setAuctionImageUrl(auction.getImageUrl());
        response.setAuctionEndTime(auction.getEndTime());
        response.setAmount(bid.getAmount());
        response.setCurrentHighestBid(auction.getCurrentHighestBid());
        response.setCreatedAt(bid.getCreatedAt());
        return response;
    }
}