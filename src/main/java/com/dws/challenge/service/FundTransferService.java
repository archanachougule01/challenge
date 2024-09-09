package com.dws.challenge.service;


import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountDoesNotExistException;
import com.dws.challenge.repository.AccountsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FundTransferService {

    private final AccountsService accountService;
    private final NotificationService notificationService;

    public FundTransferService(AccountsService accountService, NotificationService notificationService) {
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    public synchronized void transfer(String fromAccountId, String toAccountId, BigDecimal amount) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account fromAccount = accountService.getAccount(fromAccountId);
        if (fromAccount == null) {
            throw new AccountDoesNotExistException("From account not found");
        }

        Account toAccount = accountService.getAccount(toAccountId);
        if (toAccount == null) {
            throw new AccountDoesNotExistException("To account not found");
        }

        Account firstLock = fromAccountId.compareTo(toAccountId) < 0 ? fromAccount : toAccount;
        Account secondLock = fromAccountId.compareTo(toAccountId) < 0 ? toAccount : fromAccount;

        synchronized (firstLock) {
            synchronized (secondLock) {
                fromAccount.withdraw(amount);
                toAccount.deposit(amount);

                accountService.updateAccount(fromAccount);
                accountService.updateAccount(toAccount);

                log.info("Transfer successful from account {} to account {} with amount {}", fromAccountId, toAccount, amount);
            }
        }

        notificationService.notifyAboutTransfer(fromAccount, "Amount successfully transferred to" + toAccountId);
        notificationService.notifyAboutTransfer(toAccount, "Received " + amount + " from " + fromAccountId);

    }
}