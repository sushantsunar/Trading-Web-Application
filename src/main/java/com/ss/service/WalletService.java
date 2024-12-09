package com.ss.service;

import com.ss.model.Order;
import com.ss.model.User;
import com.ss.model.Wallet;

public interface WalletService {
    // Add methods for wallet operations
    // Example: deposit, withdraw, transfer, etc.

    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, Long amount);
    Wallet findWalletById(Long id) throws Exception;

    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet,Long amount) throws Exception;
    Wallet payOrderPayment(Order order, User user) throws Exception;
}
