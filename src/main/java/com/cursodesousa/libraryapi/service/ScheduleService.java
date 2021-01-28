package com.cursodesousa.libraryapi.service;

import com.cursodesousa.libraryapi.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    @Value("${application.mail.lateloans.message}")
    private String mensagem;
    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void senMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> mailList = allLateLoans.stream().map(loan -> loan.getEmail()).collect(Collectors.toList());

        emailService.sendMails(mensagem,mailList);
    }
}
