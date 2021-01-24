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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import java.time.LocalDate;

import static com.cursodesousa.libraryapi.model.repository.BookRepositoryTest.createNewBook;
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
        Book book = createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();

        entityManager.persist(loan);

        Boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }
}
