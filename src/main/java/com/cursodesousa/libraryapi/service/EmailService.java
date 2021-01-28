package com.cursodesousa.libraryapi.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public interface EmailService {
    public void sendMails(String mensagem, List<String> mailList);

}
