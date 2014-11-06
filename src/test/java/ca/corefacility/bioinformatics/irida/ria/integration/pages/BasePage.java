package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by josh on 14-08-06.
 */
public class BasePage {
	private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
	public static final String URL = "http://localhost:8080/";

	public static WebDriver initializeDriver() {
		WebDriver driver = new PhantomJSDriver();
		driver.manage().window().setSize(new Dimension(1024, 900));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LoginPage.login(driver, LoginPage.ADMIN_USERNAME, LoginPage.GOOD_PASSWORD);
		return driver;
	}

	public static WebDriver initializeChromeDriver() {
		WebDriver driver = new ChromeDriver();
		driver.manage().window().setSize(new Dimension(1024, 900));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LoginPage.login(driver, LoginPage.ADMIN_USERNAME, LoginPage.GOOD_PASSWORD);
		return driver;
	}

	public static void destroyDriver(WebDriver driver) {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}
	
	public static void logout(WebDriver driver){
		driver.get(URL+"/logout");
	}

	public static void waitForTime() {
		try {
			// There is a 500 ms pause on filtering names.
			Thread.sleep(700);
		} catch (InterruptedException e) {
			logger.error("Cannot sleep the thread.");
		}
	}
}
