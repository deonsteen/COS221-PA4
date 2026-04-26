package com.deonsteenkamp;

import javax.swing.SwingUtilities;

public class App {
    public static void main( String[] args )
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { // <-- You were missing this method declaration
                MainFrame frame = new MainFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}