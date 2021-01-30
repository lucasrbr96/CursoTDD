package com.cursodesousa.libraryapi.service.impl;

import com.cursodesousa.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    @Value("${application.mail.default-remetent}")
    private String remetent;

    @Override
    public void sendMails(String mensagem, List<String> mailList) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("Livro com emprestimo atrasado");
        mailMessage.setText(mensagem);

        String[] mails = mailList.toArray(new String[mailList.size()]);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);
    }
}
