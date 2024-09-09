package com.dws.challenge;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountDoesNotExistException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.FundTransferService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
class FundTransferControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private FundTransferService fundTransferService;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
        Account fromAccount = new Account("Id1", new BigDecimal("1500.00"));
        Account toAccount = new Account("Id2", new BigDecimal("1000.00"));

        this.accountsService.createAccount(fromAccount);
        this.accountsService.createAccount(toAccount);

    }

    @AfterEach
    void cleanUp() {
        accountsService.getAccountsRepository().clearAccounts();

    }

    @Test
    void testSuccessfulTransferAmount() throws Exception {
        this.mockMvc.perform(post("/v1/fundTransfer/transfer")
                        .param("fromAccountId", "Id1").param("toAccountId", "Id2").param("amount", "1200")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string("Transfer Successful."));
    }

    @Test
    void testTransferWithInsufficientFunds() throws Exception {
        this.mockMvc.perform(post("/v1/fundTransfer/transfer")
                        .param("fromAccountId", "Id1").param("toAccountId", "Id2").param("amount", "2000")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance."));
    }

    @Test
    void testTransferNegativeamount() throws Exception {
        this.mockMvc.perform(post("/v1/fundTransfer/transfer")
                        .param("fromAccountId", "Id1").param("toAccountId", "Id2").param("amount", "-1000")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(content().string("Transfer amount must be positive"));
    }

    @Test
    void testTransferFromNonExistingAccount() throws Exception {
        this.mockMvc.perform(post("/v1/fundTransfer/transfer")
                        .param("fromAccountId", "Id123").param("toAccountId", "Id2").param("amount", "1000")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
                .andExpect(content().string("From account not found"));
    }

    @Test
    void testTransferToNonExistingAccount() throws Exception {
        this.mockMvc.perform(post("/v1/fundTransfer/transfer")
                        .param("fromAccountId", "Id1").param("toAccountId", "Id201").param("amount", "1000")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
                .andExpect(content().string("To account not found"));
    }
}
