package ca.corefacility.bioinformatics.irida.web.controller.test.listeners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

import java.util.concurrent.TimeUnit;

/**
 * Global settings for all integration tests.
 * 
 */
public class IntegrationTestListener extends RunListener {
	private static final Logger logger = LoggerFactory.getLogger(IntegrationTestListener.class);
	public static final int DRIVER_TIMEOUT_IN_SECONDS = 3;

	private static WebDriver driver;

	/**
	 * {@inheritDoc}
	 */
	public void testRunStarted(Description description) throws Exception {

		driver = new ChromeDriver();
		driver.manage().window().setSize(new Dimension(1400, 900));
		driver.manage().timeouts().implicitlyWait(DRIVER_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

		logger.debug("Setting up RestAssured.");
		RestAssured.requestContentType(ContentType.JSON);
		RestAssured.port = Integer.valueOf(System.getProperty("jetty.port"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void testRunFinished(Result result) throws Exception {
		driver.quit();
	}

	/**
	 * Get a reference to the {@link WebDriver} used in the tests.
	 * @return the instance of {@link WebDriver} used in the tests.
	 */
	public static WebDriver driver() {
		return driver;
	}
}