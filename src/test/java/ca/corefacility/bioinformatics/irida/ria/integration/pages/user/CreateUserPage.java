package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Edit user page for selenium testing
 */
public class CreateUserPage extends AbstractPage {

	public static String CREATE_PAGE = "users/create";

	public CreateUserPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, CREATE_PAGE);
	}

	public void enterUserCredsWithPassword(String username, String email, String password, String confirmPassword) {
		// unselect the set password checkbox
		WebElement setPasswordCheckbox = driver.findElement(By.className("t-set-password-cb"));
		if (setPasswordCheckbox.isSelected()) {
			setPasswordCheckbox.click();
		}
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("firstName")).sendKeys("test");
		driver.findElement(By.id("lastName")).sendKeys("user");
		driver.findElement(By.id("phoneNumber")).sendKeys("8675309");
		driver.findElement(By.id("email")).sendKeys(email);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.id("confirmPassword")).sendKeys(confirmPassword);
		Select select = new Select(driver.findElement(By.id("systemRole")));
		select.selectByIndex(1);
	}

	public void enterUserCredsWithoutPassword(String username, String email) {
		// ensure the password checkbox is checked
		WebElement setPasswordCheckbox = driver.findElement(By.className("t-set-password-cb"));
		if (!setPasswordCheckbox.isSelected()) {
			setPasswordCheckbox.click();
		}

		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("firstName")).sendKeys("test");
		driver.findElement(By.id("lastName")).sendKeys("user");
		driver.findElement(By.id("phoneNumber")).sendKeys("8675309");
		driver.findElement(By.id("email")).sendKeys(email);
		Select select = new Select(driver.findElement(By.id("systemRole")));
		select.selectByIndex(1);
	}

	public void clickSubmit() {
		driver.findElement(By.className("t-submit-btn")).click();
	}

	public boolean createSuccess(String headerName) {
		try {
			waitForElementInVisible(By.className("ant-spin"));
			return ensurePageHeadingIsTranslated(headerName);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isSubmitEnabled() {
		return driver.findElement(By.className("t-submit-btn")).isEnabled();
	}

	public boolean hasErrors() {
		return !driver.findElements(By.className("t-form-error")).isEmpty();
	}

}
