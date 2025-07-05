package org.cucumberRunners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.runner.RunWith;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/eatures/AccountManagement.feature",
        glue = {"org.fintech.stepDefinations"},
        plugin = {"pretty", "html:target/cucumber-reports.html"},
        dryRun = false
)
public class AccountManagementRunner {
}

