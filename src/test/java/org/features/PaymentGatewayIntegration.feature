@payment_gateway
Feature: Payment Gateway Integration
  This feature covers all payment gateway operations for the Fintech domain, including API structure, status codes, callbacks, retries, fraud prevention, and 3DS authentication.

  Background:
    Given the payment gateway API base URL is set
    And the following payment modes are supported:
      | mode         |
      | CARD         |
      | UPI          |
      | NETBANKING   |
      | WALLET       |

  @PG001
  Scenario: Make payment with valid card details
    When I make a payment with the following details:
      | mode | cardNumber        | cardHolder | expiry | cvv | amount | currency | orderId   |
      | CARD | 4111111111111111  | John Doe   | 12/30  | 123 | 100.00 | USD      | ORDER1001 |
    Then the payment response status code should be 200
    And the payment should redirect to the success URL

  @PG002
  Scenario: Payment fails due to insufficient funds
    When I make a payment with the following details:
      | mode | cardNumber        | cardHolder | expiry | cvv | amount | currency | orderId   |
      | CARD | 4111111111111111  | John Doe   | 12/30  | 123 | 99999  | USD      | ORDER1002 |
    Then the payment response status code should be 402
    And the payment response should contain the error message "Insufficient funds"

  @PG003
  Scenario: 3DS OTP entered incorrectly
    When I make a payment with 3DS authentication and enter an incorrect OTP
    Then the payment response status code should be 400
    And the payment response should contain the error message "3DS authentication failed"

  @PG004
  Scenario: Duplicate webhook callback
    Given a payment with orderId ORDER1003 is already successful
    When the payment gateway sends a duplicate webhook callback for orderId ORDER1003
    Then the payment should not be recorded twice
    And the response status code should be 200

  @PG005
  Scenario: API request with invalid merchant key
    When I make a payment request with an invalid API key
    Then the payment response status code should be 401
    And the payment response should contain the error message "Unauthorized"

  @PG006
  Scenario: User cancels payment on 3DS page
    When the user cancels the payment during 3DS authentication
    Then the payment should be marked as cancelled
    And the user should be redirected to the failure/cancel page

  @PG007
  Scenario: Network disconnect during payment
    When a network disconnect occurs during payment processing
    Then the system should trigger a retry or show a pending message

  @PG008
  Scenario: Check fraud flag for abnormal transaction
    When I make a payment flagged as high-risk (e.g., abnormal amount or suspicious IP)
    Then the payment should be flagged for fraud review
    And the payment should fail gracefully

  @PG009
  Scenario: Retry payment API call with idempotency key
    When I retry a failed payment API call with the same idempotency key
    Then the payment should not be duplicated
    And the response status code should be 200

  @PG010
  Scenario: Payment request timeout
    When the payment gateway does not respond within 30 seconds
    Then the system should fail gracefully with a user-friendly timeout error

  @PG011
  Scenario: Payment with unsupported payment mode
    When I make a payment with the following details:
      | mode   | cardNumber        | cardHolder | expiry | cvv | amount  | currency | orderId   |
      | CRYPTO | 4111111111111111  | John Doe   | 12/30  | 123 | 100.00  | USD      | ORDER1004 |
    Then the payment response status code should be 400
    And the payment response should contain the error message "Unsupported payment mode"
