Feature: Apilayer Timeseries RestAPI

  @200 @401 @404 @RetestUnhandled404
  Scenario Outline: Test access to <APIName> Api and verify response codes
    Given the API endpoint is <EndPoint>
    And the APIKey is <APIKey>
    When you send a basic GET request to the endpoint
    Then request ends with <Response> response
    
    Examples:
    | APIName    | EndPoint                                             | APIKey                           | Response         |
	  | timeseries | https://api.apilayer.com/fixer/timeseries            | xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO | ACCESSGRANTED    |
    | timeseries | https://api.apilayer.com/fixer/timeseries            |                                  | UNAUTHORIZED     |
    | timeseries | https://api.apilayer.com/gummybears                  | xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO | RESOURCENOTFOUND |
    | timeseries | https://api.apilayer.com/fixer/timeseries/gummybears | xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO | RESOURCENOTFOUND |

  @403 @404
  Scenario Outline: Confirm how <APIName> endpoint handles forbidden (403) and undefined (404) request types
    Given the API endpoint is <EndPoint>
    And the APIKey is <APIKey>
    When you send a basic POST request to the endpoint
    Then request ends with forbidden access response
    When you send a basic DELETE request to the endpoint
    Then request ends with resource not found response
    When you send a basic PATCH request to the endpoint
    Then request ends with resource not found response

    Examples:
    | APIName    | EndPoint                                  | APIKey                           |
    | timeseries | https://api.apilayer.com/fixer/timeseries | xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO |

  @400
  Scenario Outline: Having access to <APIName> API send a Bad Request
    Given the API endpoint is <EndPoint>
    And the APIKey is <APIKey>
    Then verify sending incorrectly constructed request to the endpoint

    Examples:
    | APIName    | EndPoint                                  | APIKey                           |
    | timeseries | https://api.apilayer.com/fixer/timeseries | xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO |

  @429
  Scenario Outline: Api <APIName> suspends access after pool of <RequestAmount> requests is depleted
    Given the API endpoint is <EndPoint>
    And the APIKey is <APIKey>
    When you send a basic GET request to the endpoint <RequestAmount> times
    Then request ends with <Response> response

    Examples:
    | APIName    | EndPoint                                  | APIKey                           | RequestAmount | Response        |
    | timeseries | https://api.apilayer.com/fixer/timeseries | VgQALyF8csABwGsJIL7GSVFzPMopMYK7 | 100           | TOOMANYREQUESTS |

  @500s
  Scenario Outline: Test timeseries Api responses to various StartDate and EndDate values
    Given the API endpoint is https://api.apilayer.com/fixer/timeseries
    And the APIKey is xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO
    When you send a basic GET request to the endpoint with start date <StartDate> and end date <EndDate>
    And confirm <ServerErrorCode> and <ErrorMessageType> with message starting with <Message>

    Examples:
    | StartDate  | EndDate    | Response      | ServerErrorCode | ErrorMessageType      | Message                                  |
    | 2020-12-31 | 2020-06-25 | ACCESSGRANTED | 504             | invalid_time_frame    | You have entered an invalid Time-Frame   |
    | 2024-06-25 | 2024-05-25 | ACCESSGRANTED | 504             | invalid_time_frame    | You have entered an invalid Time-Frame   |
    | 2024-06-25 | WrongValue | ACCESSGRANTED | 503             | invalid_end_date      | You have specified an invalid end date   |
    | WrongValue | 2024-06-25 | ACCESSGRANTED | 502             | invalid_start_date    | You have specified an invalid start date |
    |            | 2024-06-25 | ACCESSGRANTED | 501             | no_timeframe_supplied | You have not specified a Time-Frame      |

  @200 @Retest201ErrorCodeInsteadOf501
  Scenario Outline: Test timeseries Api responses to various BaseCurrency and OutputCurrencies values
    Given the API endpoint is https://api.apilayer.com/fixer/timeseries
    And the APIKey is xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO
    When you send GET request with input <BaseCurrency> and expected output <OutputCurrencies>
    And confirm <ServerErrorCode> and <ErrorMessageType> with message starting with <Message>

    Examples:
    | APIName    | BaseCurrency  | OutputCurrencies | ResponseCode  | ServerErrorCode | ErrorMessageType      | Message                                                              |
    | timeseries | PLN           | USD, EUR, MXN    | ACCESSGRANTED | -               | -                     | -                                                                    |
    | timeseries | PLN           |                  | ACCESSGRANTED | -               | -                     | -                                                                    |
    | timeseries |               | USD, EUR, MXN    | ACCESSGRANTED | -               | -                     | -                                                                    |
    | timeseries | USD, EUR, MXN | USD              | ACCESSGRANTED | 501             | invalid_base_currency | You have specified an invalid base. Only single currency is allowed. |

  @200
  Scenario Outline: Having access to <APIName> API test currency conversion feature
    Given the API endpoint is <EndPoint>
    And the APIKey is <APIKey>
    When you send GET request with input <BaseCurrency> and expected output <OutputCurrencies>
    Then compare response to expected response template

    Examples:
    | APIName    | BaseCurrency | OutputCurrencies | EndPoint                                  | APIKey                           |
    | timeseries | PLN          | USD, EUR, MXN    | https://api.apilayer.com/fixer/timeseries | xXWna4w5rPHDqbI2Xvf22eHRB7vD0QQO |
