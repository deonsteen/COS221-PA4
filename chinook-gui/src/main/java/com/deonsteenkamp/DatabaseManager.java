package com.deonsteenkamp;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/u25135742_chinook";
    private static final String USER = "u25135742";

    public static Connection connect() throws SQLException {

        String password = System.getenv("DB_PASSWORD");

        if (password == null) {
            throw new IllegalStateException("Database password not set in environment variable DB_PASSWORD");
        }
        return DriverManager.getConnection(URL, USER, password);
    }

    public static void testConnection() {

        try (Connection conn = connect()){

            System.out.println("\n=== DATABASE CONNECTION SUCCESSFUL ===");
            System.out.println("======================================\n");

        } catch (SQLException e) {
            System.err.println("\n[ERROR] Database connection or query failed!");
            System.err.println("Make sure MariaDB is running and your DB_PASSWORD is set.");
            e.printStackTrace();
        }
    }

    public static DefaultTableModel getEmployeesTableModel() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Title");
        model.addColumn("City");
        model.addColumn("Country");
        model.addColumn("Phone");
        model.addColumn("Supervisor");
        model.addColumn("Active");

        String query = "SELECT e.FirstName, e.LastName, e.Title, e.City, e.Country, e.Phone, " +
                "CONCAT(m.FirstName, ' ', m.LastName) AS SupervisorName " +
                "FROM Employee e " +
                "LEFT JOIN Employee m ON e.ReportsTo = m.EmployeeId;";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Title"),
                        rs.getString("City"),
                        rs.getString("Country"),
                        rs.getString("Phone"),
                        rs.getString("SupervisorName") == null ? "N/A" : rs.getString("SupervisorName"), "yes"

                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }


    
}
