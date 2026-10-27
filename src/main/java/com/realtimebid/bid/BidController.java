package com.realtimebid.bid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtimebid.exception.InvalidCredentialsException;
import com.realtimebid.bid.dto.BidRequest;
import com.realtimebid.user.User;
import com.realtimebid.user.UserRepository;

import java.util.List;

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
    public ResponseEntity<Bid> placeBid(@RequestBody BidRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User bidder = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        Bid bid = bidService.placeBid(request, bidder);
        return ResponseEntity.status(HttpStatus.CREATED).body(bid);
    }

    @GetMapping("/auctions/{auctionId}/bids")
    public ResponseEntity<List<Bid>> getBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getBidsForAuction(auctionId));
    }
}