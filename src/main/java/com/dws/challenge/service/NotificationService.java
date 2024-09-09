package com.dws.challenge.service;

import org.springframework.stereotype.Service;

import com.dws.challenge.domain.Account;


public interface NotificationService {

  void notifyAboutTransfer(Account account, String transferDescription);
}
