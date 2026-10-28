package com.realtimebid.auction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.realtimebid.user.User;

import jakarta.persistence.LockModeType;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Auction a WHERE a.id = :id")
	Optional<Auction> findByIdForUpdate(@Param("id") Long id);

	List<Auction> findByStatus(AuctionStatus status);

	List<Auction> findBySeller(User seller);

	List<Auction> findByStatusAndStartTimeBefore(AuctionStatus status, LocalDateTime time);

	List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime time);
}
