package com.txstate.bloodhound;

import com.txstate.bloodhound.service.RecordManager;
import com.txstate.bloodhound.ui.ConsoleApp;

/**
 * Entry point for the standalone Bloodhound application.
 */
public class BloodhoundApplication {
    public static void main(String[] args) {
        RecordManager manager = new RecordManager();
        ConsoleApp app = new ConsoleApp(manager);
        app.run();
    }
}
