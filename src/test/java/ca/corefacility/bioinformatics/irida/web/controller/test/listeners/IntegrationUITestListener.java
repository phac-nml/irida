package ca.corefacility.bioinformatics.irida.web.controller.test.listeners;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

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
	@Override
	public void testRunStarted(Description description) throws Exception {
		logger.debug("Running ChromeDriver for UI tests.");
		startWebDriver();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunFinished(Result result) throws Exception {
		logger.debug("Closing ChromeDriver for UI tests.");
		stopWebDriver();
	}

	/**
	 * Get a reference to the {@link WebDriver} used in the tests.
	 *
	 * @return the instance of {@link WebDriver} used in the tests.
	 */
	public static WebDriver driver() {
		return driver;
	}

	/**
	 * Start the web driver.
	 */
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

		// Run chrome in headless mode
		String headless = System.getProperty("irida.it.headless");
		if (headless != null && headless.equals("true")) {
			logger.info("Running Chome in headless mode");
			options.addArguments("headless");
		} else {
			logger.info("Running Chome in no headless (normal) mode");
		}

		options.addArguments("--window-size=1920,1080");

		// Run selenium tests through external selenium server
		String seleniumUrl = System.getProperty("webdriver.selenium_url");
		if (seleniumUrl != null) {
			try {
				driver = new RemoteWebDriver(new URL(seleniumUrl), options);
			} catch (MalformedURLException e) {
				logger.error("webdriver.selenium_url is malformed", e);
				fail("Could not connect to the remote web driver at following url: " + seleniumUrl);
			}
		} else {
			driver = new ChromeDriver(options);
		}

		driver.manage().timeouts().implicitlyWait(DRIVER_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
	}

	public static void stopWebDriver() {
		driver.quit();
	}
}
