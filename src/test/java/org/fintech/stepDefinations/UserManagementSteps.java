package org.fintech.stepDefinations;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.cucumber.datatable.DataTable;
import org.utility.ApiResource;
import org.utility.ConfigReader;

import java.util.Map;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManagementSteps {
    private Response response;
    private String baseUrl;
    private int lastUserId;
    private List<String> roles;
    private static final Logger logger = LoggerFactory.getLogger(UserManagementSteps.class);

    @Given("the API base URL is set")
    public void setApiBaseUrl() {
        baseUrl = ConfigReader.get("baseUrl");
        logger.info("User API base URL set: {}", baseUrl);
    }

    // 1. Create user
    @When("I create a user with the following details:")
    public void createUser(DataTable dataTable) {
        Map<String, String> user = dataTable.asMaps().get(0);
        logger.info("Creating user with details: {}", user);
        response = RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post(baseUrl + ApiResource.USERS.getResourcePath());
        if (response.statusCode() == 201) {
            lastUserId = response.jsonPath().getInt("id");
            logger.info("User created with id: {}", lastUserId);
        } else {
            logger.warn("User creation failed. Status: {}, Response: {}", response.statusCode(), response.asString());
        }
    }

    // 2. Get user
    @When("I retrieve the user with id {int}")
    public void getUserById(int userId) {
        logger.info("Retrieving user with id: {}", userId);
        response = RestAssured.get(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
    }

    // 3. Update user
    @When("I update the user with id {int} with the following details:")
    public void updateUser(int userId, DataTable dataTable) {
        Map<String, String> updates = dataTable.asMap(String.class, String.class);
        logger.info("Updating user id {} with details: {}", userId, updates);
        response = RestAssured.given()
                .contentType("application/json")
                .body(updates)
                .put(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
    }

    // 4. Delete user
    @When("I delete the user with id {int}")
    public void deleteUser(int userId) {
        logger.info("Deleting user with id: {}", userId);
        response = RestAssured.delete(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
    }

    @Then("the response status code should be {int}")
    public void checkStatusCode(int statusCode) {
        assertThat(response.statusCode(), is(statusCode));
    }

    @Then("the response should contain the user id")
    public void responseShouldContainUserId() {
        assertThat(response.jsonPath().getInt("id"), greaterThan(0));
    }

    @Then("the response should contain the name {string}")
    public void responseShouldContainName(String name) {
        assertThat(response.jsonPath().getString("name"), equalTo(name));
    }

    @Then("the response should contain the updated name {string}")
    public void responseShouldContainUpdatedName(String name) {
        assertThat(response.jsonPath().getString("name"), equalTo(name));
    }

    @Given("a user exists with id {int}")
    public void userExists(int userId) {
        logger.info("Ensuring user exists with id: {}", userId);
        Response getResponse = RestAssured.get(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
        if (getResponse.statusCode() != 200) {
            logger.info("User not found, creating temp user for id: {}", userId);
            RestAssured.given()
                .contentType("application/json")
                .body(Map.of("name", "Temp User", "email", "temp@user.com", "password", "temp123", "role", "CUSTOMER"))
                .post(baseUrl + ApiResource.USERS.getResourcePath());
        }
    }

    @Then("the user with id {int} should not exist")
    public void userShouldNotExist(int userId) {
        Response getResponse = RestAssured.get(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
        assertThat(getResponse.statusCode(), is(404));
    }

    @And("the following roles exist:")
    public void rolesExist(DataTable dataTable) {
        roles = dataTable.asList(String.class);
        logger.info("Roles set for test: {}", roles);
        // In a real test, you might set up roles in the system here
    }

    @Then("the response should contain the role {string}")
    public void responseShouldContainRole(String role) {
        assertThat(response.jsonPath().getString("role"), equalTo(role));
    }

    @Then("the response should contain the error message {string}")
    public void responseShouldContainErrorMessage(String errorMessage) {
        assertThat(response.jsonPath().getString("error"), equalTo(errorMessage));
    }

    // 9. Auth required
    @When("I try to access the user API without authentication")
    public void accessUserApiWithoutAuth() {
        logger.warn("Accessing user API without authentication");
        response = RestAssured.given()
                .get(baseUrl + ApiResource.USERS.getResourcePath() + "/1");
    }

    // 10. Auth invalid
    @When("I try to access the user API with invalid token")
    public void accessUserApiWithInvalidToken() {
        logger.warn("Accessing user API with invalid token");
        response = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .get(baseUrl + ApiResource.USERS.getResourcePath() + "/1");
    }

    // 11. Auth valid
    @When("I try to access the user API with valid token")
    public void accessUserApiWithValidToken() {
        String token = ConfigReader.get("userToken");
        logger.info("Accessing user API with valid token");
        response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get(baseUrl + ApiResource.USERS.getResourcePath() + "/1");
    }

    @Then("the response status code should not be 401")
    public void responseStatusCodeShouldNotBe401() {
        assertThat(response.statusCode(), not(401));
    }

    // 12. Rate limit
    @When("I send requests to the user API exceeding the rate limit")
    public void exceedUserApiRateLimit() {
        logger.warn("Sending requests to user API exceeding rate limit");
        for (int i = 0; i < 20; i++) {
            response = RestAssured.given().get(baseUrl + ApiResource.USERS.getResourcePath() + "/1");
        }
    }
}
