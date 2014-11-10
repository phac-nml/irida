package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class PasswordResetPage extends AbstractPage {
	private final String RELATIVE_URL = "password_reset/";
	private final String SUCCESS_PAGE = RELATIVE_URL + "success/.+";

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

		driver.findElement(By.className("submit")).click();
	}

	public boolean checkSuccess() {
		return driver.getCurrentUrl().matches(BASE_URL + SUCCESS_PAGE);
	}
}
