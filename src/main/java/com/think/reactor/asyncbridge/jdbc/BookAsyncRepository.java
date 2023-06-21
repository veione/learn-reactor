package com.think.reactor.asyncbridge.jdbc;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 16:43:00
 */
public class BookAsyncRepository {
    private static final String SELECT_ALL_BOOKS = "SELECT id, title, author_id FROM BOOK";

    Flux<Book> getAllBooksAsync() {
        Flux<Book> objectFlux = Flux.create(fluxSink -> {
            Connection connection = null;
            try {
                connection = H2DataSource.getInstance().getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rst = stmt.executeQuery(SELECT_ALL_BOOKS);
                while (rst.next()) {
                    int id = rst.getInt(1);
                    String title = rst.getString(2);
                    int author_id = rst.getInt(3);
                    fluxSink.next(new Book(id, title, author_id));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                fluxSink.error(e);
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                fluxSink.complete();
            }
        });
        return objectFlux.subscribeOn(Schedulers.parallel(), false);
    }
}
