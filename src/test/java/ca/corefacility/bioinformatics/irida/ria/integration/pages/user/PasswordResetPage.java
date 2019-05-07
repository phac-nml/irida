package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

import java.util.List;

public class PasswordResetPage extends AbstractPage {
	private final String RELATIVE_URL = "password_reset/";

	public PasswordResetPage(WebDriver driver) {
		super(driver);
	}

	public void getPasswordReset(String key) {
		get(driver, RELATIVE_URL + key);
	}

	public void enterPassword(String password, String confirmPassword) {
		WebElement passwordElement = driver.findElement(By.id("password"));
		WebElement confirmElement = driver.findElement(By.id("confirmPassword"));
		passwordElement.sendKeys(password);
		confirmElement.sendKeys(confirmPassword);
	}

	public void clickSubmit() {
		submitAndWait(driver.findElement(By.className("t-submit-btn")));
	}

	public boolean checkSuccess() {
		try {
			WebElement el = waitForElementVisible(By.className("t-reset-success"));
			return el.getText().contains("Password successfully updated.");
		} catch (Exception e) {
			return false;
		}
	}
}
