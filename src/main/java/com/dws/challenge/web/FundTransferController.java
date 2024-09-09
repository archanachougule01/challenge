package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountDoesNotExistException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.FundTransferService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.http.HttpRequest;


@RestController
@RequestMapping("/v1/fundTransfer")
@Slf4j
public class FundTransferController {

  private final FundTransferService fundTransferService;

 @Autowired
  public FundTransferController(FundTransferService fundTransferService) {
    this.fundTransferService = fundTransferService;
  }
 @PostMapping("/transfer")
  public ResponseEntity<Object> transfer(@RequestParam String fromAccountId, @RequestParam String toAccountId, @RequestParam BigDecimal amount){
    log.info("Received request to transfer from account {} to account {} with amount {}", fromAccountId, toAccountId, amount);
    try{
        fundTransferService.transfer(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok("Transfer Successful.");
        
    }catch(IllegalArgumentException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }catch(AccountDoesNotExistException adne){
        return new ResponseEntity<>(adne.getMessage(), HttpStatus.NOT_FOUND);
    }catch(Exception ise){
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed."+ ise.getMessage());
        return new ResponseEntity<>("Transfer failed"+ise.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

    }
  }
  
}
