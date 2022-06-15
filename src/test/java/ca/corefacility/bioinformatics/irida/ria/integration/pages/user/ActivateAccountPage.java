package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ActivateAccountPage extends AbstractPage {
	private final String RESET_PASSWORD_URL = "password_reset/";

	public ActivateAccountPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		// Go to the login page
		get(driver, "/");
	}

	public void clickActivateAccountLink() {
		driver.findElement(By.className("t-activate-account-link")).click();
	}

	public void enterActivationID(String identifier) {
		WebElement element = driver.findElement(By.id("activationId"));
		element.sendKeys(identifier);
	}

	public void clickSubmit() {
		driver.findElement(By.className("t-submit-btn")).click();
	}

	public boolean isErrorAlertDisplayed() {
		try {
			return driver.findElement(By.className("t-activation-id-error-alert")).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean passwordResetPageDisplayed(String key) {
		String url = driver.getCurrentUrl();
		if(url.contains(RESET_PASSWORD_URL+key)) {
			return true;
		}
		return false;
	}

}
