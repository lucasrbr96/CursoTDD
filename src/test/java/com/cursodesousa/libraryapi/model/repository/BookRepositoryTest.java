package com.cursodesousa.libraryapi.model.repository;

import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.repoitory.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro")
    public  void returenTrueWhenIsbnExists(){
       //cenario
       String isbn = "123";
        Book book = Book.builder()
                .title("Aventuras")
                .author("Fulano")
                .isbn(isbn).build();
        entityManager.persist(book);
       //execução
        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve retornar falso quando nao existir um livro")
    public  void returnFalseWhenIsbnExists(){
        //cenario
        String isbn = "123";

        //execução
        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isFalse();

    }
}
