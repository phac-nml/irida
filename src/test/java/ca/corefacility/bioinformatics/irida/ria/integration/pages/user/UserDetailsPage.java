package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * User details page for selenium testing
 */
public class UserDetailsPage extends AbstractPage {

	public static String DETAILS_PAGE = "users/1/details";

	public UserDetailsPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, DETAILS_PAGE);
	}

	public void enterFirstName(String newName) {
		WebElement firstNameBox = driver.findElement(By.id("firstName"));
		firstNameBox.sendKeys(newName);
	}

	public void enterEmail(String newEmail) {
		WebElement emailBox = driver.findElement(By.id("email"));
		emailBox.sendKeys(newEmail);
	}

	public void clickSubmit() {
		driver.findElement(By.className("t-submit-btn")).click();
	}

	public boolean updateSuccess() {
		try {
			waitForElementVisible(By.className("t-user-page-notification-success"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean hasErrors() {
		return !driver.findElements(By.className("ant-form-item-has-error")).isEmpty();
	}

}
