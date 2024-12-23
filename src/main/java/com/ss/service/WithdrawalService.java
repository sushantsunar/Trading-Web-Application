package com.ss.service;

import com.ss.model.User;
import com.ss.model.Withdrawal;
import lombok.With;

import java.util.List;

public interface WithdrawalService {

    Withdrawal requestWithdrawal(Long amount, User user);
    Withdrawal procedWithdrawal(Long withdrawalId, boolean accept) throws Exception;
    List<Withdrawal> getUserWithWithdrawalHistory(User user);

    List<Withdrawal> getAllWithdrawalRequest();

}
