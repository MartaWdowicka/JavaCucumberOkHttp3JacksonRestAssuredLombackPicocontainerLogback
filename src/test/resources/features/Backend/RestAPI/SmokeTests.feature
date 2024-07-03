@200 @SmokeTests
Feature: Timeseries RestAPI

  Background:
    Given the API endpoint is timeseries
    And the APIKey is correct

  Scenario: Test access to timeseries API and verify response codes
    When you send a basic GET request to the endpoint
    Then request ends with access granted response

  Scenario: Having access to timeseries API test currency conversion feature
    When you send GET request with base currency PLN and output currencies USD, EUR, MXN
    Then compare response to saved expected response template