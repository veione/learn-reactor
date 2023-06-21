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
 * @date 2023年06月21日 16:43:00
 */
public class AuthorRepository {

    private static final String SELECTALLBOOKS = "SELECT id ,name FROM AUTHOR";

    public List<Author> getAllAuthors() {
        List<Author> result = new ArrayList<>(2);
        Connection connection = null;
        try {
            connection = H2DataSource.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet rst = statement.executeQuery(SELECTALLBOOKS);
            while (rst.next()) {
                int id = rst.getInt(1);
                String name = rst.getString(2);
                result.add(new Author(id, name));
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
