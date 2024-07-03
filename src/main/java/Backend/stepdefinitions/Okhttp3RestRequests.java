package Backend.stepdefinitions;

import Backend.ObjectMappers.ConvertCurrencyResponse;
import Backend.ObjectMappers.ErrorResponse;
import Backend.enums.ResponseCodes.ResponseCode;
import lombok.Cleanup;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import Abstract.TestContext;
import org.junit.Assert;

public class Okhttp3RestRequests extends CommonSteps {
	private static final Logger log = LoggerFactory.getLogger(Okhttp3RestRequests.class);
	private TestContext testContext;

	public Okhttp3RestRequests(TestContext testContext) {
        super(testContext);
    }

	public void executeDefaultRequest(String requestType, String startDate, String endDate) throws Exception {
		timeseriesRequest(requestType, (startDate == null)? todaysDate() : startDate, (endDate == null)? todaysDate() : endDate);
	}

	public void timeseriesRequest(String requestType, String dateStart, String dateEnd) throws Exception {
		timeseriesRequest(requestType, dateStart, dateEnd, null, null);
	}

	public void timeseriesRequest(@NonNull String requestType, @NonNull String dateStart, @NonNull String dateEnd, String initialCurrency, String targetCurrencies) throws Exception {
		OkHttpClient client = new OkHttpClient();
		Request request; RequestBody body;
		try {
			switch (requestType) {
				case "POST":
					body = new FormBody.Builder()
		    	      .add("start_date", dateStart)
		    	      .add("end_date", dateEnd)
		    	      .build();
		    	    request = new Request.Builder()
		    	      .url(testContext.getEndpoint())
		    	      .addHeader("apikey", testContext.getAPIKey())
		    	      .post(body)
		    	      .build();
				    break;
				case "DELETE":
					request = new Request.Builder()
		    	      .url(testContext.getEndpoint() + "?start_date=" + dateStart + "&end_date=" + dateEnd)
		    	      .addHeader("apikey", testContext.getAPIKey())
		    	      .delete()
		    	      .build();
				    break;
				case "PATCH":
					body = new FormBody.Builder()
		    	      .add("start_date", dateStart)
		    	      .add("end_date", dateEnd)
		    	      .build();
		    		request = new Request.Builder()
				      .url(testContext.getEndpoint() + "?start_date=" + dateStart + "&end_date=" + dateEnd)
				      .addHeader("apikey", testContext.getAPIKey())
				      .patch(body)
				      .build();
				    break;
				case "GET":
				default:
					targetCurrencies = (targetCurrencies != null) ? targetCurrencies.replaceAll("\\s", "").toUpperCase() : targetCurrencies;
					request = new Request.Builder()
		    	      .url(testContext.getEndpoint() + "?start_date=" + dateStart + "&end_date=" + dateEnd + ((initialCurrency != null)? "&base=" + initialCurrency: "")+ "&end_date=" + dateEnd + ((targetCurrencies != null)? "&symbols=" + targetCurrencies: ""))
		    	      .addHeader("apikey", testContext.getAPIKey())
		    	      .method("GET", null)
		    	      .build();
					if (initialCurrency != null || targetCurrencies != null) {
						JSONObject testData = (testContext.getTestData() == null)? new JSONObject() : testContext.getTestData();
						testData.put("StartDate", dateStart)
	                    		.put("EndDate", dateEnd)
	                    		.put("BaseCurrency", initialCurrency)
	                    		.put("TargetCurrencies", targetCurrencies);
						testContext.setTestData(testData);
						log.info("Added additional test data: " + testContext.getTestData().toString());
					}
			}
			@Cleanup Response response = client.newCall(request).execute();
			testContext.setResponse(response);
			testContext.setJSONBody(response.body().string());
		} catch (Exception e) {
			throw new Exception("\nIssue generating request for type: " + requestType + " with paramaters - Endpoint: " + testContext.getEndpoint() + ", Start Date: " + dateStart + ", End Date: " + dateEnd + ", and optional values - Initial Currency: " + initialCurrency + ", Target Currencies: " + targetCurrencies + ". \nOrginal error: " + e.getMessage());
		}
	}

