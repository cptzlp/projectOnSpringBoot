package ru.max.springcourse.projectonspringboot;

import ru.max.springcourse.projectonspringboot.utils.ConnectionManager;

import java.sql.*;
import java.util.Random;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
        checkMetaData();
        System.out.println(getTitleOfBookAndOwner(new Random().nextInt(1, 5)));
    }


    //Получаем имя владельца и название его книги
    public static String getTitleOfBookAndOwner(int ownerId) {
        String sql = """
                 select p.name as name, b.title as title  from person p, book b
                 where p.id = ? \s
                \s""";

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ownerId);

            ResultSet owner = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            if (owner.next()) {
                sb.append("Owner: ")
                        .append(owner.getString("name"))
                        .append(", book: ")
                        .append(owner.getString("title"));
            } else {
                sb.append("This owner does not exist.");
            }

            return sb.toString();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //Метаданные базы данных
    public static void checkMetaData() throws SQLException {
        try (Connection connection = ConnectionManager.get()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String userName = metaData.getUserName();
            System.out.println(userName);
        }
    }
}
