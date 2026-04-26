package com.deonsteenkamp;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Chinook Database GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.add(new JLabel("Employees will go here"), BorderLayout.CENTER);

        tabbedPane.addTab("Employees", employeePanel);

        tabbedPane.addTab("Customers", new JPanel());

        add(tabbedPane);
    }
    
}
