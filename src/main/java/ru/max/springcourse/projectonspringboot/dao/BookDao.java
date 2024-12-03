package ru.max.springcourse.projectonspringboot.dao;

import ru.max.springcourse.projectonspringboot.entity.Book;
import ru.max.springcourse.projectonspringboot.entity.Person;
import ru.max.springcourse.projectonspringboot.exception.DaoException;
import ru.max.springcourse.projectonspringboot.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDao implements Dao<Long, Book> {
    private static final BookDao INSTANCE = new BookDao();

    private static final String SAVE_SQL = """
            INSERT INTO book(title, publication_date) VALUES (?, ?)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM book WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, title, publication_date FROM book WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, title, publication_date FROM book
            """;

    private static final String UPDATE_SQL = """
            UPDATE book SET title = ?, publication_date = ? WHERE id = ?
            """;


    //Добавить книгу в БД
    @Override
    public Book save(Book book) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setDate(2, Date.valueOf(book.getPublicationDate()));

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                book.setId(keys.getLong("id"));
            }

            return book;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //Удалить книгу из БД
    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //Изменить книгу в БД
    @Override
    public boolean update(Book book) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, book.getTitle());
            statement.setDate(2, Date.valueOf(book.getPublicationDate()));
            statement.setLong(3, book.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //Получить все книги
    @Override
    public List<Book> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Book> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(getBook(resultSet));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //Получить книгу по ID
    @Override
    public Optional<Book> findById(Long id) {
        try (Connection connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //С коннекшеном
    public Optional<Book> findById(Long id, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Book book = null;
            if (resultSet.next()) {
                book = getBook(resultSet);
            }
            return Optional.ofNullable(book);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static Book getBook(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getDate("publication_date").toLocalDate());
    }

    private BookDao() {
    }

    public static BookDao getInstance() {
        return INSTANCE;
    }
}
