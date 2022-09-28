package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("t-forgot-password-alert")));
			WebElement element = driver.findElement(By.className("t-forgot-password-alert"));
			wait.until(ExpectedConditions.textToBePresentInElement(element,
					"Check your email for password reset instructions"));
			return element.getText().contains("Check your email for password reset instructions");
		} catch (Exception e) {
			return false;
		}
	}

	public boolean checkAccountDisabledMessage() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10L);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("t-forgot-password-alert")));
			WebElement element = driver.findElement(By.className("t-forgot-password-alert"));
			wait.until(ExpectedConditions.textToBePresentInElement(element,
					"If a user with the provided email address or username exists, and is enabled, you will receive an email with password reset instructions."));
			return element.getText()
					.contains(
							"If a user with the provided email address or username exists, and is enabled, you will receive an email with password reset instructions.");
		} catch (Exception e) {
			return false;
		}
	}
}
