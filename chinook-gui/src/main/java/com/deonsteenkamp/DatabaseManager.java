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

    public static void testConnection() {

        try (Connection conn = connect()) {

            System.out.println("\n=== DATABASE CONNECTION SUCCESSFUL ===");

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

    public static DefaultTableModel getTracksTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Track ID");
        model.addColumn("Name");
        model.addColumn("Unit Price");

        String query = "SELECT TrackId, Name, UnitPrice FROM Track;";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("TrackId"),
                        rs.getString("Name"),
                        rs.getDouble("UnitPrice")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static void insertTrack(String name, int albumId, int mediaId, int genreId, double price) {
        String query = "INSERT INTO Track (Name, AlbumId, MediaTypeId, GenreId, Milliseconds, Bytes, UnitPrice) " +
                "VALUES (?, ?, ?, ?, 200000, 5000000, ?)";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, albumId);
            pstmt.setInt(3, mediaId);
            pstmt.setInt(4, genreId);
            pstmt.setDouble(5, price);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to insert the new track. Your SQL might be angry.");
            e.printStackTrace();
        }
    }

    public static ComboItem[] getAlbums() {
        // We use a dynamic list and convert it to an array at the end
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        String query = "SELECT AlbumId, Title FROM Album ORDER BY Title;";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new ComboItem(rs.getInt("AlbumId"), rs.getString("Title")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new ComboItem[0]);
    }

    public static ComboItem[] getGenres() {
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        String query = "SELECT GenreId, Name FROM Genre ORDER BY Name;";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new ComboItem(rs.getInt("GenreId"), rs.getString("Name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new ComboItem[0]);
    }

    public static ComboItem[] getMediaTypes() {
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        String query = "SELECT MediaTypeId, Name FROM MediaType ORDER BY Name;";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new ComboItem(rs.getInt("MediaTypeId"), rs.getString("Name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new ComboItem[0]);
    }

    public static class ComboItem {
        private int id;
        private String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
