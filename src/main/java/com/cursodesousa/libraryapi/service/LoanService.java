package com.cursodesousa.libraryapi.service;

import com.cursodesousa.libraryapi.api.dto.LoanFilterDTO;
import com.cursodesousa.libraryapi.api.resource.BookController;
import com.cursodesousa.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable);
}
