package ru.max.springcourse.projectonspringboot.dao;

import ru.max.springcourse.projectonspringboot.entity.Person;
import ru.max.springcourse.projectonspringboot.exception.DaoException;
import ru.max.springcourse.projectonspringboot.utils.ConnectionManager;

import java.sql.*;

public class PersonDao {

    private static final PersonDao INSTANCE = new PersonDao();

    private static final String SAVE_SQL = """
            INSERT INTO person(name, age, book_id) VALUES (?, ?, ?)
            """;

    public Person save(Person person) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, person.getName());
            statement.setInt(2, person.getAge());
            statement.setLong(3, person.getBookId());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()){
                person.setId(keys.getLong("id"));
            }

            return person;
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
