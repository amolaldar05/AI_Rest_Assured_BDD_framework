package org.utility;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
    @Before
    public void beforeScenario() {
        // Setup code: logging, DB connection, etc.
        System.out.println("[HOOK] Starting scenario...");
        // Example: Initialize DB connection
        //DBConnectionManager.init();
        // Example: Set up logging
        //Logger.setup();
    }

    @After
    public void afterScenario() {
        // Teardown code: cleanup, logging, etc.
        System.out.println("[HOOK] Scenario finished.");
        // Example: Close DB connection
        //DBConnectionManager.close();
        // Example: Clean up resources
        //ResourceCleaner.cleanup();
    }
}
