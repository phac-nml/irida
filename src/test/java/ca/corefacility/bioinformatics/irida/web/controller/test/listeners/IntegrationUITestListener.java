package ca.corefacility.bioinformatics.irida.web.controller.test.listeners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

import java.util.concurrent.TimeUnit;

/**
 * Global settings for UI integration tests.
 * 
 */
public class IntegrationUITestListener extends RunListener {
	private static final Logger logger = LoggerFactory.getLogger(IntegrationUITestListener.class);
	public static final int DRIVER_TIMEOUT_IN_SECONDS = 3;

	private static WebDriver driver;

	/**
	 * {@inheritDoc}
	 */
	public void testRunStarted(Description description) throws Exception {
		logger.debug("Running ChromeDriver for UI tests.");
		startWebDriver();
		
        logger.debug("Setting up RestAssured.");

        RestAssured.requestContentType(ContentType.JSON);
        RestAssured.port = Integer.valueOf(System.getProperty("jetty.port"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void testRunFinished(Result result) throws Exception {
		logger.debug("Closing ChromeDriver for UI tests.");
		stopWebDriver();
	}

	/**
	 * Get a reference to the {@link WebDriver} used in the tests.
	 * @return the instance of {@link WebDriver} used in the tests.
	 */
	public static WebDriver driver() {
		return driver;
	}

	public static void startWebDriver() {
		ChromeOptions options = new ChromeOptions();
		
		/*
		 * Run chrome in no sandbox mode. Only use this option for running tests
		 * in docker containers.
		 */
		String noSandbox = System.getProperty("irida.it.nosandbox");
		if (noSandbox != null && noSandbox.equals("true")) {
			logger.warn("Running Chrome in no sandbox mode");
			options.addArguments("--no-sandbox");
		}
		
		driver = new ChromeDriver(options);
		driver.manage().window().setSize(new Dimension(1400, 900));
		driver.manage().timeouts().implicitlyWait(DRIVER_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	}

	public static void stopWebDriver() {
		driver.quit();
	}
}