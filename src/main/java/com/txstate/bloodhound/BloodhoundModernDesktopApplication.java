package com.txstate.bloodhound;

import com.txstate.bloodhound.service.RecordManager;
import com.txstate.bloodhound.ui.ModernDesktopAppFrame;
import com.txstate.bloodhound.util.StorageInitializationResult;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the alternative modern-themed desktop UI.
 */
public class BloodhoundModernDesktopApplication {
    public static void main(String[] args) {
        RecordManager manager = new RecordManager();
        StorageInitializationResult startup = manager.initializeStorage();

        SwingUtilities.invokeLater(() -> {
            applyNimbusLookAndFeel();
            ModernDesktopAppFrame frame = new ModernDesktopAppFrame(manager, startup);
            frame.setVisible(true);
        });
    }

    private static void applyNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Continue with default look and feel when custom setup is unavailable.
        }
    }
}
