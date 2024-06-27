package Backend;

import Abstract.TestContext;
import Backend.stepdefinitions.Okhttp3RestRequests;
import Backend.stepdefinitions.RestAssuredRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;

public class RestAPISteps {
    private final Okhttp3RestRequests restRequests;
    private final RestAssuredRequest restAssuredRequests;

    public RestAPISteps(TestContext testContext) {
        this.restRequests = new Okhttp3RestRequests(testContext);
        this.restAssuredRequests = new RestAssuredRequest(testContext);
    }

    @Given("the API endpoint is {}")
    public void setAPIEndpoint(String endpoint) {
    	restRequests.setEndpoint(endpoint);
    }

    @Given("the APIKey is {}")
    public void setAPIKey(String APIKey) {
    	restRequests.setAPIKey(APIKey);
    }

    @When("you send a basic {} request to the endpoint")
    public void executePatchRequest(String requestType) throws Exception {
    	restRequests.executeDefaultRequest(requestType, null, null);
    }

    @When("you send a basic GET request to the endpoint {} times")
    public void tooManyRequestsTest(int timesExecuted) throws IOException {
    	restRequests.tooManyRequests(timesExecuted);
    }

    @When("you send a basic GET request to the endpoint with start date {} and end date {}")
    public void smokeTest(String startDate, String endDate) throws IOException {
    	restRequests.timeseriesRequest("GET", startDate, endDate);
    }

    @When("you send GET request with input {} and expected output {}")
    public void testCurrencyConversion(String initialCurrency, String targetCurrencies) throws IOException {
    	restRequests.timeseriesRequest("GET", Okhttp3RestRequests.todaysDate(), Okhttp3RestRequests.todaysDate(), initialCurrency, targetCurrencies);
    }

    @Then("verify sending incorrectly constructed request to the endpoint")
    public void executeBadRequest() throws Exception{
    	restAssuredRequests.executeBadRequest();
    }

    @Then("request ends with {} response")
    public void verifyResponse(String expectedResponse) throws Exception {
    	restRequests.verifyResponse(expectedResponse);
    }

    @Then("confirm {} and {} with message starting with {}")
    public void confirmServerSideErrors(String serverErrorCode, String errorMessageType, String message) throws Exception {
    	restRequests.confirmServerSideErrors(serverErrorCode, errorMessageType, message);
    }

    @Then("compare response to expected response template")
    public void verifyCurrencyResponseTemplate() throws Exception {
    	restRequests.verifyCurrencyResponseTemplate();
    }
}