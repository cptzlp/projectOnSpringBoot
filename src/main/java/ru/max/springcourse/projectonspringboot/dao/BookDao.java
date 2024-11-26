package ru.max.springcourse.projectonspringboot.dao;

public class BookDao {
    private static final BookDao INSTANCE = new BookDao();

    private BookDao() {
    }

    public static BookDao getInstance() {
        return INSTANCE;
    }
}
