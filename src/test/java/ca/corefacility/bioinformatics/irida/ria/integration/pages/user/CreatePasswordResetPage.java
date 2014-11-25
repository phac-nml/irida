package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class CreatePasswordResetPage extends AbstractPage {
	private final String SUCCESS_PAGE = BASE_URL + "password_reset/created/.+";

	public CreatePasswordResetPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, "password_reset/");
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
