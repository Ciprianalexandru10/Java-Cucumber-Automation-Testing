Feature: Verify Debtor Total Amount

  Scenario: Check if Debtor Total Amount has at least 2 digits

    Given the XML document is loaded
    When I check the debtor total amount
    Then The debtor total amount should have at least 2 digits

  Scenario: Check if Debtor Total Amount is equal to the sum of all credits

    Given the XML document is loaded
    When I calculate the total debtor amount and sum of all credits
    Then The debtor total amount should be equal to the sum of all credits

  Scenario: Check if transaction date is not in the future

    Given the XML document is loaded
    When I check the transaction date
    Then The transaction date should not be in the future

  Scenario: Check if IBANs are valid

    Given the XML document is loaded
    When I check the validity of IBANs
    Then All IBANs should be valid