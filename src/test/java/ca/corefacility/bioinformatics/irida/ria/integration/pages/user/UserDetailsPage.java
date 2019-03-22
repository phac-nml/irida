package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

/**
 * User details page for selenium testing
 *
 */
public class UserDetailsPage extends AbstractPage {
	public static String EDIT_USER_LINK = "editUser";
	public static String USER_ID = "user-id";
	private PageUtilities pageUtilities;

	public UserDetailsPage(WebDriver driver) {
		super(driver);
		this.pageUtilities = new PageUtilities(driver);
	}

	public void getCurrentUser() {
		get(driver, "users/current");
	}

	public void getOtherUser(Long id) {
		get(driver, "users/" + id);
	}

	public String getUserId() {
		WebElement findElement = driver.findElement(By.id(USER_ID));
		return findElement.getText();
	}

	public boolean canGetEditLink(Long id) {
		get(driver, "users/" + id);
		try {
			driver.findElement(By.id(EDIT_USER_LINK));
			return true;
		} catch (NoSuchElementException ex) {
			return false;
		}
	}

	public List<String> getUserProjectIds() {
		List<WebElement> findElements = driver.findElements(By.className("user-project-id"));
		List<String> ids = new ArrayList<>();
		findElements.forEach(ele -> {
			ids.add(ele.getText());
		});
		return ids;
	}

	public void sendPasswordReset() {
		WebElement passwordResetLink = driver.findElement(By.className("password-reset-link"));
		passwordResetLink.click();
		WebElement confirmButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.id("resetPasswordButton")));
		confirmButton.click();
	}

	public void subscribeToFirstProject() {
		WebElement firstCheckbox = driver.findElements(By.className("subcription-checkbox"))
				.iterator()
				.next();

		firstCheckbox.click();
	}

	public boolean checkSuccessNotification() {
		return pageUtilities.checkSuccessNotification();
	}
}
