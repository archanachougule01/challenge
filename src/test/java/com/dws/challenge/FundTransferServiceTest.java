package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.FundTransferService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FundTransferServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    FundTransferService fundTransferService;

    @BeforeEach
    void setUp() {
        accountsService.getAccountsRepository().clearAccounts();

    }

    @Test
    void transfer() {
        Account fromAccount = new Account("Id-001");
        fromAccount.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(fromAccount);

        Account toAccount = new Account("Id-002");
        toAccount.setBalance(new BigDecimal(500));
        this.accountsService.createAccount(toAccount);

        try {
            this.fundTransferService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(), new BigDecimal("200"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(new BigDecimal("800"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700"), toAccount.getBalance());

    }

    @Test
    void testTransferWithInsufficientFunds() {
        Account fromAccount = new Account("Id-001");
        fromAccount.setBalance(new BigDecimal(2000));
        this.accountsService.createAccount(fromAccount);

        Account toAccount = new Account("Id-002");
        toAccount.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(toAccount);

        try {
            this.fundTransferService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(), new BigDecimal("2500"));
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("Insufficient balance.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testTransferNegativeamount() {
        Account fromAccount = new Account("Id-001");
        fromAccount.setBalance(new BigDecimal(2000));
        this.accountsService.createAccount(fromAccount);

        Account toAccount = new Account("Id-002");
        toAccount.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(toAccount);

        try {
            this.fundTransferService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(), new BigDecimal("-150"));
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("Transfer amount must be positive");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testTransferFromNonExistingAccount() {
        Account toAccount = new Account("Id-002");
        toAccount.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(toAccount);

        try {
            try {
                this.fundTransferService.transfer("Id-001", toAccount.getAccountId(), new BigDecimal("-150"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("From account not found");
        }
    }
}
