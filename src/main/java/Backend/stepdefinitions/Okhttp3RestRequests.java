package Backend.stepdefinitions;

import Backend.ObjectMappers.ConvertCurrencyResponse;
import Backend.ObjectMappers.ErrorResponse;
import Backend.enums.ResponseCodes.ResponseCode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import Abstract.TestContext;
import org.junit.Assert;

public class Okhttp3RestRequests {
	private static final Logger LOGGER = LoggerFactory.getLogger(Okhttp3RestRequests.class);
	private TestContext testContext;

	public Okhttp3RestRequests(TestContext testContext) {
        this.testContext = testContext;
    }

	public void setEndpoint(String endpoint) {
		this.testContext.setEndpoint(endpoint);
	}

	public void setAPIKey(String APIKey) {
		this.testContext.setAPIKey(APIKey);
	}

	public void executeDefaultRequest(String requestType, String startDate, String endDate) throws IOException {
		timeseriesRequest(requestType, (startDate == null)? todaysDate() : startDate, (endDate == null)? todaysDate() : endDate);
	}

	public void timeseriesRequest(String requestType, String dateStart, String dateEnd) throws IOException {
		timeseriesRequest(requestType, dateStart, dateEnd, null, null);
	}

	public void timeseriesRequest(String requestType, String dateStart, String dateEnd, String initialCurrency, String targetCurrencies) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request; RequestBody body;

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
					LOGGER.info("Added additional test data: " + testContext.getTestData().toString());
				}
		}

		Response response = client.newCall(request).execute();
		testContext.setResponse(response);
		testContext.setJSONBody(response.body().string());
	}

	public void tooManyRequests(int timesExecuted) {
		LOGGER.info("Executing request " + timesExecuted + " times");
    	int executedXTimes = 0;
    	try {
	    	for (int x = 0; x <= timesExecuted; x++) {
	    		executedXTimes++;
	    		executeDefaultRequest("GET", null, null);
	    		LOGGER.info("Response code: " + testContext.getResponse().code());
	    		Validate.isTrue(ResponseCode.getResponseCode("TOOMANYREQUESTS")!= testContext.getResponse().code());
	    	}
	    	LOGGER.debug("Executed request: " + executedXTimes + " times without metting any exeptions");
    	} catch (Exception e) {
    		LOGGER.debug("Executed request: " + executedXTimes + " time(s) before getting expected response.");
    	}
    }

	public void verifyResponse(String expectedResponse) throws Exception {
		expectedResponse = expectedResponse.replaceAll("\\s", "").toUpperCase();
		LOGGER.info("\n    Request response: " + testContext.getResponse().toString());
		LOGGER.info("Request body:\n" + testContext.getJSONBody().trim());
		Assert.assertEquals(ResponseCode.getResponseCode(expectedResponse), testContext.getResponse().code());
	}

	public void confirmServerSideErrors(String serverErrorCode, String errorMessageType, String message) throws Exception {
		if (serverErrorCode.replaceAll("\\s", "").equals("-")) {
			Assert.assertFalse("Request expected to end with no errors, but ended with: " + errorMessageType, testContext.getResponse().toString().contains("error"));		
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			ErrorResponse errorResponse = null;
			LOGGER.info("\n    Request response: " + testContext.getResponse().toString());
			LOGGER.info("Request body:\n" + testContext.getJSONBody().trim());
			Assert.assertNotNull(testContext.getJSONBody());

			try {
				errorResponse = objectMapper.readValue(testContext.getJSONBody(), ErrorResponse.class);
			} catch (Exception e) {
				LOGGER.error("Experienced problem with parsing body with mapper for ErrorResponse");
				throw new Exception("Failed to map JSON with ErrorResponse mapper. Error: " + e.getMessage());
			}

			Assert.assertNotNull(errorResponse.getError().get("code"));
			Assert.assertEquals(serverErrorCode, errorResponse.getError().get("code").asText());
			Assert.assertNotNull(errorResponse.getError().get("type"));
			Assert.assertEquals(errorMessageType, errorResponse.getError().get("type").asText());
			Assert.assertNotNull(errorResponse.getError().get("info"));
			Assert.assertTrue(errorResponse.getError().get("info").asText().matches(message + ".*$"));
		}
	}

	public void verifyCurrencyResponseTemplate() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ConvertCurrencyResponse convertCurrencyResponse = null;
		LOGGER.info("\n    Request response: " + testContext.getResponse().toString());
		LOGGER.info("Request body:\n" + testContext.getJSONBody().trim());
		Assert.assertNotNull(testContext.getJSONBody());

		try {
			convertCurrencyResponse = objectMapper.readValue(testContext.getJSONBody(), ConvertCurrencyResponse.class);
		} catch (Exception e) {
			LOGGER.error("Experienced problem with parsing body with mapper for ConvertCurrencyResponse");
			throw new Exception("Failed to map JSON with ConvertCurrencyResponse mapper. Error: " + e.getMessage());
		}

		Assert.assertNotNull(convertCurrencyResponse.getStartDate());
		Assert.assertEquals(testContext.getTestData().get("StartDate").toString(), convertCurrencyResponse.getStartDate());
		Assert.assertNotNull(convertCurrencyResponse.getEndDate());
		Assert.assertEquals(testContext.getTestData().get("EndDate").toString(), convertCurrencyResponse.getEndDate());
		Assert.assertNotNull(convertCurrencyResponse.getBaseCurrency());
		Assert.assertEquals(testContext.getTestData().get("BaseCurrency").toString(), convertCurrencyResponse.getBaseCurrency());
		Assert.assertNotNull(convertCurrencyResponse.getRates());
		Assert.assertNotNull(convertCurrencyResponse.getRates().get(convertCurrencyResponse.getStartDate()));
		JsonNode currencyRates = convertCurrencyResponse.getRates().get(convertCurrencyResponse.getStartDate());
		String[] expectedCurrencies = testContext.getTestData().get("TargetCurrencies").toString().split(",");
		for (String expectedCurrency: expectedCurrencies) {
			LOGGER.info("Expecting rate for currency(" + expectedCurrency + ") - " + ((currencyRates.get(expectedCurrency) != null)? "Found with value: " + currencyRates.get(expectedCurrency) : "But was not found."));
			Assert.assertTrue(currencyRates.get(expectedCurrency).toString().matches("(\\d*\\.\\d{6})$"));
		}
		LOGGER.info("Comparing currency conversion response against a basic template and validation completed.");
	}

	public static String todaysDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        return dateFormat.format(today);
	}
}
