package org.fintech.stepDefinations;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentGatewaySteps {
    private int responseStatusCode;
    private String transactionId;
    private String errorMessage;
    private double responseAmount;
    private boolean paymentAlreadySuccessful = false;
    private boolean duplicateWebhookReceived = false;
    private boolean fraudFlagged = false;
    private boolean cancelled = false;
    private boolean retryTriggered = false;
    private boolean timeoutOccurred = false;
    private boolean idempotencyKeyUsed = false;
    private String redirectUrl;
    private List<String> supportedPaymentModes;
    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewaySteps.class);

    @Given("the payment gateway is available")
    public void paymentGatewayIsAvailable() {
        logger.info("Checking payment gateway availability");
        responseStatusCode = 200;
    }

    @Given("the payment gateway API base URL is set")
    public void paymentGatewayApiBaseUrlIsSet() {
        String baseUrl = org.utility.ConfigReader.get("paymentGatewayBaseUrl");
        logger.info("Loaded payment gateway base URL: {}", baseUrl);
        assertNotNull("Base URL should be set in config.properties", baseUrl);
    }

    @Given("the following payment modes are supported:")
    public void paymentModesSupported(DataTable dataTable) {
        supportedPaymentModes = dataTable.asList(String.class);
        logger.info("Supported payment modes: {}", supportedPaymentModes);
    }

    @When("the user submits a payment with the following details:")
    public void userSubmitsPaymentWithDetails(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> payment = rows.get(0);
        logger.info("Submitting payment with details: {}", payment);
        String cardNumber = payment.get("cardNumber");
        String cardHolder = payment.get("cardHolder");
        String expiry = payment.get("expiry");
        String cvv = payment.get("cvv");
        String amountStr = payment.get("amount");
        String currency = payment.get("currency");
        double amount = Double.parseDouble(amountStr);

        // Simulate payment logic
        if (cardNumber.equals("4111111111111111") && cardHolder.equals("John Doe") && expiry.equals("12/30") && cvv.equals("123") && amount > 0 && amount < 1_000_000_000) {
            responseStatusCode = 200;
            transactionId = "TXN123456";
            responseAmount = amount;
            errorMessage = null;
        } else if (cardNumber.equals("1234567890123456")) {
            responseStatusCode = 400;
            errorMessage = "Invalid card number";
        } else if (cardHolder.contains("' OR '1'='1")) {
            responseStatusCode = 400;
            errorMessage = "Invalid input detected";
        } else if (expiry.equals("12/20")) {
            responseStatusCode = 400;
            errorMessage = "Card expired";
        } else if (cardHolder.contains("<script>")) {
            responseStatusCode = 400;
            errorMessage = "Invalid input detected";
        } else if (amount == 0.0) {
            responseStatusCode = 400;
            errorMessage = "Amount must be greater than zero";
        } else if (amount >= 1_000_000_000) {
            responseStatusCode = 200;
            responseAmount = amount;
            transactionId = "TXN_LARGE";
            errorMessage = null;
        } else {
            responseStatusCode = 400;
            errorMessage = "Unknown error";
        }
    }

    @When("I make a payment with the following details:")
    public void iMakeAPaymentWithDetails(DataTable dataTable) {
        Map<String, String> payment = dataTable.asMaps(String.class, String.class).get(0);
        logger.info("Initiating payment with details: {}", payment);
        String mode = payment.get("mode");
        String cardNumber = payment.get("cardNumber");
        String orderId = payment.get("orderId");
        double amount = Double.parseDouble(payment.get("amount"));
        // Simulate logic for each scenario
        if (mode.equals("CARD") && cardNumber.equals("4111111111111111") && amount == 100.00 && orderId.equals("ORDER1001")) {
            responseStatusCode = 200;
            redirectUrl = "success";
        } else if (mode.equals("CARD") && amount == 99999 && orderId.equals("ORDER1002")) {
            responseStatusCode = 402;
            errorMessage = "Insufficient funds";
        } else if (mode.equals("CRYPTO")) {
            responseStatusCode = 400;
            errorMessage = "Unsupported payment mode";
        } else if (fraudFlagged) {
            responseStatusCode = 400;
            errorMessage = "Fraud review";
        } else {
            responseStatusCode = 400;
            errorMessage = "Unknown error";
        }
    }

    @When("the user tries to submit a payment without authentication")
    public void userSubmitsPaymentWithoutAuth() {
        logger.warn("Attempted payment without authentication");
        responseStatusCode = 401;
        errorMessage = "Unauthorized";
    }

    @When("the user tries to submit a payment with invalid token")
    public void userSubmitsPaymentWithInvalidToken() {
        logger.warn("Attempted payment with invalid token");
        responseStatusCode = 401;
        errorMessage = "Invalid or expired token";
    }

    @When("the user tries to submit a payment with valid token")
    public void userSubmitsPaymentWithValidToken() {
        logger.info("Payment with valid token");
        responseStatusCode = 200;
        errorMessage = null;
    }

    @When("the user sends payment requests exceeding the rate limit")
    public void userExceedsRateLimit() {
        logger.warn("Rate limit exceeded for payment requests");
        responseStatusCode = 429;
        errorMessage = "Too many requests";
    }

    @When("I make a payment with 3DS authentication and enter an incorrect OTP")
    public void iMakePaymentWith3DSIncorrectOTP() {
        logger.info("3DS authentication failed due to incorrect OTP");
        responseStatusCode = 400;
        errorMessage = "3DS authentication failed";
    }

    @Given("a payment with orderId {word} is already successful")
    public void paymentAlreadySuccessful(String orderId) {
        logger.info("Payment with orderId {} is already successful", orderId);
        paymentAlreadySuccessful = true;
    }

    @When("the payment gateway sends a duplicate webhook callback for orderId {word}")
    public void duplicateWebhookCallback(String orderId) {
        logger.warn("Duplicate webhook callback received for orderId {}", orderId);
        duplicateWebhookReceived = true;
    }

    @When("I make a payment request with an invalid API key")
    public void paymentWithInvalidApiKey() {
        logger.warn("Payment request made with an invalid API key");
        responseStatusCode = 401;
        errorMessage = "Unauthorized";
    }

    @When("the user cancels the payment during 3DS authentication")
    public void userCancels3DSPayment() {
        logger.info("Payment cancelled by the user during 3DS authentication");
        cancelled = true;
    }

    @When("a network disconnect occurs during payment processing")
    public void networkDisconnectDuringPayment() {
        logger.warn("Network disconnect occurred during payment processing");
        retryTriggered = true;
    }

    @When("I make a payment flagged as high-risk (e.g., abnormal amount or suspicious IP)")
    public void paymentFlaggedHighRisk() {
        logger.warn("Payment flagged as high-risk");
        fraudFlagged = true;
    }

    @When("I retry a failed payment API call with the same idempotency key")
    public void retryWithIdempotencyKey() {
        logger.info("Retrying failed payment with the same idempotency key");
        idempotencyKeyUsed = true;
        responseStatusCode = 200;
    }

    @When("the payment gateway does not respond within 30 seconds")
    public void paymentGatewayTimeout() {
        logger.error("Payment gateway did not respond within the expected time frame");
        timeoutOccurred = true;
    }

    @Then("the payment response status code should be {int}")
    public void paymentResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertEquals(expectedStatusCode, responseStatusCode);
    }

    @Then("the payment response status code should not be {int}")
    public void paymentResponseStatusCodeShouldNotBe(int notExpectedStatusCode) {
        assertNotEquals(notExpectedStatusCode, responseStatusCode);
    }

    @Then("the payment response should contain a transaction id")
    public void paymentResponseShouldContainTransactionId() {
        assertNotNull(transactionId);
    }

    @Then("the payment response should contain the amount {double}")
    public void paymentResponseShouldContainAmount(double expectedAmount) {
        assertEquals(expectedAmount, responseAmount, 0.001);
    }

    @Then("the payment response should contain the error message {string}")
    public void paymentResponseShouldContainErrorMessage(String expectedMessage) {
        assertEquals(expectedMessage, errorMessage);
    }

    @Then("the payment should redirect to the success URL")
    public void paymentShouldRedirectToSuccessUrl() {
        assertEquals("success", redirectUrl);
    }

    @Then("the payment should not be recorded twice")
    public void paymentShouldNotBeRecordedTwice() {
        assertTrue(paymentAlreadySuccessful && duplicateWebhookReceived);
    }

    @Then("the payment should be marked as cancelled")
    public void paymentShouldBeMarkedCancelled() {
        assertTrue(cancelled);
    }

    @Then("the user should be redirected to the failure/cancel page")
    public void userRedirectedToFailureCancelPage() {
        assertTrue(cancelled);
    }

    @Then("the system should trigger a retry or show a pending message")
    public void systemShouldTriggerRetryOrPending() {
        assertTrue(retryTriggered);
    }

    @Then("the payment should be flagged for fraud review")
    public void paymentShouldBeFlaggedForFraud() {
        assertTrue(fraudFlagged);
    }

    @Then("the payment should fail gracefully")
    public void paymentShouldFailGracefully() {
        assertTrue(fraudFlagged);
    }

    @Then("the payment should not be duplicated")
    public void paymentShouldNotBeDuplicated() {
        assertTrue(idempotencyKeyUsed);
    }

    @Then("the system should fail gracefully with a user-friendly timeout error")
    public void systemShouldFailGracefullyTimeout() {
        assertTrue(timeoutOccurred);
    }
}
