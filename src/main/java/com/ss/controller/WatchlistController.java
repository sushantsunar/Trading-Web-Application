package com.ss.controller;

import com.ss.model.Coin;
import com.ss.model.User;
import com.ss.model.Watchlist;
import com.ss.service.CoinService;
import com.ss.service.UserService;
import com.ss.service.WatchlistService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;
    @GetMapping("/user")
   public ResponseEntity<Watchlist> getUserWatchlist(
           @RequestHeader("Authorization")
           String jwt    ) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        Watchlist watchlist = watchlistService.findUserWatchList(user.getId());
        return ResponseEntity.ok(watchlist);

    }

        @GetMapping("/{watchlistId}")
        public ResponseEntity<Watchlist> getWatchlistById(
                @PathVariable Long watchlistId) throws Exception {
            Watchlist watchlist = watchlistService.findById(watchlistId);
            return ResponseEntity.ok (watchlist);
        }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId) throws Exception {
        User user =  userService.findUserProfileByJwt (jwt);
        Coin coin = coinService.findById(coinId);
        Coin addedCoin = watchlistService.addItemToWatchlist(coin, user);
        return ResponseEntity.ok(addedCoin);
    }



}
