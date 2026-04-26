package com.deonsteenkamp;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

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

    public static void testConnectionAndPull() {
        String query = "SELECT FirstName, LastName, Title FROM Employee LIMIT 5;";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== DATABASE CONNECTION SUCCESSFUL ===");
            System.out.println("Pulling top Employees from MariaDB:");

            while (rs.next()) {
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String title = rs.getString("Title");

                System.out.println("- " + firstName + " " + lastName + " (" + title + ")");
            }

            System.out.println("======================================\n");

        } catch (SQLException e) {
            System.err.println("\n[ERROR] Database connection or query failed!");
            System.err.println("Make sure MariaDB is running and your DB_PASSWORD is set.");
            e.printStackTrace();
        }
    }

    public static DefaultTableModel getEmployeesTableModel() {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Full Name");
        model.addColumn("Job Title");
        model.addColumn("Manager"); // The data from the self-join
        model.addColumn("Hire Date");

        String query = "SELECT e.EmployeeId, " +
                "CONCAT(e.FirstName, ' ', e.LastName) AS EmployeeName, " +
                "e.Title, " +
                "CONCAT(m.FirstName, ' ', m.LastName) AS ManagerName, " +
                "e.HireDate " +
                "FROM Employee e " +
                "LEFT JOIN Employee m ON e.ReportsTo = m.EmployeeId;";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("EmployeeId"),
                        rs.getString("EmployeeName"),
                        rs.getString("Title"),
                        rs.getString("ManagerName") == null ? "N/A (Top Level)" : rs.getString("ManagerName"),
                        rs.getString("HireDate")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }

}
