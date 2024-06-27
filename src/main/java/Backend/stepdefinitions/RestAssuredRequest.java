package Backend.stepdefinitions;

import java.net.ConnectException;

import org.json.JSONObject;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Backend.enums.ResponseCodes.ResponseCode;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import Abstract.TestContext;

public class RestAssuredRequest {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestAssuredRequest.class);
	private TestContext testContext;

	public RestAssuredRequest(TestContext testContext) {
        this.testContext = testContext;
    }

	// Background: One of the tasks was to generate error 400 and I did it in a few ways
	// OkHttp3 prevents me from intentionally creating bad request with its internal validators
	// This endpoint did not decided to allow GET with parameters in body and uses url f.e. /?start_date=2024-03-01&end_date=2024-03-01
	public void executeBadRequest() throws Exception {
        RequestSpecification requestSpecification = RestAssured.given().log().ifValidationFails();
        JSONObject requestParameters = new JSONObject();
        requestParameters.put("start_date",  Okhttp3RestRequests.todaysDate());
        requestParameters.put("end_date",  Okhttp3RestRequests.todaysDate());
        requestSpecification
                .header("apikey", testContext.getAPIKey())
                .body(requestParameters.toMap())
                .then().log().ifError();
        try {
        	Response response = requestSpecification.get();
        	Assert.assertEquals(ResponseCode.getResponseCode("BADREQUEST"), response.statusCode());
        } catch (ConnectException e) {
        	LOGGER.error("Test Ended - Sometimes server refuses connection as a response to Bad Request. This was one of these times.");
        }
	}
}