package ca.corefacility.bioinformatics.irida.junit5.listeners;

import org.apache.commons.io.FileUtils;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

public class IntegrationUITestListener implements TestExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(IntegrationUITestListener.class);
	public static final int DRIVER_TIMEOUT_IN_SECONDS = 3;


	private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
	public static final File DOWNLOAD_DIRECTORY = new File(TEMP_DIRECTORY, "irida-test");

	private static WebDriver driver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testPlanExecutionStarted(TestPlan testPlan) {
		logger.debug("Running ChromeDriver for UI tests.");
		startWebDriver();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testPlanExecutionFinished(TestPlan testPlan) {
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
			logger.info("Running Chrome in headless mode");
			options.addArguments("headless");
		} else {
			logger.info("Running Chrome in no headless (normal) mode");
		}

		options.addArguments("--window-size=1920,1080");

		// Set up default download directory
		if (!DOWNLOAD_DIRECTORY.exists()) {
			DOWNLOAD_DIRECTORY.mkdir();
		}
		Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", DOWNLOAD_DIRECTORY.getAbsolutePath());
		options.setExperimentalOption("prefs", chromePrefs);

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

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DRIVER_TIMEOUT_IN_SECONDS));
	}


	public static void stopWebDriver() {
		// Clean up the download directory
		try {
			FileUtils.deleteDirectory(DOWNLOAD_DIRECTORY);
		} catch (IOException e) {
			logger.debug("Could not delete directory: ", DOWNLOAD_DIRECTORY.getAbsolutePath());
		}

		driver.quit();
	}
}
