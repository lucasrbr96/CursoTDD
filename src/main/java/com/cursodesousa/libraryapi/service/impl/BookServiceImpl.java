package com.cursodesousa.libraryapi.service.impl;

import com.cursodesousa.libraryapi.exception.BusinessException;
import com.cursodesousa.libraryapi.model.entity.Book;
import com.cursodesousa.libraryapi.model.repoitory.BookRepository;
import com.cursodesousa.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado");
        }
        return repository.save(book);
    }
}
