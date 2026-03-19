package com.txstate.bloodhound;

import com.txstate.bloodhound.service.RecordManager;
import com.txstate.bloodhound.ui.DesktopAppFrame;
import com.txstate.bloodhound.util.StorageInitializationResult;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Swing desktop Bloodhound application.
 */
public class BloodhoundDesktopApplication {
    public static void main(String[] args) {
        RecordManager manager = new RecordManager();
        StorageInitializationResult startup = manager.initializeStorage();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use default look and feel if native style cannot be applied.
            }

            DesktopAppFrame frame = new DesktopAppFrame(manager, startup);
            frame.setVisible(true);
        });
    }
}
