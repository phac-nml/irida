package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Helper methods for finding items within the UI.
 *
 */
public class PageUtilities {
	public static final int TIME_OUT_IN_SECONDS = 10;
	private WebDriver driver;

	public PageUtilities(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Wait for an {@link org.openqa.selenium.WebElement} to be present on the screen. 10 seconds.
	 *
	 * @param locator {@link org.openqa.selenium.By}
	 */
	public void waitForElementPresent(By locator) {
		(new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS)).until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	/**
	 * Wait for an {@link org.openqa.selenium.WebElement} to be visible on the screen. 10 seconds.
	 *
	 * @param locator {@link org.openqa.selenium.By}
	 */
	public void waitForElementVisible(By locator) {
		(new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS)).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Wait for an {@link org.openqa.selenium.WebElement} to be invisible on the screen. 10 seconds.
	 *
	 * @param locator {@link org.openqa.selenium.By}
	 */
	public void waitForElementInvisible(By locator) {
		(new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS)).until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	/**
	 * Wait for an {@link org.openqa.selenium.WebElement} to be invisible on the screen. 10 seconds.
	 *
	 * @param locator {@link org.openqa.selenium.By}
	 */
	public void waitForElementToBeAbsent(By locator) {
		(new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS)).until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public void waitForElementToBeClickable(By locator){
		(new WebDriverWait(this.driver, TIME_OUT_IN_SECONDS)).until(ExpectedConditions.elementToBeClickable(locator));
	}

	public boolean checkSuccessNotification() {
		return checkNotyNotification("ant-message-success");
	}

	private boolean checkNotyNotification(String type) {
		boolean present = false;
		try {
			(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By
					.className(type)));
			present = true;
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}
}
