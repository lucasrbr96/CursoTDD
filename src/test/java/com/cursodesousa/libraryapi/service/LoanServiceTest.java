package com.cursodesousa.libraryapi.service;


import com.cursodesousa.libraryapi.exception.BusinessException;
import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.entity.Loan;
import com.cursodesousa.libraryapi.model.repoitory.LoanRepository;
import com.cursodesousa.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanService service;

    @MockBean
    private LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void  saveLoanTest(){
        Book book = Book.builder().id(1L).build();
        Loan savingLoang = Loan.builder().book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .customer("Fulano")
                .book(book)
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);

        Mockito.when(repository.save(savingLoang)).thenReturn(savedLoan);


        Loan loan = service.save(savingLoang);

        Mockito.verify(repository, Mockito.times(1)).save(savingLoang);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
        
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao salvar um livro ja emprestado")
    public void  loanedBookSaveLoanTest(){
        Book book = Book.builder().id(1L).build();
        Loan savingLoang = Loan.builder().book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(()-> service.save(savingLoang) );


        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(repository, Mockito.never()).save(savingLoang);

    }
}
