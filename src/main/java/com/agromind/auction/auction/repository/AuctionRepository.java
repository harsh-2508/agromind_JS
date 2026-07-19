package com.agromind.auction.auction.repository;

import com.agromind.auction.auction.model.Auction;
import com.agromind.auction.auction.model.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
    //custom query to find all currently running actions
    List<Auction> findByStatus(AuctionStatus status);
}
