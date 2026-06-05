package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Timer;

public class DBApp {
    public static void main(String[] args) {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername("root");
        dataSource.setPassword("yearup26");

        String sql = """
                SELECT
                    film_id,
                    title,
                    last_update
                FROM
                    film
                """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet results = stmt.executeQuery();
        ) {

            while (results.next()) {
                int filmId = results.getInt("film_id");
                String title = results.getString("title");
                Timestamp Time =results.getTimestamp("last_update");

                System.out.println(filmId + " - " + title + " - " + Time);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}