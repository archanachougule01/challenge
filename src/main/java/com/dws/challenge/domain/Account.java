package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }
  
  /**
   * Deposits a positive amount into the account.
   * 
   * @param amount the amount to deposit
   * 
   * @throws
   * IllegalArgumentException if amount is non-positive
   */
  public void deposit(BigDecimal amount){
    
    balance = balance.add(amount);
  }

  public void withdraw(BigDecimal amount){
    if(balance.compareTo(amount)< 0){
      throw new IllegalArgumentException("Insufficient balance.");
    }
    balance = balance.subtract(amount);
  }
}
