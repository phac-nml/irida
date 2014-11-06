package ca.corefacility.bioinformatics.irida.ria.integration.pages;

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
public class LoginPage extends AbstractPage {
	public static final String ADMIN_USERNAME = "mrtest";
	public static final String USER_USERNAME = "testUser";
	public static final String GOOD_PASSWORD = "Password1";
	public static final String BAD_USERNAME = "badman";
	public static final String BAD_PASSWORD = "notapassword";

	@FindBy(name = "username")
	private WebElement username;

	@FindBy(name = "password")
	private WebElement password;

	@FindBy(id = "submitBtn")
	private WebElement submitBtn;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public static void login(WebDriver driver, String username, String password) {
		get(driver, "login");
		LoginPage loginPage = PageFactory.initElements(driver, LoginPage.class);
		loginPage.login(username, password);
	}

	public static LoginPage to(WebDriver driver) {
		get(driver, "login");
		return PageFactory.initElements(driver, LoginPage.class);
	}

	public void login(String username, String password) {
		this.username.sendKeys(username);
		this.password.sendKeys(password);
		this.submitBtn.click();
	}
}
