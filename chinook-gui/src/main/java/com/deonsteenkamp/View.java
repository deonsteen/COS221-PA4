package com.deonsteenkamp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.mysql.cj.xdevapi.Table;

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
        tabbedPane.addTab("Insights & Recommendations", createRecommendationsTab());

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

    private JPanel createNotificationsTab() {
        
        JPanel masterPanel = new JPanel(new BorderLayout());
        JTabbedPane subTabbedPane = new JTabbedPane();
        JPanel managePanel = new JPanel(new BorderLayout());

        DefaultTableModel crudModel = DatabaseManager.getCustomersTableModel();
        JTable customerTable = new JTable(crudModel);
        customerTable.setFillsViewportHeight(true);
        TableRowSorter<DefaultTableModel> crudSorter = new TableRowSorter<>(crudModel);
        customerTable.setRowSorter(crudSorter);

        managePanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JTextField txtId = new JTextField(); txtId.setEnabled(false);
        JTextField txtFName = new JTextField(); JTextField txtLName = new JTextField();
        JTextField txtEmail = new JTextField(); JTextField txtPhone = new JTextField();
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
        managePanel.add(formPanel, BorderLayout.SOUTH);


        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                int viewRow = customerTable.getSelectedRow();
                int modelRow = customerTable.convertRowIndexToModel(viewRow);
                
                txtId.setText(customerTable.getModel().getValueAt(modelRow, 0).toString());
                txtFName.setText(customerTable.getModel().getValueAt(modelRow, 1) != null ? customerTable.getModel().getValueAt(modelRow, 1).toString() : "");
                txtLName.setText(customerTable.getModel().getValueAt(modelRow, 2) != null ? customerTable.getModel().getValueAt(modelRow, 2).toString() : "");
                txtEmail.setText(customerTable.getModel().getValueAt(modelRow, 3) != null ? customerTable.getModel().getValueAt(modelRow, 3).toString() : "");
                txtPhone.setText(customerTable.getModel().getValueAt(modelRow, 4) != null ? customerTable.getModel().getValueAt(modelRow, 4).toString() : "");
                txtCountry.setText(customerTable.getModel().getValueAt(modelRow, 5) != null ? customerTable.getModel().getValueAt(modelRow, 5).toString() : "");
            }
        });

        btnClear.addActionListener(e -> {
            txtId.setText(""); txtFName.setText(""); txtLName.setText("");
            txtEmail.setText(""); txtPhone.setText(""); txtCountry.setText("");
        });

        btnAdd.addActionListener(e -> {
            DatabaseManager.insertCustomer(txtFName.getText(), txtLName.getText(), txtEmail.getText(), txtPhone.getText(), txtCountry.getText());
            customerTable.setModel(DatabaseManager.getCustomersTableModel()); 
            customerTable.setRowSorter(new TableRowSorter<>((DefaultTableModel) customerTable.getModel())); // Reset sorter
            btnClear.doClick(); 
        });

        btnUpdate.addActionListener(e -> {
            if (!txtId.getText().isEmpty()) {
                DatabaseManager.updateCustomer(Integer.parseInt(txtId.getText()), txtFName.getText(), txtLName.getText(), txtEmail.getText(), txtPhone.getText(), txtCountry.getText());
                customerTable.setModel(DatabaseManager.getCustomersTableModel());
                customerTable.setRowSorter(new TableRowSorter<>((DefaultTableModel) customerTable.getModel()));
            }
        });

        btnDelete.addActionListener(e -> {
            if (!txtId.getText().isEmpty()) {
                if (JOptionPane.showConfirmDialog(managePanel, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (DatabaseManager.deleteCustomer(Integer.parseInt(txtId.getText()))) {
                        customerTable.setModel(DatabaseManager.getCustomersTableModel());
                        customerTable.setRowSorter(new TableRowSorter<>((DefaultTableModel) customerTable.getModel()));
                        btnClear.doClick();
                    } else JOptionPane.showMessageDialog(managePanel, "Cannot delete! Customer has purchase history.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel inactivePanel = new JPanel(new BorderLayout());
        JTable inactiveTable = new JTable(DatabaseManager.getInactiveCustomersModel());
        inactiveTable.setFillsViewportHeight(true);
        inactivePanel.add(new JScrollPane(inactiveTable), BorderLayout.CENTER);

        subTabbedPane.addTab("Manage Customers", managePanel);
        subTabbedPane.addTab("Inactive Report", inactivePanel);

        subTabbedPane.addChangeListener(e -> {
            if (subTabbedPane.getSelectedIndex() == 1) {
                inactiveTable.setModel(DatabaseManager.getInactiveCustomersModel());
            }
        });

        masterPanel.add(subTabbedPane, BorderLayout.CENTER);
        return masterPanel;
    }

    private JPanel createRecommendationsTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Customer:"));

        DatabaseManager.ComboItem[] customers = DatabaseManager.getCustomerComboItems();
        JComboBox<DatabaseManager.ComboItem> customerBox = new JComboBox<>(customers);
        topPanel.add(customerBox);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel summaryPannel = new JPanel(new GridLayout(2, 2, 10, 10));
        summaryPannel.setBorder(BorderFactory.createTitledBorder("Customer Insights"));
        
        JLabel lblTotalSPent = new JLabel("Total Spent: R0.00");
        JLabel lblTotalPurchases = new JLabel("Total Purchases: 0");
        JLabel lblLastPurchas = new JLabel("Last Purchase: N/A");
        JLabel lblFavGenre = new JLabel("Favorite Genre: N/A");

        summaryPannel.add(lblTotalSPent);
        summaryPannel.add(lblTotalPurchases);
        summaryPannel.add(lblLastPurchas);
        summaryPannel.add(lblFavGenre);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Recommended Tracks"));
        JTable RecommendationsTable = new JTable();
        tablePanel.add(new JScrollPane(RecommendationsTable), BorderLayout.CENTER);
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(summaryPannel, BorderLayout.NORTH);
        centerWrapper.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        customerBox.addActionListener(e -> {
            DatabaseManager.ComboItem selectedCustomer = (DatabaseManager.ComboItem) customerBox.getSelectedItem();
            if (selectedCustomer != null) {
                int customerId = selectedCustomer.getId();

                String[] summary = DatabaseManager.getCustomerSummary(customerId);
                String favoriteGenre = DatabaseManager.getFavoriteGenre(customerId);

                lblTotalSPent.setText("Total Spent: " + summary[0]);
                lblTotalPurchases.setText("Total Purchases: " + summary[1]);
                lblLastPurchas.setText("Last Purchase: " + summary[2]);
                lblFavGenre.setText("Favorite Genre: " + favoriteGenre);
                RecommendationsTable.setModel(DatabaseManager.getRecommendationsModel(customerId, favoriteGenre));
            }
        });

        if (customerBox.getItemCount() > 0) {
            customerBox.setSelectedIndex(0); // Trigger initial load
            
        }
        return mainPanel;
    }
}