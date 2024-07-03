package Frontend;

import java.time.Duration;
import java.util.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

public class BrowserSteps {
	public final WebDriver driver;
    public final static Integer timeout = 60;
    
    public BrowserSteps() {
        this.driver = BrowserSetup.setUp();
    }
	
	public static String javascriptExecutor(WebDriver driver, String script) {
        return ((JavascriptExecutor) driver).executeScript(script).toString();
    }

    public static WebElement getWebElement(WebDriver driver, String jQuerySelector) {
        return (WebElement) ((JavascriptExecutor) driver).executeScript(jQuerySelector + ".get(0);");
    }
	
    public static WebElement waitForElement(WebDriver driver, By selector) {
	    FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
	            .withTimeout(Duration.ofSeconds(timeout * 100))
	            .pollingEvery(Duration.ofSeconds(1))
	            .ignoring(NoSuchElementException.class);
	    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
	    return element;
    }
    
    public static void waitForElementToDissapear(WebDriver driver, By selector) {
	    FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
	            .withTimeout(Duration.ofSeconds(timeout * 100))
	            .pollingEvery(Duration.ofSeconds(1))
	            .ignoring(NoSuchElementException.class);
	    wait.until(ExpectedConditions.invisibilityOf(driver.findElement(selector)));
    }
}
