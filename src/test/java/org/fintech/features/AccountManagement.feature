@account_management
Feature: Account Management API
  This feature covers all account management operations via API for the Fintech domain.

  Background:
    Given the API base URL is set for accounts

  @create_account
  Scenario: Create a new account
    When I create an account with the following details:
      | accountName | accountType | initialBalance | currency |
      | Main Wallet | SAVINGS     | 1000.00       | USD      |
    Then the response status code should be 201
    And the response should contain the account id
    And the response should contain the balance 1000.00

  @get_account
  Scenario Outline: Get account by id
    When I retrieve the account with id <accountId>
    Then the response status code should be 200
    And the response should contain the account name <accountName>
    Examples:
      | accountId | accountName  |
      | 1         | Main Wallet  |
      | 2         | Travel Fund  |

  @update_account
  Scenario: Update account name
    Given an account exists with id 1
    When I update the account with id 1 with the following details:
      | field       | value         |
      | accountName | Updated Wallet|
    Then the response status code should be 200
    And the response should contain the updated account name "Updated Wallet"

  @delete_account
  Scenario: Delete an account
    Given an account exists with id 1
    When I delete the account with id 1
    Then the response status code should be 204
    And the account with id 1 should not exist

  @negative_create_account
  Scenario: Attempt to create an account with duplicate name
    When I create an account with the following details:
      | accountName | accountType | initialBalance | currency |
      | Main Wallet | SAVINGS     | 1000.00       | USD      |
    And I create an account with the following details:
      | accountName | accountType | initialBalance | currency |
      | Main Wallet | SAVINGS     | 500.00        | USD      |
    Then the response status code should be 409
    And the response should contain the error message "Account name already exists"

  @negative_get_account
  Scenario: Attempt to retrieve a non-existent account
    When I retrieve the account with id 9999
    Then the response status code should be 404
    And the response should contain the error message "Account not found"

  @negative_update_account
  Scenario: Attempt to update an account with invalid data
    Given an account exists with id 1
    When I update the account with id 1 with the following details:
      | field        | value |
      | accountType  | INVALID_TYPE |
    Then the response status code should be 400
    And the response should contain the error message "Invalid account type"

  @negative_delete_account
  Scenario: Attempt to delete a non-existent account
    When I delete the account with id 9999
    Then the response status code should be 404
    And the response should contain the error message "Account not found"

  @edge_zero_balance
  Scenario: Create an account with zero initial balance
    When I create an account with the following details:
      | accountName | accountType | initialBalance | currency |
      | Zero Wallet | SAVINGS     | 0.00          | USD      |
    Then the response status code should be 201
    And the response should contain the balance 0.00

  @edge_large_balance
  Scenario: Create an account with a very large initial balance
    When I create an account with the following details:
      | accountName | accountType | initialBalance | currency |
      | Big Wallet  | SAVINGS     | 1000000000.00 | USD      |
    Then the response status code should be 201
    And the response should contain the balance 1000000000.00

  @auth_required
  Scenario: Access account API without authentication
    When I try to access the account API without authentication
    Then the response status code should be 401
    And the response should contain the error message "Unauthorized"

  @auth_invalid
  Scenario: Access account API with invalid token
    When I try to access the account API with invalid token
    Then the response status code should be 401
    And the response should contain the error message "Invalid or expired token"

  @auth_valid
  Scenario: Access account API with valid token
    When I try to access the account API with valid token
    Then the response status code should not be 401

  @rate_limit
  Scenario: Exceed rate limit for account API
    When I send requests to the account API exceeding the rate limit
    Then the response status code should be 429
    And the response should contain the error message "Too many requests"
