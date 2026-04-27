package com.deonsteenkamp;

import javax.swing.SwingUtilities;

public class App {
    public static void main( String[] args )
    {

        DatabaseManager.testConnection();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                View frame = new View();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}