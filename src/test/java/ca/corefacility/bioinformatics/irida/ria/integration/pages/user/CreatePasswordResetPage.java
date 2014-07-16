package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreatePasswordResetPage {
	private final String SUCCESS_PAGE = "http://localhost:8080/password_reset/created/.+";
	private WebDriver driver;

	public CreatePasswordResetPage(WebDriver driver) {
		this.driver = driver;
		driver.get("http://localhost:8080/password_reset/");
	}


	public void enterEmail(String email) {
		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(email);

		driver.findElement(By.className("submit")).click();
	}

	public boolean checkSuccess() {
		return driver.getCurrentUrl().matches(SUCCESS_PAGE);
	}
}
