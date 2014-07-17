package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PasswordResetPage {
	private final String SUCCESS_PAGE = "http://localhost:8080/password_reset/success/.+";
	private WebDriver driver;

	public PasswordResetPage(WebDriver driver) {
		this.driver = driver;
	}

	public void getPasswordReset(String key) {
		driver.get("http://localhost:8080/password_reset/" + key);
	}

	public void enterPassword(String password, String confirmPassword) {
		WebElement passwordElement = driver.findElement(By.id("password"));
		WebElement confirmElement = driver.findElement(By.id("confirmPassword"));
		passwordElement.sendKeys(password);
		confirmElement.sendKeys(confirmPassword);

		driver.findElement(By.className("submit")).click();
	}

	public boolean checkSuccess() {
		return driver.getCurrentUrl().matches(SUCCESS_PAGE);
	}
}
