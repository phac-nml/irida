package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import static org.junit.Assert.*;

/**
 * Represents the common elements in a page within the application.
 *
 */
public class AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(AbstractPage.class);
	protected static final String BASE_URL = System.getProperty("server.base.url", "http://localhost:" + System.getProperty("jetty.port", "8080")) + "/";
	protected static final Long TIME_OUT_IN_SECONDS = 10L;

	protected final int DEFAULT_WAIT = 500;

	@FindBy(className = "error")
	private WebElement errors;

	protected WebDriver driver;

	public AbstractPage(WebDriver driver) {
		setDriver(driver);
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public String getErrors() {
		return errors.getText();
	}

	protected static void get(WebDriver driver, String relativeUrl) {
		String url = BASE_URL + relativeUrl;
		driver.get(url);
		// Check to make sure that there is no server error
		try {
			WebElement main = driver.findElement(By.tagName("main"));
			String error = main.getAttribute("data-error");
			if (!Strings.isNullOrEmpty(error)) {
				determineError(error);
			}
		} catch (NoSuchElementException e) {
			logger.trace("Did not find `main` element on page when checking for errors, everything is *probably* OK.");
		}
	}

	private static void determineError(String error) {
		assertFalse("A server error occured", error.equals("server"));
		assertFalse("An oauth error occured", error.equals("oauth"));
		assertFalse("An access denied error occured", error.equals("access_denied"));
		assertFalse("An item not found error occured", error.equals("404"));
	}

	public static void logout(WebDriver driver) {
		driver.get(BASE_URL + "logout");
	}

	public WebElement waitForElementToBeClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS);
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public void clickElement(By finder) {
		boolean clicked = false;
		do {
			try {
				final WebElement el = driver.findElement(finder);
				waitForElementToBeClickable(el);
				el.click();
				clicked = true;
			} catch (final StaleElementReferenceException ex) {
				logger.debug("Got stale element reference exception when clicking launch pipeline, trying again.");
			}
		} while (!clicked);
	}

	public WebElement waitForElementVisible(By locator) {
		WebDriverWait wait = new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS);
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public Collection<WebElement> waitForElementsVisible(By locator) {
		new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS)
				.until(ExpectedConditions.visibilityOfElementLocated(locator));
		return driver.findElements(locator);
	}

	public void waitForElementInvisible(By locator) {
		(new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS))
				.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public static void waitForTime(int length) {
		try {
			Thread.sleep(length);
		} catch (InterruptedException e) {
			logger.error("Cannot sleep the thread.");
		}
	}

	public WebElement openSelect2List(WebDriver driver) {
		driver.findElement(By.className("select2-choice")).click();
		waitForElementVisible(By.className("select2-results"));
		return driver.findElement(By.cssSelector(".select2-input"));
	}

	public boolean isElementOnScreen(String id) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean exists = driver.findElements(By.id(id)).size() != 0;
		driver.manage().timeouts().implicitlyWait(AbstractIridaUIITChromeDriver.DRIVER_TIMEOUT_IN_SECONDS,
				TimeUnit.SECONDS);
		return exists;
	}

	// Cart is available on all pages.
	public boolean isCartCountVisible() {
		// Only displays count if count > 0
		return driver.findElement(By.id("cart-count")).isDisplayed();
	}

	public int getCartCount() {
		return Integer.parseInt(driver.findElement(By.className("js-cart-count")).getText());
	}

	/**
	 * Search globally for a query
	 *
	 * @param query
	 * 		the query to search for
	 * @param admin
	 * 		whether to search as an admin
	 */
	public void globalSearch(String query, boolean admin) {
		WebElement searchBox = driver.findElement(By.id("global-search-input"));
		searchBox.clear();
		searchBox.sendKeys(query);
		if (!admin) {
			searchBox.sendKeys(Keys.ENTER);
		} else {
			driver.findElement(By.id("admin-search-toggle")).click();
			driver.findElement(By.id("search-admin-link")).click();
		}
	}

	public void showCart() {
		driver.findElement(By.id("cart-show-btn")).click();
		waitForTime(500);
	}

	public void clearCart() {
		driver.findElement(By.id("clear-cart-btn")).click();
		waitForTime(250);
	}

	public void removeProjectFromCart(Long projectId) {
		driver.findElement(By.id("remove-project-" + projectId)).click();
		waitForTime(250);
	}

	public void removeFirstSampleFromProjectInCart(Long projectId) {
		WebElement projectItem = driver.findElement(By.id("cart-project-" + projectId));
		List<WebElement> sampleRemoveButtons = projectItem.findElements(By.className("remove-sample-btn"));
		WebElement firstSampleRemoveBtn = sampleRemoveButtons.iterator().next();
		firstSampleRemoveBtn.click();
		waitForTime(500);
	}

	public boolean isCartVisible() {
		return driver.findElements(By.className("cart-open")).size() > 0;
	}

	public int getCartProjectCount() {
		return driver.findElements(By.cssSelector("#cart-project-list > li.local-project")).size();
	}

	/**
	 * Test for breadcrumbs on any given page.
	 *
	 * @param expected
	 *            {@link List} containing {@link Map} of expected crumbs - href:
	 *            expected href - text: expected text displayed
	 */
	public void checkBreadCrumbs(List<Map<String, String>> expected) {
		List<WebElement> crumbs = driver.findElement(By.className("breadcrumbs")).findElements(By.tagName("a"));
		assertEquals("Should have the correct number of breadcrumbs", expected.size(), crumbs.size());
		for (int i = 0; i < crumbs.size(); i++) {
			WebElement crumb = crumbs.get(i);
			String href = crumb.getAttribute("href");
			String text = crumb.getText();
			assertTrue("Should have the epected url in the breadcrumb", href.contains(expected.get(i).get("href")));
			assertTrue("Should have the epected url in the breadcrumb", href.contains(expected.get(i).get("href")));
			assertEquals("Should have the epected text in the breadcrumb", expected.get(i).get("text"), text);
		}
	}

	/**
	 * Get the BASE URL
	 *
	 * @return
	 */
	public String getBaseUrl() {
		return BASE_URL;
	}

	/**
	 * Convenience method to make sure that form submission actually happens
	 * before proceeding to checking later steps.
	 *
	 * @param submitButton
	 *            the submit button to click.
	 */
	public void submitAndWait(final WebElement submitButton) {
		WebElement oldHtml = driver.findElement(By.tagName("html"));
		submitButton.click();
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS).until(ExpectedConditions.stalenessOf(oldHtml));
	}

	/**
	 * Wait for jQuery AJAX calls to complete on a page
	 */
	public void waitForJQueryAjaxResponse() {
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS)
				.until((ExpectedCondition<Boolean>) wd ->
						(Boolean) ((JavascriptExecutor) wd).executeScript("return jQuery.active == 0"));
	}

	/**
	 * Selenium is having issues sending complete sequences of strings to the UI.
	 * Sending one at a time might help.  See thread: https://github.com/angular/protractor/issues/698
	 * @param keys {@link String} value to send to the input
	 * @param inputElement {@link WebElement} input the send string to.
	 */
	protected void sendInputTextSlowly(String keys, WebElement inputElement) {
		for(int i = 0; i < keys.length(); i++) {
			String key = String.valueOf(keys.charAt(i));
			inputElement.sendKeys(key);
			waitForTime(200);
		}
	}

	/**
	 * Check if the '.t-submit-btn' is enabled
	 * @return if the '.t-submit-btn' is enabled
	 */
	public boolean isSubmitEnabled() {
		return driver.findElement(By.className("t-submit-btn")).isEnabled();
	}

	/**
	 * Check if there are any '.t-form-error' elements
	 * @return if there are any '.t-form-error' elements
	 */
	public boolean hasErrors() {
		return !driver.findElements(By.className("t-form-error")).isEmpty();
	}
}
