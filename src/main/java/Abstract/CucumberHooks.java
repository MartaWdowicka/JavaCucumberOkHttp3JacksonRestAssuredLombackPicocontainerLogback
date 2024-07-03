package Abstract;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import Abstract.TestContext;
import Frontend.BrowserSetup;

@SuppressWarnings("unused")
public class CucumberHooks {
    private final TestContext testContext;

    public CucumberHooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before()
    public void setUp() {
    }

    @Before("@ui")
    public static void setUp(Scenario scenario) {
    	BrowserSetup.setUp();
    }

    @After("@ui")
    public static void tearDown() {
    	BrowserSetup.tearDown();
    }
}