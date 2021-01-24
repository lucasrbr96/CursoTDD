package com.cursodesousa.libraryapi.api.resource;

import com.cursodesousa.libraryapi.api.dto.BookDTO;
import com.cursodesousa.libraryapi.exception.BusinessException;
import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";


    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;


    private BookDTO createBook() {
        return BookDTO.builder().author("Arthur").title("As aventuras").isbn("001").build();
    }

    @Test
    @DisplayName("Deve criar um book com sucesso")
    public void createBookTest() throws  Exception{

        //criação do objeto
        BookDTO dto = createBook();
        //criação do objeto salvo
        Book savedBook = Book.builder().id(10l).author("Arthur").title("As aventuras").isbn("001").build();
        //chamada do service passando o book e esperando o book salvo
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willReturn(savedBook);

        //criando o json para a requisição faker
        String json = new ObjectMapper().writeValueAsString(dto);

        //criando a requisição em si
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificação da simulação
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar um erro ao criar um book")
    public void createInvalidBookTest() throws Exception{
        //criando o json para a requisição faker
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        //criando a requisição em si
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }
    @Test
    @DisplayName("Deve lançar um erro ao cadastrar um livro ja utilizado")
    public void createBooWithDuplicatedIsbn() throws Exception{
        BookDTO dto = createBook();

        String mensagemErro = "Isbn já cadastrado";

        //criando o json para a requisição faker
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));

        //criando a requisição em si
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }

    @Test
    @DisplayName("Deve obter informaçoes do livro")
    public  void getBookDetailTest() throws Exception{
        //cenario
        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .title(createBook().getTitle())
                .author(createBook().getAuthor())
                .isbn(createBook().getIsbn()).build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execucao

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBook().getTitle()))
                .andExpect(jsonPath("author").value(createBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createBook().getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando livro procurado nao existir")
    public void bookNotFoundTest() throws Exception{

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception{

        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1));

        mvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando nao encontrar o livro para deletar")
    public void deleteInexitentBookTest() throws Exception{

        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1));

        mvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public  void updateBookTest() throws Exception{
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBook());

        Book updatingBook = Book.builder().id(1L)
                .title("some title")
                .author("some author")
                .isbn("321").build();

        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(
                        updatingBook));

        BDDMockito.given(service.update(updatingBook))
                .willReturn(Book.builder()
                        .id(id)
                        .author("Arthur")
                        .title("As aventuras")
                        .isbn("321").build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBook().getTitle()))
                .andExpect(jsonPath("author").value(createBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));;
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public  void updateInexistentBookTest() throws Exception{
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBook());

        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception{
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title(createBook().getTitle())
                .isbn(createBook().getIsbn())
                .author(createBook().getAuthor())
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class),
                Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book),
                        PageRequest.of(0,100),1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(),book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                .get(BOOK_API.concat(queryString))
                                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content",Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));




    }


}
