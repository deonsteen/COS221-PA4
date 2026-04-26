package com.deonsteenkamp;

import javax.swing.SwingUtilities;

public class App {
    public static void main( String[] args )
    {

        DatabaseManager.testConnectionAndPull();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}