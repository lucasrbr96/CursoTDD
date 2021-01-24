package com.cursodesousa.libraryapi.model.repoitory;

import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndNotReturned(Book book);
}
