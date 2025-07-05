package org.fintech.stepDefinations;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.cucumber.datatable.DataTable;
import org.fintech.utility.ApiResource;
import org.fintech.utility.ConfigReader;

import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AccountManagementSteps {
    private Response response;
    private String baseUrl;
    private int lastAccountId;

    @Given("the API base URL is set for accounts")
    public void setApiBaseUrl() {
        baseUrl = ConfigReader.get("baseUrl");
    }

    // 1. Create account
    @When("I create an account with the following details:")
    public void createAccount(DataTable dataTable) {
        Map<String, String> account = dataTable.asMaps().get(0);
        response = RestAssured.given()
                .contentType("application/json")
                .body(account)
                .post(baseUrl + ApiResource.ACCOUNTS.getResourcePath());
        if (response.statusCode() == 201) {
            lastAccountId = response.jsonPath().getInt("id");
        }
    }

    // 2. Get account
    @When("I retrieve the account with id {int}")
    public void getAccountById(int accountId) {
        response = RestAssured.get(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/" + accountId);
    }

    // 3. Update account
    @When("I update the account with id {int} with the following details:")
    public void updateAccount(int accountId, DataTable dataTable) {
        Map<String, String> updates = dataTable.asMap(String.class, String.class);
        response = RestAssured.given()
                .contentType("application/json")
                .body(updates)
                .put(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/" + accountId);
    }

    // 4. Delete account
    @When("I delete the account with id {int}")
    public void deleteAccount(int accountId) {
        response = RestAssured.delete(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/" + accountId);
    }

    @Then("the response status code should be {int}")
    public void checkStatusCode(int statusCode) {
        assertThat(response.statusCode(), is(statusCode));
    }

    @Then("the response should contain the account id")
    public void responseShouldContainAccountId() {
        assertThat(response.jsonPath().getInt("id"), greaterThan(0));
    }

    @Then("the response should contain the balance {double}")
    public void responseShouldContainBalance(double balance) {
        assertThat(response.jsonPath().getDouble("balance"), is(balance));
    }

    @Then("the response should contain the account name {string}")
    public void responseShouldContainAccountName(String name) {
        assertThat(response.jsonPath().getString("accountName"), equalTo(name));
    }

    @Then("the response should contain the updated account name {string}")
    public void responseShouldContainUpdatedAccountName(String name) {
        assertThat(response.jsonPath().getString("accountName"), equalTo(name));
    }

    @Given("an account exists with id {int}")
    public void accountExists(int accountId) {
        Response getResponse = RestAssured.get(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/" + accountId);
        if (getResponse.statusCode() != 200) {
            // Create account if not exists (simplified for demo)
            RestAssured.given()
                .contentType("application/json")
                .body(Map.of("accountName", "Temp Account", "accountType", "SAVINGS", "initialBalance", "100.00", "currency", "USD"))
                .post(baseUrl + ApiResource.ACCOUNTS.getResourcePath());
        }
    }

    @Then("the account with id {int} should not exist")
    public void accountShouldNotExist(int accountId) {
        Response getResponse = RestAssured.get(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/" + accountId);
        assertThat(getResponse.statusCode(), is(404));
    }

    @Then("the response should contain the error message {string}")
    public void responseShouldContainErrorMessage(String errorMessage) {
        assertThat(response.jsonPath().getString("error"), equalTo(errorMessage));
    }

    @And("the response should contain the account name <accountName>")
    public void theResponseShouldContainTheAccountNameAccountName() {
    }

    // 11. Auth required
    @When("I try to access the account API without authentication")
    public void accessAccountApiWithoutAuth() {
        response = RestAssured.given()
                .get(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/1");
    }

    // 12. Auth invalid
    @When("I try to access the account API with invalid token")
    public void accessAccountApiWithInvalidToken() {
        response = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .get(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/1");
    }

    // 13. Auth valid
    @When("I try to access the account API with valid token")
    public void accessAccountApiWithValidToken() {
        String token = ConfigReader.get("accountToken");
        response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/1");
    }

    @Then("the response status code should not be 401")
    public void responseStatusCodeShouldNotBe401() {
        assertThat(response.statusCode(), not(401));
    }

    // 14. Rate limit
    @When("I send requests to the account API exceeding the rate limit")
    public void exceedAccountApiRateLimit() {
        for (int i = 0; i < 20; i++) {
            response = RestAssured.given().get(baseUrl + ApiResource.ACCOUNTS.getResourcePath() + "/1");
        }
    }
}
