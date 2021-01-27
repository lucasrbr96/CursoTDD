package com.cursodesousa.libraryapi.service;


import com.cursodesousa.libraryapi.api.dto.LoanFilterDTO;
import com.cursodesousa.libraryapi.exception.BusinessException;
import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.entity.Loan;
import com.cursodesousa.libraryapi.model.repoitory.LoanRepository;
import com.cursodesousa.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        Loan savingLoang = createLoan();

        Mockito.when(repository.existsByBookAndNotReturned(createLoan().getBook())).thenReturn(true);

        Throwable exception = catchThrowable(()-> service.save(savingLoang) );


        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(repository, Mockito.never()).save(savingLoang);

    }

    @Test
    @DisplayName("Deve obter informaçoes de um emprestimo pelo id")
    public void getLoanDetailTest(){
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        Mockito.when(repository.save(loan)).thenReturn(loan);

        Loan updateLoan = service.update(loan);

        assertThat(updateLoan.getReturned()).isTrue();
        Mockito.verify(repository).save(loan);


    }
    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    public void findBookTest(){
        //cenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);


        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, 1);

        Mockito.when(repository.findByBookIsbnOrCustomer(
                Mockito.anyString(),Mockito.anyString(),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execução
        Page<Loan> result =  service.find(loanFilterDTO,pageRequest);

        //verificações
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan(){
        String customer = "Fulano";

        Book book = Book.builder().id(1L).build();
        Loan savingLoang = Loan.builder().book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        return savingLoang;
    }
}
