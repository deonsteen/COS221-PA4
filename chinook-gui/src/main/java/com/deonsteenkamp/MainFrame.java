package com.deonsteenkamp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Chinook Database GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        DefaultTableModel employeeData = DatabaseManager.getEmployeesTableModel();

        JTable employeeTable = new JTable(employeeData);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Employees", employeePanel);
        tabbedPane.addTab("Customers", new JPanel());

        add(tabbedPane);
    }

}
