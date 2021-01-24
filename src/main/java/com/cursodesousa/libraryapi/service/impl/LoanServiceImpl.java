package com.cursodesousa.libraryapi.service.impl;

import com.cursodesousa.libraryapi.model.entity.Loan;
import com.cursodesousa.libraryapi.model.repoitory.LoanRepository;
import com.cursodesousa.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {
    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}
