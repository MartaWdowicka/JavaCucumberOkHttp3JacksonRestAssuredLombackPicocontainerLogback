@Regresion
Feature: Timeseries RestAPI

  Background:
  	Given the API endpoint is timeseries
    And the APIKey is correct
    
  @200 @Retest201ErrorCodeInsteadOf501
  Scenario Outline: Test timeseries API responses to various base currency and output currencies values
    When you send GET request with base currency <BaseCurrency> and output currencies <OutputCurrencies>
    And confirm <ServerErrorCode> and <ErrorMessageType> with message starting with <Message>

    Examples:
    
    	| BaseCurrency  | OutputCurrencies | ServerErrorCode | ErrorMessageType      | Message                                                              |
    	| PLN           | USD, EUR, MXN    | -               | -                     | -                                                                    |
    	| PLN           |                  | -               | -                     | -                                                                    |
    	|               | USD, EUR, MXN    | -               | -                     | -                                                                    |
    	| USD, EUR, MXN | USD              | 501             | invalid_base_currency | You have specified an invalid base. Only single currency is allowed. |