package org.cucumberRunners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/AccountManagement.feature",
        glue = "org.fintech.stepDefinations",
        plugin = {"pretty", "html:target/cucumber-reports/account-management.html"}
)
public class AccountManagementRunner extends AbstractTestNGCucumberTests {
}
