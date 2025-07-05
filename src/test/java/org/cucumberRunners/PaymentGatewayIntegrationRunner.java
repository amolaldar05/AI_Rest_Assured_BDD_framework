package org.cucumberRunners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/PaymentGatewayIntegration.feature",
        glue = {"org.fintech.stepDefinations"},
        plugin = {"pretty", "html:target/cucumber-reports.html"},
        dryRun = false
)
public class PaymentGatewayIntegrationRunner {
}

