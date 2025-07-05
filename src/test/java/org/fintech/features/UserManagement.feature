@user_management
Feature: User Management API
  This feature covers all user management operations via API for the Fintech domain.

  Background:
    Given the API base URL is set
    And the following roles exist:
      | role      |
      | CUSTOMER  |
      | ADMIN     |

  @create_user
  Scenario: Create a new customer user
    When I create a user with the following details:
      | name        | email                | password | role     |
      | John Doe    | john@fintech.com     | pass123  | CUSTOMER |
    Then the response status code should be 201
    And the response should contain the user id
    And the response should contain the role "CUSTOMER"

  @create_admin
  Scenario: Create a new admin user
    When I create a user with the following details:
      | name        | email                | password | role  |
      | Jane Admin  | jane@fintech.com     | admin123 | ADMIN |
    Then the response status code should be 201
    And the response should contain the user id
    And the response should contain the role "ADMIN"

  @get_user
  Scenario Outline: Get user by id
    When I retrieve the user with id <userId>
    Then the response status code should be 200
    And the response should contain the name <name>
    And the response should contain the role <role>
    Examples:
      | userId | name        | role     |
      | 1      | John Doe    | CUSTOMER |
      | 2      | Jane Admin  | ADMIN    |

  @update_user
  Scenario: Update user details
    Given a user exists with id 1
    When I update the user with id 1 with the following details:
      | field    | value               |
      | name     | John Updated        |
      | email    | john@newfintech.com |
    Then the response status code should be 200
    And the response should contain the updated name "John Updated"

  @delete_user
  Scenario: Delete a user
    Given a user exists with id 1
    When I delete the user with id 1
    Then the response status code should be 204
    And the user with id 1 should not exist

  @negative_create_user
  Scenario: Attempt to create a user with an existing email
    When I create a user with the following details:
      | name        | email            | password | role     |
      | John Doe    | john@fintech.com | pass123  | CUSTOMER |
    And I create a user with the following details:
      | name        | email            | password | role     |
      | Jane Smith  | john@fintech.com | pass456  | CUSTOMER |
    Then the response status code should be 409
    And the response should contain the error message "Email already exists"

  @negative_get_user
  Scenario: Attempt to retrieve a non-existent user
    When I retrieve the user with id 9999
    Then the response status code should be 404
    And the response should contain the error message "User not found"

  @negative_update_user
  Scenario: Attempt to update a user with invalid data
    Given a user exists with id 1
    When I update the user with id 1 with the following details:
      | field    | value |
      | email    | not-an-email |
    Then the response status code should be 400
    And the response should contain the error message "Invalid email format"

  @negative_delete_user
  Scenario: Attempt to delete a non-existent user
    When I delete the user with id 9999
    Then the response status code should be 404
    And the response should contain the error message "User not found"

  @auth_required
  Scenario: Access user API without authentication
    When I try to access the user API without authentication
    Then the response status code should be 401
    And the response should contain the error message "Unauthorized"

  @auth_invalid
  Scenario: Access user API with invalid token
    When I try to access the user API with invalid token
    Then the response status code should be 401
    And the response should contain the error message "Invalid or expired token"

  @auth_valid
  Scenario: Access user API with valid token
    When I try to access the user API with valid token
    Then the response status code should not be 401

  @rate_limit
  Scenario: Exceed rate limit for user API
    When I send requests to the user API exceeding the rate limit
    Then the response status code should be 429
    And the response should contain the error message "Too many requests"
