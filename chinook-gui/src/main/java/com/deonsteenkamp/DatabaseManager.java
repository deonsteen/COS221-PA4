package com.deonsteenkamp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
 
    private static final String URL = "jdbc:mysql://localhost:3306/u25135742_Chinook";
    private static final String USER = "u25135742";


    public static Connection connect() throws SQLException {

        String password = System.getenv("DB_PASSWORD");
        
        if (password == null) {
            throw new IllegalStateException("Database password not set in environment variable DB_PASSWORD");
        }
        return DriverManager.getConnection(URL, USER, password);
    }

}
