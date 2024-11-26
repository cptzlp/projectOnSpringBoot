package ru.max.springcourse.projectonspringboot.exception;


public class DaoException extends RuntimeException {
    public DaoException(Throwable e) {
        super(e);
    }
}
