package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Edit user page for selenium testing
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class CreateUserPage extends AbstractPage {

	public static String CREATE_PAGE = "users/create";
	public static String SUCCESS_PAGE = BASE_URL + "users/\\d+";

	public CreateUserPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, CREATE_PAGE);
	}

	public void createUserWithPassword(String username, String email, String password, String confirmPassword) {
		// unselect the set password checkbox
		WebElement setPasswordCheckbox = driver.findElement(By.id("setpassword"));
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

		driver.findElement(By.className("submit")).click();
	}

	public void createUserWithoutPassword(String username, String email) {
		// ensure the password checkbox is checked
		WebElement setPasswordCheckbox = driver.findElement(By.id("setpassword"));
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

		driver.findElement(By.className("submit")).click();
	}

	public boolean createSuccess() {
		return driver.getCurrentUrl().matches(SUCCESS_PAGE);
	}

}
