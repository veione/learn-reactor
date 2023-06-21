package com.think.reactor.asyncbridge.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 16:47:00
 */
public class BookRepository {

    private static final String SELECTALLBOOKS = "SELECT id ,title,author_id FROM BOOK";

    public List<Book> getAllBooks() {
        List<Book> result = new ArrayList<>(10);
        Connection connection = null;
        try {
            connection = H2DataSource.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet allBooks = statement.executeQuery(SELECTALLBOOKS);
            while (allBooks.next()) {
                int id = allBooks.getInt(1);
                String title = allBooks.getString(2);
                int author_id = allBooks.getInt(3);
                result.add(new Book(id, title, author_id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