	public void tooManyRequests(int timesExecuted) {
		log.info("Executing request " + timesExecuted + " times");
		int executedXTimes = 0;
    	try {
	    	for (int x = 0; x <= timesExecuted; x++) {
	    		executedXTimes++;
	    		executeDefaultRequest("GET", null, null);
	    		log.debug("Response code: " + testContext.getResponse().code());
	    		Validate.isTrue(ResponseCode.getResponseCode("TOOMANYREQUESTS")!= testContext.getResponse().code());
	    	}
	    	log.debug("Executed request: " + executedXTimes + " times without metting any exeptions");
    	} catch (Exception e) {
    		log.debug("Executed request: " + executedXTimes + " time(s) before getting expected response.");
    	}
    }

	public void verifyResponse(@NonNull String expectedResponse) throws Exception {
		logStatus();
		Assert.assertEquals(ResponseCode.getResponseCode(expectedResponse), testContext.getResponse().code());
	}

	public void confirmServerSideErrors(@NonNull String serverErrorCode, @NonNull String errorMessageType, @NonNull String message) throws Exception {
		if (serverErrorCode.replaceAll("\\s", "").equals("-")) {
			Assert.assertFalse("Request expected to end with no errors, but ended with: " + errorMessageType, testContext.getResponse().toString().contains("error"));		
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			ErrorResponse errorRestAPIResponse = null;
			logStatus();
			try {
				errorRestAPIResponse = objectMapper.readValue(testContext.getJSONBody(), ErrorResponse.class);
			} catch (Exception e) {
				log.error("Experienced problem with parsing body with mapper for ErrorResponse");
				throw new Exception("Failed to map JSON with ErrorResponse mapper. Error: " + e.getMessage());
			}
			notNullEquals(serverErrorCode, errorRestAPIResponse.getError().get("code").asText());
			notNullEquals(errorMessageType, errorRestAPIResponse.getError().get("type").asText());
			Assert.assertTrue(errorRestAPIResponse.getError().get("info").asText().matches(message + ".*$"));
		}
	}

	public void verifyCurrencyResponseTemplate() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ConvertCurrencyResponse convertCurrencyAPIResponse = null;
		logStatus();
		try {
			convertCurrencyAPIResponse = objectMapper.readValue(testContext.getJSONBody(), ConvertCurrencyResponse.class);
		} catch (Exception e) {
			log.error("Experienced problem with parsing body with mapper for ConvertCurrencyResponse");
			try {
				ErrorResponse response = objectMapper.readValue(testContext.getJSONBody(), ErrorResponse.class);
				throw new Exception("Expected currencies from a server, but error returned: " + response.getError());
			} catch (Exception e1) {
				throw new Exception("Failed to map JSON with ConvertCurrencyResponse mapper. Error: " + e.getMessage());
			}
		}
		notNullEquals(testContext.getTestData().get("StartDate").toString(), convertCurrencyAPIResponse.getStartDate());
		notNullEquals(testContext.getTestData().get("EndDate").toString(), convertCurrencyAPIResponse.getEndDate());
		notNullEquals(testContext.getTestData().get("BaseCurrency").toString(), convertCurrencyAPIResponse.getBaseCurrency());
		Assert.assertNotNull(convertCurrencyAPIResponse.getRates());
		Assert.assertNotNull(convertCurrencyAPIResponse.getRates().get(convertCurrencyAPIResponse.getStartDate()));
		validateReceivedCurrencyRates(convertCurrencyAPIResponse);
	}
	
	public void validateReceivedCurrencyRates(ConvertCurrencyResponse convertCurrencyAPIResponse) {
		log.info("Validating received currency rates.");
		JsonNode currencyRates = convertCurrencyAPIResponse.getRates().get(convertCurrencyAPIResponse.getStartDate());
		String[] expectedCurrencies = testContext.getTestData().get("TargetCurrencies").toString().split(",");
		for (String expectedCurrency: expectedCurrencies) {
			log.info("Expecting rate for currency(" + expectedCurrency + ") - " + ((currencyRates.get(expectedCurrency) != null)? "Found with value: " + currencyRates.get(expectedCurrency) : "But was not found."));
			Assert.assertTrue(currencyRates.get(expectedCurrency).toString().matches("(\\d*\\.\\d{1,6})$"));
		}
		log.info("Completed validating response against ConvertCurrencyResponse template. All entries are matching monetary value pattern.");
	}
}