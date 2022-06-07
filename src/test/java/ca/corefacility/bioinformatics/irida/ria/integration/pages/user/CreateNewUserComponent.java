package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Edit user page for selenium testing
 */
public class CreateNewUserComponent extends AbstractPage {

	public CreateNewUserComponent(WebDriver driver) {
		super(driver);
	}

	public static CreateNewUserComponent goTo(WebDriver driver) {
		return PageFactory.initElements(driver, CreateNewUserComponent.class);
	}

	public void enterUserDetailsWithPassword(String username, String email, String password) {
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("firstName")).sendKeys("test");
		driver.findElement(By.id("lastName")).sendKeys("user");
		driver.findElement(By.id("email")).sendKeys(email);
		driver.findElement(By.id("phoneNumber")).sendKeys("1234");
		driver.findElement(By.id("password")).sendKeys(password);
	}

	public void enterUserDetailsWithoutPassword(String username, String email) {
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("firstName")).sendKeys("test");
		driver.findElement(By.id("lastName")).sendKeys("user");
		driver.findElement(By.id("email")).sendKeys(email);
		driver.findElement(By.id("phoneNumber")).sendKeys("1234");
		// ensure the password checkbox is checked
		WebElement setPasswordCheckbox = driver.findElement(By.id("activate"));
		if (!setPasswordCheckbox.isSelected()) {
			setPasswordCheckbox.click();
		}
	}

	public void clickSubmit() {
		driver.findElement(By.className("t-submit-btn")).click();
	}

	public boolean hasSuccessfulNotification() {
		try {
			waitForElementVisible(By.className("t-user-page-notification-success"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isSubmitEnabled() {
		return driver.findElement(By.className("t-submit-btn")).isEnabled();
	}

	public boolean hasErrors() {
		return !driver.findElements(By.className("ant-form-item-has-error")).isEmpty();
	}

}
