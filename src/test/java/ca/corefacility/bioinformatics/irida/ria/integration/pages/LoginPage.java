package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * <p>
 * Page Object to represent the login page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class LoginPage {
	public static final String GOOD_USERNAME = "mrtest";
	public static final String GOOD_PASSWORD = "Password1";
	public static final String BAD_USERNAME = "badman";
	public static final String BAD_PASSWORD = "notapassword";

	@FindBy(name = "username")
	private WebElement username;

	@FindBy(name = "password")
	private WebElement password;

	@FindBy(className = "btn-primary")
	private WebElement submit;

	private WebDriver driver;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
	}

	public static LoginPage to(WebDriver driver) {
		driver.get("http://localhost:8080/login");
		return PageFactory.initElements(driver, LoginPage.class);
	}

	public void doBadUsernameLogin() {
		login(BAD_USERNAME, GOOD_PASSWORD);
	}

	public void doBadPasswordLogin() {
		login(GOOD_USERNAME, BAD_PASSWORD);
	}

	public void doLogin() {
		login(GOOD_USERNAME, GOOD_PASSWORD);
	}

	public void login(String username, String password) {
		this.username.sendKeys(username);
		this.password.sendKeys(password);
		this.submit.click();
	}

	public String getError() {
		WebElement error = driver.findElement(By.className("alert"));
		String errorText = "";
		if (error != null) {
			errorText = error.getText();
		}
		return errorText;
	}
}
