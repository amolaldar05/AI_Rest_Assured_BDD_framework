package org.fintech.stepDefinations;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.cucumber.datatable.DataTable;
import org.fintech.utility.ApiResource;
import org.fintech.utility.ConfigReader;

import java.util.Map;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserManagementSteps {
    private Response response;
    private String baseUrl;
    private int lastUserId;
    private List<String> roles;

    @Given("the API base URL is set")
    public void setApiBaseUrl() {
        baseUrl = ConfigReader.get("baseUrl");
    }

    // 1. Create user
    @When("I create a user with the following details:")
    public void createUser(DataTable dataTable) {
        Map<String, String> user = dataTable.asMaps().get(0);
        response = RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post(baseUrl + ApiResource.USERS.getResourcePath());
        if (response.statusCode() == 201) {
            lastUserId = response.jsonPath().getInt("id");
        }
    }

    // 2. Get user
    @When("I retrieve the user with id {int}")
    public void getUserById(int userId) {
        response = RestAssured.get(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
    }

    // 3. Update user
    @When("I update the user with id {int} with the following details:")
    public void updateUser(int userId, DataTable dataTable) {
        Map<String, String> updates = dataTable.asMap(String.class, String.class);
        response = RestAssured.given()
                .contentType("application/json")
                .body(updates)
                .put(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
    }

    // 4. Delete user
    @When("I delete the user with id {int}")
    public void deleteUser(int userId) {
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
        Response getResponse = RestAssured.get(baseUrl + ApiResource.USERS.getResourcePath() + "/" + userId);
        if (getResponse.statusCode() != 200) {
            // Create user if not exists (simplified for demo)
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
        response = RestAssured.given()
                .get(baseUrl + ApiResource.USERS.getResourcePath() + "/1");
    }

    // 10. Auth invalid
    @When("I try to access the user API with invalid token")
    public void accessUserApiWithInvalidToken() {
        response = RestAssured.given()
                .header("Authorization", "Bearer invalid_token")
                .get(baseUrl + ApiResource.USERS.getResourcePath() + "/1");
    }

    // 11. Auth valid
    @When("I try to access the user API with valid token")
    public void accessUserApiWithValidToken() {
        String token = ConfigReader.get("userToken");
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
        for (int i = 0; i < 20; i++) {
            response = RestAssured.given().get(baseUrl + ApiResource.USERS.getResourcePath() + "/1");
        }
    }
}
