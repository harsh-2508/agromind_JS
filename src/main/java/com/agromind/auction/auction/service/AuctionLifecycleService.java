package com.agromind.auction.auction.service;

import com.agromind.auction.auction.model.Auction;
import com.agromind.auction.auction.model.AuctionStatus;
import com.agromind.auction.auction.model.Bid;
import com.agromind.auction.auction.repository.AuctionRepository;
import com.agromind.auction.user.model.User;
import com.agromind.auction.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionLifecycleService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BiddingEngineService biddingEngineService;

//    phase 1) waking up the database and the RAM
    @Transactional
    public void activateAuction(Long auctionId){

        Auction auction=auctionRepository.findById(auctionId).orElseThrow(()-> new RuntimeException("Auction not found in postgresql"));

        if(auction.getStatus()!= AuctionStatus.PENDING){ throw new RuntimeException("Only PENDING auctions can be started!");
        }

//        1. update db status
        auction.setStatus(AuctionStatus.ACTIVE);
        auctionRepository.save(auction);

//        2.fire up the max-heap in ram!
        biddingEngineService.startAuction(auctionId);
    }

//    phase 2) dropping the gavel and saving permanently

    @Transactional
    public Auction finalizeAuction(Long auctionId){
//        1.pull the auction from the db
        Auction auction=auctionRepository.findById(auctionId).orElseThrow(()->new RuntimeException("Auction not found"));

//        2.shut down the max-heap and pull the ultimate winner from RAM
        Bid winningBid=biddingEngineService.closeAuction(auctionId);

//        3.Save the results permanently
        if(winningBid!=null){
            User winner=userRepository.findById(winningBid.getBuyerId()).orElse(null);
            auction.setWinningBuyer(winner);
            auction.setWinningBidAmount(winningBid.getBidAmount());
        }
        auction.setStatus(AuctionStatus.COMPLETED);

//        return the saved, finalized database record
        return auctionRepository.save(auction);

    }
}
