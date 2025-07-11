package org.fintech.cucumberRunners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/UserManagement.feature",
    glue = {"org.fintech"},
    plugin = {"pretty", "html:target/cucumber-reports.html"},
    dryRun = false
)
public class UserManagementRunner {
}

