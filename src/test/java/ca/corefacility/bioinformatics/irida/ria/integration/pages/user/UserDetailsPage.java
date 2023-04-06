package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * User details page for selenium testing
 */
public class UserDetailsPage extends AbstractPage {

	public static String DETAILS_PAGE = "users/1/details";

	public static String DETAILS_PAGE_LDAP = "users/4/details";

	public UserDetailsPage(WebDriver driver) {
		super(driver);
	}

	public void goTo(String page) {
		get(driver, page);
	}

	public void enterFirstName(String newName) {
		WebElement firstNameBox = driver.findElement(By.id("firstName"));
		firstNameBox.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
		firstNameBox.sendKeys(newName);
	}

	public void enterLastName(String newName) {
		WebElement lastNameBox = driver.findElement(By.id("lastName"));
		lastNameBox.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
		lastNameBox.sendKeys(newName);
	}

	public void enterEmail(String newEmail) {
		WebElement emailBox = driver.findElement(By.id("email"));
		emailBox.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
		emailBox.sendKeys(newEmail);
	}

	public void enterPhone(String newPhone) {
		WebElement phoneBox = driver.findElement(By.id("phoneNumber"));
		phoneBox.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
		phoneBox.sendKeys(newPhone);
	}

	public void clickEnabledCheckbox() {
		WebElement checkbox = driver.findElement(By.id("enabled"));
		checkbox.click();
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
