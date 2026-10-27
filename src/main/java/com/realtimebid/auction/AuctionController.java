package com.realtimebid.auction;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtimebid.auction.dto.AuctionRequest;
import com.realtimebid.auction.dto.AuctionResponse;
import com.realtimebid.exception.InvalidCredentialsException;
import com.realtimebid.user.User;
import com.realtimebid.user.UserRepository;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

	private final AuctionService auctionService;
	private final UserRepository userRepository;

	public AuctionController(AuctionService auctionService, UserRepository userRepository) {
		this.auctionService = auctionService;
		this.userRepository = userRepository;
	}

	@PostMapping
	public ResponseEntity<AuctionResponse> createAuction(@RequestBody AuctionRequest request) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User seller = userRepository.findByEmail(email)
				.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

		AuctionResponse response = auctionService.createAuction(request, seller);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<AuctionResponse>> getActiveAuctions() {
		return ResponseEntity.ok(auctionService.getActiveAuctions());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AuctionResponse> getAuctionById(@PathVariable Long id) {
		return ResponseEntity.ok(auctionService.getAuctionById(id));
	}
}