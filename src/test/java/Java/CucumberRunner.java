package Java;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "summary", "io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm", "json:target/cucumber-reports/CucumberReport.json"},
        features = "src/test/resources/features",
        glue = {"Abstract"})
public class CucumberRunner {
	
}