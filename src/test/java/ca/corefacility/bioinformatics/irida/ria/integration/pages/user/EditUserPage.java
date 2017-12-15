package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Edit user page for selenium testing
 * 
 *
 */
public class EditUserPage extends AbstractPage {

	public static String EDIT_PAGE = "users/1/edit";

	public EditUserPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, EDIT_PAGE);
	}

	public void enterFirstName(String newName) {
		WebElement firstNameBox = driver.findElement(By.id("firstName"));
		firstNameBox.sendKeys(newName);
	}

	public String getUpdatedUserFirstLastName() {
		return driver.findElement(By.id("user-name")).getText();
	}
	
	public void enterPassword(String password, String confirm){
		WebElement passwordBox = driver.findElement(By.id("password"));
		WebElement confirmPasswordBox = driver.findElement(By.id("confirmPassword"));
		passwordBox.sendKeys(password);
		confirmPasswordBox.sendKeys(confirm);
	}

	public void clickSubmit() {
		driver.findElement(By.className("t-submit-btn")).click();
	}

	public boolean updateSuccess(){
		try {
			waitForElementVisible(By.className("t-user-page-success"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isSubmitEnabled(){
		return driver.findElement(By.className("t-submit-btn")).isEnabled();
	}

	public boolean hasErrors() {
		return !driver.findElements(By.className("t-form-error")).isEmpty();
	}

}
