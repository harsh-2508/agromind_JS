package com.agromind.auction.auction.controller;

import com.agromind.auction.auction.model.Bid;
import com.agromind.auction.auction.service.BiddingEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final BiddingEngineService biddingEngineService;



    // 1. Initialize the Heap for a specific crop listing
    @PostMapping("/{auctionId}/start")
    public ResponseEntity<String>  startAuction(@PathVariable Long auctionId){
        biddingEngineService.startAuction(auctionId);
        return ResponseEntity.ok("Auction "+auctionId+" started successfully.Ready for bids!");
    }



    // 2. place a bid(O(log n) insertion)
    @PostMapping("/bid")
    public ResponseEntity<String> placeBid(@RequestBody Bid incomingBid){
        // PRO TIP: In a real enterprise app, you would extract the buyerId from the JWT context.
        // We also stamp the exact server time here so buyers can't hack their computer clocks to cheat tie-breakers.
        incomingBid.setTimestamp(Instant.now());

        boolean accepted= biddingEngineService.placeBid(incomingBid);
        if(accepted){
            return ResponseEntity.ok("Bid accepted into the Max-Heap");
        }
        else{
            return ResponseEntity.badRequest().body("Bid rejected.you must bid higher than the current maximum");
        }
    }



    // 3. Get Current Highest Bid (O(1) Instant Lookup)
    @GetMapping("/{auctionId}/highest")
    public ResponseEntity<Bid> getHighestBid(@PathVariable Long auctionId){
        Bid highestBid=biddingEngineService.getHighestBid(auctionId);
        if(highestBid==null){
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok(highestBid);
        }
    }



    // 4. Close the Auction and Announce Winner
    @PostMapping("/{auctionId}/close")
    public ResponseEntity<String> closeAuction(@PathVariable Long auctionId){
        Bid winningBid=biddingEngineService.closeAuction(auctionId);
        if(winningBid==null){
            return ResponseEntity.ok("Auction "+auctionId+" closed with no bids.");
        }
        return ResponseEntity.ok("Auction closed! Winner is Buyer "+winningBid.getBuyerId()+" with a massive bid for Rs."+winningBid.getBidAmount());
    }



}
