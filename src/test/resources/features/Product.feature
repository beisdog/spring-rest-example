Feature: Test products

  Scenario: Assign numbers to enterprise
    Given path param enterpriseId=kamran
    And list param dns=+49-221100100,+49-221100200,+49-221100300
    When POST /enterprise/{enterpriseId}/assignedNumbers
    Then status code is 204

  Scenario: Check assigned numbers in enterprise
    Given path param enterpriseId=kamran
    When GET /enterprise/{enterpriseId}/assignedNumbers
    Then response list with path "" is not empty


  Scenario: Unassign numbers from enterprise
    Given path param enterpriseId=kamran
    Given list param dns=+49-221100100,+49-221100200,+49-221100300
    When DELETE /enterprise/{enterpriseId}/assignedNumbers
    Then status code is 204