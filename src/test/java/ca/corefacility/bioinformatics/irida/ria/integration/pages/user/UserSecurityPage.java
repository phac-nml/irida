package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

/**
 * User security page for selenium testing
 */
public class UserSecurityPage extends AbstractPage {

	private PageUtilities pageUtilities;

	public static String SECURITY_PAGE = "users/3/security";

	public UserSecurityPage(WebDriver driver) {
		super(driver);
		this.pageUtilities = new PageUtilities(driver);
	}

	public void goTo() {
		get(driver, SECURITY_PAGE);
	}

	public void getOtherUser(Long id) {
		get(driver, "users/" + id + "/security");
	}

	public void changePassword(String oldPassword, String newPassword) {
		WebElement oldPasswordBox = driver.findElement(By.id("oldPassword"));
		oldPasswordBox.sendKeys(oldPassword);
		WebElement newPasswordBox = driver.findElement(By.id("newPassword"));
		newPasswordBox.sendKeys(newPassword);
		WebElement submitButton = driver.findElement(By.className("t-submit-btn"));
		submitButton.click();
	}

	public void sendPasswordReset() {
		WebElement passwordResetLink = driver.findElement(By.className("t-password-reset-link"));
		passwordResetLink.click();
		pageUtilities.waitForElementVisible(By.className("ant-popover-message"));
		WebElement confirmButton = driver.findElement(
				By.cssSelector("div.ant-popover-buttons > button.ant-btn-primary"));
		confirmButton.click();
	}

	public boolean checkSuccessNotification() {
		return pageUtilities.checkSuccessNotification();
	}

	public boolean hasErrors() {
		return !driver.findElements(By.className("ant-form-item-has-error")).isEmpty();
	}

}
