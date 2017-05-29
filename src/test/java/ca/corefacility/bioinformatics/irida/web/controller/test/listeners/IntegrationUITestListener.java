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
		String noSandbox = System.getenv("irida.it.nosandbox");
		if (noSandbox != null && noSandbox.equals("true")) {
			logger.debug("NO SANDBOX MODE");
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