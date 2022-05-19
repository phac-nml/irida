package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class CreatePasswordResetPage extends AbstractPage {

	public CreatePasswordResetPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		// Go to the login page
		get(driver, "/");
	}

	public void clickForgotPasswordLink() {
		driver.findElement(By.className("t-forgot-password-link")).click();
	}

	public void enterEmail(String email) {
		WebElement emailElement = driver.findElement(By.id("usernameOrEmail"));
		emailElement.sendKeys(email);

		driver.findElement(By.className("t-submit-btn")).click();
	}

	public boolean checkSuccess() {
		try {
			WebElement el = waitForElementVisible(By.className("t-forgot-password-alert"));
			return el.getText().contains("Check your email for password reset instructions");
		} catch (Exception e) {
			return false;
		}
	}
}
