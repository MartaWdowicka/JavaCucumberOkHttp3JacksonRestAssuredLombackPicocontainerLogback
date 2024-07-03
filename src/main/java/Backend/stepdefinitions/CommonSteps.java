package Backend.stepdefinitions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Abstract.ServerConfig;
import Abstract.TestContext;
import lombok.NonNull;

public class CommonSteps {
	private static final Logger log = LoggerFactory.getLogger(RestAssuredRequest.class);
	private ServerConfig config = ServerConfig.getServerConfig();
	private TestContext testContext;
	
	public CommonSteps(TestContext testContext) {
        this.testContext = testContext;
    }

	public void setEndpoint(@NonNull String endpoint) {
		switch (endpoint) {
		case "wrong": 
			this.testContext.setEndpoint(" ");
			break;
		case "correct":
		default:
			log.info("Using endpoint: " + config.getTimeseriesEndpoint());
			this.testContext.setEndpoint(config.getTimeseriesEndpoint());
		}
	}

	public void setAPIKey(@NonNull String APIKey) {
		switch (APIKey) {
		case "wrong": 
			this.testContext.setAPIKey(" ");
			break;
		case "depleted":
		    log.info("Using BurnerAPIKey: " + config.getBurnerAPIKey());
		    this.testContext.setAPIKey(config.getBurnerAPIKey());
		    break;
		case "correct":
		default:
			log.info("Using APIKey: " + config.getAPIKey1());
			this.testContext.setAPIKey(config.getAPIKey1());
		}
	}
}