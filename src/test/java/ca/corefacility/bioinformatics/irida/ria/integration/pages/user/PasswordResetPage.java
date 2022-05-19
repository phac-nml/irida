package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class PasswordResetPage extends AbstractPage {
	private final String RELATIVE_URL = "password_reset/";

	public PasswordResetPage(WebDriver driver) {
		super(driver);
	}

	public void getPasswordReset(String key) {
		get(driver, RELATIVE_URL + key);
	}

	public void enterPassword(String password) {
		WebElement passwordElement = driver.findElement(By.id("password"));
		passwordElement.sendKeys(password);
	}

	public void clickSubmit() {
		driver.findElement(By.className("t-submit-btn")).click();
	}

	public boolean checkSuccess() {
		try {
			WebElement el = waitForElementVisible(By.className("t-reset-success-alert"));
			return el.getText().contains("Password successfully updated. You may use your new credentials to log in to IRIDA.");
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isErrorAlertDisplayed() {
		try {
			return driver.findElement(By.className("t-reset-error-alert")).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}
}
