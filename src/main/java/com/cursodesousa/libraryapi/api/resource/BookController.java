package com.cursodesousa.libraryapi.api.resource;

import com.cursodesousa.libraryapi.api.dto.BookDTO;
import com.cursodesousa.libraryapi.api.dto.LoanDTO;
import com.cursodesousa.libraryapi.api.exception.ApiErrors;
import com.cursodesousa.libraryapi.exception.BusinessException;
import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.entity.Loan;
import com.cursodesousa.libraryapi.service.BookService;
import com.cursodesousa.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService service;

    private final ModelMapper model;

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        log.info(" creating a book for isbn {}", dto.getIsbn());
        Book entity = model.map(dto, Book.class);

        entity = service.save(entity);

        return model.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id){
        log.info(" obtaining details for book {}", id);
        return  service.getById(id).map(book -> model.map(book, BookDTO.class))
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  void delete(@PathVariable Long id){
    log.info(" deleting book of id {}", id);
    Book book = service.getById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    service.delete(book);
    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable long id, BookDTO dto){
        log.info(" updating book of id {}", id);
        return  service.getById(id).map(book -> {
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);

            return  model.map(book, BookDTO.class);
        }).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageableRequest){
        Book book = model.map(dto,Book.class);
        Page<Book> result = service.find(book, pageableRequest);
        List<BookDTO> list = result.getContent().stream()
                .map(entity -> model.map(entity, BookDTO.class))
                .collect(Collectors.toList());
        return  new PageImpl<BookDTO>(list, pageableRequest, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable("id") Long id, Pageable pageable){
        Book book = service.getById(id).orElseThrow(()->new ResponseStatusException((HttpStatus.NOT_FOUND)));
        Page<Loan> result = loanService.getLoansByBook(book,pageable);
        List<LoanDTO> list = result.getContent().stream().map(loan -> {
            Book loanBook = loan.getBook();
            BookDTO bookDTO = model.map(loanBook, BookDTO.class);
            LoanDTO loanDTO = model.map(loan, LoanDTO.class);
            loanDTO.setBook(bookDTO);
            return loanDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(list, pageable, result.getTotalElements());
    }

}
