package com.ss.service;

import com.ss.model.Coin;
import com.ss.model.User;
import com.ss.model.Watchlist;

public interface WatchlistService {
    // TODO: Implement watchlist management

    Watchlist findUserWatchList(Long userId) throws Exception;
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin, User user) throws Exception;

}
