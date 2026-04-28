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
        tabbedPane.addTab("Notifications", createNotificationsTab());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) {

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

    //Notifications Tab CRUD
    private JPanel createNotificationsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable customerTable = new JTable(DatabaseManager.getCustomersTableModel());
        customerTable.setFillsViewportHeight(true);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.add(formPanel, BorderLayout.SOUTH);

        JTextField txtId = new JTextField(); 
        txtId.setEnabled(false);
        JTextField txtFName = new JTextField();
        JTextField txtLName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtCountry = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        inputPanel.add(new JLabel("ID:")); inputPanel.add(txtId);
        inputPanel.add(new JLabel("First:")); inputPanel.add(txtFName);
        inputPanel.add(new JLabel("Last:")); inputPanel.add(txtLName);
        inputPanel.add(new JLabel("Email:")); inputPanel.add(txtEmail);
        inputPanel.add(new JLabel("Phone:")); inputPanel.add(txtPhone);
        inputPanel.add(new JLabel("Country:")); inputPanel.add(txtCountry);
        formPanel.add(inputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnClear = new JButton("Clear Form");
        JButton btnAdd = new JButton("Add New");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete Selected");
        
        buttonPanel.add(btnClear); buttonPanel.add(btnAdd); 
        buttonPanel.add(btnUpdate); buttonPanel.add(btnDelete);
        formPanel.add(buttonPanel);

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                int row = customerTable.getSelectedRow();

                txtId.setText(customerTable.getValueAt(row, 0).toString());
                txtFName.setText(customerTable.getValueAt(row, 1) != null ? customerTable.getValueAt(row, 1).toString() : "");
                txtLName.setText(customerTable.getValueAt(row, 2) != null ? customerTable.getValueAt(row, 2).toString() : "");
                txtEmail.setText(customerTable.getValueAt(row, 3) != null ? customerTable.getValueAt(row, 3).toString() : "");
                txtPhone.setText(customerTable.getValueAt(row, 4) != null ? customerTable.getValueAt(row, 4).toString() : "");
                txtCountry.setText(customerTable.getValueAt(row, 5) != null ? customerTable.getValueAt(row, 5).toString() : "");
            }
        });

        btnClear.addActionListener(e -> {
            customerTable.clearSelection();
            txtId.setText(""); txtFName.setText(""); txtLName.setText("");
            txtEmail.setText(""); txtPhone.setText(""); txtCountry.setText("");
        });

        btnAdd.addActionListener(e -> {
            DatabaseManager.insertCustomer(txtFName.getText(), txtLName.getText(), txtEmail.getText(), txtPhone.getText(), txtCountry.getText());
            customerTable.setModel(DatabaseManager.getCustomersTableModel()); // Refresh table
            btnClear.doClick(); // Reset form
        });

        btnUpdate.addActionListener(e -> {
            if (!txtId.getText().isEmpty()) {
                int id = Integer.parseInt(txtId.getText());
                DatabaseManager.updateCustomer(id, txtFName.getText(), txtLName.getText(), txtEmail.getText(), txtPhone.getText(), txtCountry.getText());
                customerTable.setModel(DatabaseManager.getCustomersTableModel());
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a customer to update.");
            }
        });


        btnDelete.addActionListener(e -> {
            if (!txtId.getText().isEmpty()) {
                int id = Integer.parseInt(txtId.getText());
                int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = DatabaseManager.deleteCustomer(id);
                    if (success) {
                        customerTable.setModel(DatabaseManager.getCustomersTableModel());
                        btnClear.doClick();
                    } else {
                        JOptionPane.showMessageDialog(panel, "Cannot delete! This customer has purchase history. You can't just erase the people giving you money.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a customer to delete.");
            }
        });

        return panel;
    }

}