@HandlingAllErrorCodes
Feature: Timeseries RestAPI

  Background:
    Given the API endpoint is timeseries
    And the APIKey is correct

  @400
  Scenario: Having access to timeseries API send a Bad Request
    Then verify sending incorrectly constructed request to the endpoint
    
  @401
  Scenario: Test access to timeseries API and verify unauthorized response code
    And the APIKey is wrong
    When you send a basic GET request to the endpoint
    Then request ends with unauthorized response
    
  @429
  Scenario: API timeseries suspends access after pool of 100 requests is depleted
    And the APIKey is depleted
    When you send a basic GET request to the endpoint 100 times
    Then request ends with too many requests response
    
  @500s
  Scenario Outline: Test timeseries API responses using various start and end dates
    When you send a basic GET request to the endpoint with start date <StartDate> and end date <EndDate>
    Then confirm <ServerErrorCode> and <ErrorMessageType> with message starting with <Message>

    Examples:
    
    	| StartDate  | EndDate    | ServerErrorCode | ErrorMessageType      | Message                                  |
    	| 2020-12-31 | 2020-06-25 | 504             | invalid_time_frame    | You have entered an invalid Time-Frame   |
    	| 2024-06-25 | 2024-05-25 | 504             | invalid_time_frame    | You have entered an invalid Time-Frame   |
    	| 2024-06-25 | WrongValue | 503             | invalid_end_date      | You have specified an invalid end date   |
    	| WrongValue | 2024-06-25 | 502             | invalid_start_date    | You have specified an invalid start date |
    	|            | 2024-06-25 | 501             | no_timeframe_supplied | You have not specified a Time-Frame      |
