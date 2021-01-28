package com.cursodesousa.libraryapi.model.repository;

import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.entity.Loan;
import com.cursodesousa.libraryapi.model.repoitory.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static com.cursodesousa.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository repository;
    @Test
    @DisplayName("Deve verificar se existe um emprestimo nao devolvido para o livro")
    public void existsByBookAndNotReturned(){
        Loan loan = createAndPersist(LocalDate.now());
        Book book = loan.getBook();

        Boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar emprestimo pelo isbn ou customer")
    public void findByBookIsbnOrCustomer(){
        Loan loan = createAndPersist(LocalDate.now());

        Page<Loan> result = repository.findByBookIsbnOrCustomer("123","Fulano", PageRequest.of(0,10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).contains(loan);
    }
    @Test
    @DisplayName("Deve obter emprestimo cuja data emprestimo for menor ou igual a tres dias atras e nao retornado")
    public void findByLoansDateLessThanAndNotReturned(){
        Loan loan = createAndPersist(LocalDate.now().minusDays(5));
        List<Loan> result = repository.findByLoansDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar vazio quano nao houver emprestimo atrasado")
    public void notFindByLoansDateLessThanAndNotReturned(){
        Loan loan = createAndPersist(LocalDate.now());
        List<Loan> result = repository.findByLoansDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();
    }

    private Loan createAndPersist(LocalDate localDate) {
        Book book = createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(localDate).build();

        entityManager.persist(loan);
        return loan;
    }
}
