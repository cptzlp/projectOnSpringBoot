package ru.max.springcourse.projectonspringboot.dao;

import ru.max.springcourse.projectonspringboot.dto.PersonFilter;
import ru.max.springcourse.projectonspringboot.entity.Book;
import ru.max.springcourse.projectonspringboot.entity.Person;
import ru.max.springcourse.projectonspringboot.exception.DaoException;
import ru.max.springcourse.projectonspringboot.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PersonDao implements Dao<Long, Person> {

    private static final PersonDao INSTANCE = new PersonDao();

    private static final BookDao BOOK_DAO = BookDao.getInstance();

    private static final String SAVE_SQL = """
            INSERT INTO person(name, age, book_id) VALUES (?, ?, ?)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM person WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT p.id, p.name, p.age, p.book_id, b.title, b.publication_date FROM person p
             JOIN public.book b on b.id = p.book_id
            """;

    private static final String FIND_BY_ID_SQL =
            FIND_ALL_SQL + " WHERE p.id = ? ";

    private static final String UPDATE_SQL = """
            UPDATE person SET name = ?, age = ?, book_id = ? WHERE id = ? 
            """;

    //Получить всех людей
    public List<Person> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Person> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(getPerson(resultSet));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //Получить всех людей(с фильтром)
    public List<Person> findAll(PersonFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filter.name() != null) {
            parameters.add(filter.name());
            whereSql.add("name = ?");
        }
        if (filter.age() > 0) {
            parameters.add(filter.age());
            whereSql.add("age = ?");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        String where = whereSql.stream().collect(Collectors.joining(" AND ",
                parameters.size() > 2 ? " WHERE " : " ",
                " LIMIT ? OFFSET ?"));

        String sql = FIND_ALL_SQL + where;
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            List<Person> result = new ArrayList<>();
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(getPerson(resultSet));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    //Получить одного человека по ID
    public Optional<Person> findById(Long id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Person person = null;
            if (resultSet.next()) {
                person = getPerson(resultSet);
            }
            return Optional.ofNullable(person);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //Вспомогательный метод для получения человека из resultSet
    private static Person getPerson(ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                BOOK_DAO.findById(resultSet.getLong("book_id"),
                        resultSet.getStatement().getConnection()).orElse(null));
    }


    //Добавить человека в бд
    public Person save(Person person) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, person.getName());
            statement.setInt(2, person.getAge());
            statement.setLong(3, person.getBook().getId());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                person.setId(keys.getLong("id"));
            }

            return person;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    //Удалить человека из бд по ID
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    //Обновить человека в БД
    public boolean update(Person person) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, person.getName());
            statement.setInt(2, person.getAge());
            statement.setLong(3, person.getBook().getId());
            statement.setLong(4, person.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private PersonDao() {
    }

    public static PersonDao getInstance() {
        return INSTANCE;
    }
}
