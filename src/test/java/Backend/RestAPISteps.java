package Backend;

import Abstract.TestContext;
import Backend.stepdefinitions.CommonSteps;
import Backend.stepdefinitions.Okhttp3RestRequests;
import Backend.stepdefinitions.RestAssuredRequests;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;

public class RestAPISteps {
    private final RestAssuredRequests restAssuredRequests;
    private final Okhttp3RestRequests restRequests;
    private final CommonSteps commonSteps;

    public RestAPISteps(TestContext testContext) {
        this.restAssuredRequests = new RestAssuredRequests(testContext);
        this.restRequests = new Okhttp3RestRequests(testContext);
        this.commonSteps = new CommonSteps(testContext);
    }

    @Given("the API endpoint is {}")
    public void setAPIEndpoint(String endpoint) {
    	commonSteps.setEndpoint(endpoint);
    }

    @Given("the APIKey is {}")
    public void setAPIKey(String APIKey) {
    	commonSteps.setAPIKey(APIKey);
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
    public void smokeTest(String startDate, String endDate) throws Exception {
    	restRequests.timeseriesRequest("GET", startDate, endDate);
    }

    @When("you send GET request with base currency {} and output currencies {}")
    public void testCurrencyConversion(String initialCurrency, String targetCurrencies) throws Exception {
    	restRequests.timeseriesRequest("GET", restRequests.getTodaysDate(), restRequests.getTodaysDate(), initialCurrency, targetCurrencies);
    }

    @Then("request ends with {} response")
    public void verifyResponse(String expectedResponse) throws Exception {
    	restRequests.verifyResponse(expectedResponse);
    }

    @Then("confirm {} and {} with message starting with {}")
    public void confirmServerSideErrors(String serverErrorCode, String errorMessageType, String message) throws Exception {
    	restRequests.confirmServerSideErrors(serverErrorCode, errorMessageType, message);
    }

    @Then("compare response to saved expected response template")
    public void verifyCurrencyResponseTemplate() throws Exception {
    	restRequests.verifyCurrencyResponseTemplate();
    }

    @Then("verify sending incorrectly constructed request to the endpoint")
    public void executeBadRequest() throws Exception{
    	restAssuredRequests.executeBadRequest(restRequests.getTodaysDate(), restRequests.getTodaysDate());
    }
}