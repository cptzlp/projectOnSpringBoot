package ru.max.springcourse.projectonspringboot.service;

import org.springframework.stereotype.Service;
import ru.max.springcourse.projectonspringboot.dao.BookDao;
import ru.max.springcourse.projectonspringboot.dto.BookDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private static final BookService INSTANCE = new BookService();
    private final BookDao bookDao = BookDao.getInstance();


    public List<BookDto> findAll() {
        return bookDao.findAll().stream()
                .map(book -> new BookDto(book.getId(),
                "Title: %s, Publication date: %s".formatted(book.getTitle(), book.getPublicationDate())))
                .collect(Collectors.toList());
    }

    public static BookService getInstance() {
        return INSTANCE;
    }

    private BookService() {
    }
}
