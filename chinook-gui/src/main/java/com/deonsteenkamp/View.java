package com.deonsteenkamp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class View extends JFrame {

    private JTable reportTable;

    public View() {
        setTitle("Chinook Database GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        
        tabbedPane.addTab("Employees", createEmployeesTab());
        tabbedPane.addTab("Tracks", createTracksTab());
        tabbedPane.addTab("Report", createReportTab());

        tabbedPane.addChangeListener(e -> {
            // Index 2 is the "Report" tab
            if (tabbedPane.getSelectedIndex() == 2) {
                // Fetch fresh data and shove it into the VIP table
                reportTable.setModel(DatabaseManager.getGenreRevenueTableModel());
            }
        });
        add(tabbedPane);
    }

    //Employees Tab
    private JPanel createEmployeesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultTableModel model = DatabaseManager.getEmployeesTableModel();
        JTable table = new JTable(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Name/City:"));
        JTextField searchField = new JTextField(20);
        topPanel.add(searchField);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Filters columns 0 (First Name), 1 (Last Name), and 3 (City)
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1, 3));
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }

    //Tracks Tab
    private JPanel createTracksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JTable trackTable = new JTable(DatabaseManager.getTracksTableModel());
        panel.add(new JScrollPane(trackTable), BorderLayout.CENTER);

        //adds the button
        JButton addTrackBtn = new JButton("Add New Track");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addTrackBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        //The Popup Dialog Logic
        addTrackBtn.addActionListener(e -> {
            
            // Fetch the arrays from the database for the dropdowns
            DatabaseManager.ComboItem[] albums = DatabaseManager.getAlbums();
            DatabaseManager.ComboItem[] genres = DatabaseManager.getGenres();
            DatabaseManager.ComboItem[] mediaTypes = DatabaseManager.getMediaTypes();

            // Feed those arrays directly into the dropdowns
            JComboBox<DatabaseManager.ComboItem> albumBox = new JComboBox<>(albums);
            JComboBox<DatabaseManager.ComboItem> genreBox = new JComboBox<>(genres);
            JComboBox<DatabaseManager.ComboItem> mediaBox = new JComboBox<>(mediaTypes);
            
            JTextField nameField = new JTextField();
            JTextField priceField = new JTextField("0.99"); // Default price

            Object[] message = {
                "Track Name:", nameField,
                "Album:", albumBox,
                "Genre:", genreBox,
                "Media Type:", mediaBox,
                "Unit Price:", priceField
            };

            // Show the popup and wait for the user to click OK or Cancel
            int option = JOptionPane.showConfirmDialog(panel, message, "Add New Track", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                try {
                    // Extract the ID's from the dropdowns they selected
                    int albumId = ((DatabaseManager.ComboItem) albumBox.getSelectedItem()).getId();
                    int genreId = ((DatabaseManager.ComboItem) genreBox.getSelectedItem()).getId();
                    int mediaId = ((DatabaseManager.ComboItem) mediaBox.getSelectedItem()).getId();
                    
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());

                    // Send the extracted data to the database to insert
                    DatabaseManager.insertTrack(name, albumId, mediaId, genreId, price);

                    //Reload table
                    trackTable.setModel(DatabaseManager.getTracksTableModel());
                    
                    JOptionPane.showMessageDialog(panel, "Track saved successfully.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Error: Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    //Reports Tab
    private JPanel createReportTab(){
        JPanel reportPanel = new JPanel(new BorderLayout());
        
        // Initialize the class-level table
        reportTable = new JTable(); 
        reportTable.setFillsViewportHeight(true);
        
        reportPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        
        // Return the constructed panel to the tabbed pane
        return reportPanel;
}
}