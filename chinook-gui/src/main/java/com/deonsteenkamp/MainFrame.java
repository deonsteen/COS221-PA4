package com.deonsteenkamp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Chinook Database GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        DefaultTableModel employeeData = DatabaseManager.getEmployeesTableModel();
        JTable employeeTable = new JTable(employeeData);
        employeeTable.setFillsViewportHeight(true);

        TableRowSorter <DefaultTableModel> sorter = new TableRowSorter <> (employeeData);
        employeeTable.setRowSorter(sorter);

        // adds search bar
        JPanel topSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topSearchPanel.add(new JLabel("Search Name/City:"));
        JTextField searchField = new JTextField(20);
        topSearchPanel.add(searchField);

        // makes that changes happen in real time as user types
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null); // shows everything if search is empty
                } else {
                    // math for the row filter 
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1, 3));
                }
            }
        });

        // 5. Assemble the UI
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.add(topSearchPanel, BorderLayout.NORTH); // Put search bar at the top
        employeePanel.add(scrollPane, BorderLayout.CENTER);    // Put table in the center

        // Add the completed tab to the pane
        tabbedPane.addTab("Employees", employeePanel);
        tabbedPane.addTab("Customers", new JPanel());

        add(tabbedPane);
    }
}