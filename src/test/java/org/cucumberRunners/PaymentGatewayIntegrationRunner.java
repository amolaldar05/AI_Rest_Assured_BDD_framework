package org.cucumberRunners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/PaymentGatewayIntegration.feature",
        glue = "org.fintech.stepDefinations",
        plugin = {"pretty", "html:target/cucumber-reports/payment-gateway.html"}
)
public class PaymentGatewayIntegrationRunner extends AbstractTestNGCucumberTests {
}
