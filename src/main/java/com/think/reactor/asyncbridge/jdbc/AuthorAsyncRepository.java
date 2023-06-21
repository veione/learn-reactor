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
 * @date 2023年06月21日 16:32:00
 */
public class AuthorAsyncRepository {
    private static final String SELECT_ALL_BOOKS = "SELECT id, name FROM AUTHOR";

    public Flux<Author> getAllAuthorsAsync() {
        Flux<Author> objectFlux = Flux.create(fluxSink -> {
            Connection connection = null;
            try {
                connection = H2DataSource.getInstance().getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rst = stmt.executeQuery(SELECT_ALL_BOOKS);
                while (rst.next()) {
                    int id = rst.getInt(1);
                    String name = rst.getString(2);
                    //推送数据
                    fluxSink.next(new Author(id, name));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //传播异常
                fluxSink.error(e);
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //终止关闭
                fluxSink.complete();
            }
        });

        return objectFlux.subscribeOn(Schedulers.parallel(), false);
    }
}
