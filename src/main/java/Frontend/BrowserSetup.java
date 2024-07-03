package Frontend;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BrowserSetup {
	public static WebDriver driver;
	private static final Logger log = LoggerFactory.getLogger(BrowserSetup.class);

	public static WebDriver setUp() {
		WebDriver driver = null;
		try {
			log.debug("Starting ChromeDriver version 126.0.0...");
	        WebDriverManager.chromedriver().browserVersion("126.0.0").setup();
	  	    driver = new ChromeDriver();
	  	} catch (Error e) {
	  		throw new Error ("Failed to initialize ChromeDriver " + e.getMessage());
	  	} finally {
	  		log.debug("ChromeDriver initialized successfully.");
	  	}
		return driver;
	}
	
	public static void tearDown(){
        driver.quit();
    }
}