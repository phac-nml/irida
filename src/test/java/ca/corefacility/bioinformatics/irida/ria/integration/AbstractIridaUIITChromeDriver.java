package ca.corefacility.bioinformatics.irida.ria.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.IridaApplication;
import ca.corefacility.bioinformatics.irida.config.IridaIntegrationTestUriConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestFilesystemConfig;
import ca.corefacility.bioinformatics.irida.junit5.listeners.IntegrationUITestListener;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.utils.NullReplacementDatasetLoader;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.google.common.base.Strings;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Common functionality to all UI integration tests.
 */
@Tag("IntegrationTest")
@Tag("UI")
@ActiveProfiles("it")
@SpringBootTest(classes = { IridaApplication.class, IridaApiTestFilesystemConfig.class },
		webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(IridaIntegrationTestUriConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
@DbUnitConfiguration(dataSetLoader = NullReplacementDatasetLoader.class)
public class AbstractIridaUIITChromeDriver {

	private static final Logger logger = LoggerFactory.getLogger(AbstractIridaUIITChromeDriver.class);

	public static final int DRIVER_TIMEOUT_IN_SECONDS = IntegrationUITestListener.DRIVER_TIMEOUT_IN_SECONDS;

	private static boolean isSingleTest = false;

	private static final String CHROMEDRIVER_PROP_KEY = "webdriver.chrome.driver";
	private static final String CHROMEDRIVER_LOCATION = "src/main/webapp/chromedriver";

	@RegisterExtension
	public ScreenshotOnFailureWatcher watcher = new ScreenshotOnFailureWatcher();

	/**
	 * Code to execute *once* after the class is finished.
	 */
	@AfterAll
	public static void destroy() {
		if (isSingleTest) {
			logger.debug("Closing ChromeDriver for single test class.");
			IntegrationUITestListener.stopWebDriver();
		}
	}

	/**
	 * Get a reference to the {@link WebDriver} used in the tests.
	 *
	 * @return the instance of {@link WebDriver} used in the tests.
	 */
	public static WebDriver driver() {
		if (IntegrationUITestListener.driver() == null) {
			final String chromeDriverProp = System.getProperty(CHROMEDRIVER_PROP_KEY);
			System.setProperty(CHROMEDRIVER_PROP_KEY,
					Strings.isNullOrEmpty(chromeDriverProp) ? CHROMEDRIVER_LOCATION : chromeDriverProp);
			logger.debug("Starting ChromeDriver for a single test class. Using `chromedriver` at '"
					+ System.getProperty(CHROMEDRIVER_PROP_KEY) + "'");
			isSingleTest = true;
			IntegrationUITestListener.startWebDriver();
		}

		return IntegrationUITestListener.driver();

	}

	/**
	 * Method to use on any page to check to ensure that internationalization messages are being automatically loaded
	 * onto the page.
	 *
	 * @param page    - the instance of {@link AbstractPage} to check for internationalization.
	 * @param entries - a {@link List} of bundle names. This will correspond to the loaded webpack bundles.
	 * @param header  - Expected text for the main heading on the page. Needs to have class name `t-main-heading`
	 */
	public void checkTranslations(AbstractPage page, List<String> entries, String header) {
		// Always check for app :)
		assertTrue(page.ensureTranslationsLoaded("app"), "Translations should be loaded for the  app bundle");
		entries.forEach(entry -> assertTrue(page.ensureTranslationsLoaded(entry),
				"Translations should be loaded for " + entry + " bundle"));
		if (!Strings.isNullOrEmpty(header)) {
			assertTrue(page.ensurePageHeadingIsTranslated(header), "Page title has been properly translated");
		}
	}

	/**
	 * Simple test watcher for taking screenshots of the browser on failure.
	 */
	private static class ScreenshotOnFailureWatcher implements TestWatcher {

		private static final Logger logger = LoggerFactory.getLogger(ScreenshotOnFailureWatcher.class);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void testFailed(ExtensionContext context, Throwable t) {
			logger.debug("Handling exception of type [" + t.getClass() + "], taking screenshot: " + t.getMessage(), t);
			final TakesScreenshot takesScreenshot = (TakesScreenshot) driver();

			final Path screenshot = Paths.get(takesScreenshot.getScreenshotAs(OutputType.FILE).toURI());

			try {
				final Path destination = Files.createTempFile("irida-" + context.getRequiredTestClass().getSimpleName()
						+ "#" + context.getRequiredTestMethod().getName(), ".png");
				Files.move(screenshot, destination, StandardCopyOption.REPLACE_EXISTING);
				logger.info("Screenshot deposited at: [" + destination.toString() + "]");
			} catch (final IOException e) {
				logger.error("Unable to write screenshot out.", e);
			}
			testFinished();
		}

		@Override
		public void testSuccessful(ExtensionContext context) {
			testFinished();
		}

		private void testFinished() {
			LoginPage.logout(driver());
		}
	}
}
