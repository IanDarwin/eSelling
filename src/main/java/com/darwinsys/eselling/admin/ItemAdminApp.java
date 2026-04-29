package com.darwinsys.eselling.admin;

import javax.swing.*;

/**
 * Entry point for the Item Admin desktop application.
 */
public class ItemAdminApp {

    public static void main(String[] args) {
        // Use system look-and-feel for a native feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Don't care
        }

        SwingUtilities.invokeLater(() -> {
            ItemAdminFrame frame =
                    new ItemAdminFrame(new ItemService(), new CategoryService());
            frame.setVisible(true);
        });
    }
}
