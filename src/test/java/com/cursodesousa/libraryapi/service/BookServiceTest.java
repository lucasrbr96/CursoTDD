package com.cursodesousa.libraryapi.service;

import com.cursodesousa.libraryapi.exception.BusinessException;
import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.repoitory.BookRepository;
import com.cursodesousa.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }
    @Test
    @DisplayName("Deve salvar um livro")
    public  void saveBookTest(){
        //cenario
        Book book = createValidBook();

        Mockito.when(service.save(book) ).thenReturn(
                 Book.builder().id(1L)
                 .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build());

        //execucao
        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }

    private Book createValidBook() {
        return Book.builder()
                .isbn("123")
                .author("Fulana")
                .title("As aventuras")
                .build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWhitDuplicateISBN(){
        //cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execução
        Throwable exception = Assertions.catchThrowable(()->service.save(book));

        //verificação
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        //verificar que apos exception nao chame o save
        verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTeste(){
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id onde nao existe")
    public void bookNotFoundByIdTeste(){
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Book book = Book.builder()
                .id(1L).build();

        assertDoesNotThrow(()-> service.delete(book));

        verify(repository, Mockito.times(1))
                .delete(book);
    }
    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteInvalidBookTest(){
        Book book = new Book();

        assertThrows(IllegalArgumentException.class,()-> service.delete(book));

        verify(repository, Mockito.never())
                .delete(book);

    }
    @Test
    @DisplayName("Deve deletar um livro")
    public void updateInvalidBookTest(){
        Book book = new Book();

        assertThrows(IllegalArgumentException.class,()-> service.update(book));

        verify(repository, Mockito.never())
                .save(book);

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public  void updateBookTest(){
        Long id = 1L;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        Book book = service.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        //cenario
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista,
                pageRequest,
                1);

        Mockito.when(repository.findAll(Mockito.any(Example.class),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execução
        Page<Book> result =  service.find(book,pageRequest);

        //verificações
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookByIsbnTest(){
        String isbn = "1230";

        Mockito.when(repository.findByIsbn(isbn))
                .thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository,Mockito.times(1)).findByIsbn(isbn);
    }


}
