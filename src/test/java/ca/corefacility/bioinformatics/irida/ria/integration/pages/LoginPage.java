package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * <p> Page Object to represent the login page. </p>
 */
public class LoginPage extends AbstractPage {
	public static final String MANAGER_USERNAME = "mrtest";
	public static final String ADMIN_USERNAME = "admin";
	public static final String USER_USERNAME = "testUser";
	public static final String ANOTHER_USER_USERNAME = "thethird";
	public static final String SEQUENCER_USERNAME = "sequencer";
	public static final String GOOD_PASSWORD = "Password1!";
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

	/**
	 * Login with custom credentials
	 *
	 * @param driver   {@link WebDriver}
	 * @param username Name to login with
	 * @param password Password to login with
	 */
	public static void login(WebDriver driver, String username, String password) {
		logout(driver);
		get(driver, "login");
		LoginPage loginPage = PageFactory.initElements(driver, LoginPage.class);
		loginPage.login(username, password);
	}

	/**
	 * Login as a user.
	 *
	 * @param driver {@link WebDriver}
	 */
	public static void loginAsUser(WebDriver driver) {
		login(driver, USER_USERNAME, GOOD_PASSWORD);
	}

	/**
	 * Login as another user.
	 *
	 * @param driver {@link WebDriver}
	 */
	public static void loginAsAnotherUser(WebDriver driver) {
		login(driver, ANOTHER_USER_USERNAME, GOOD_PASSWORD);
	}

	/**
	 * Login as an manager
	 *
	 * @param driver {@link WebDriver}
	 */
	public static void loginAsManager(WebDriver driver) {
		login(driver, MANAGER_USERNAME, GOOD_PASSWORD);
	}

	/**
	 * Login as an admin
	 *
	 * @param driver {@link WebDriver}
	 */
	public static void loginAsAdmin(WebDriver driver) {
		login(driver, ADMIN_USERNAME, GOOD_PASSWORD);
	}

	/**
	 * To to the login page in and initialize the page
	 *
	 * @param driver {@link WebDriver}
	 * @return An initialized {@link LoginPage}
	 */
	public static LoginPage to(WebDriver driver) {
		get(driver, "login");
		return PageFactory.initElements(driver, LoginPage.class);
	}

	/**
	 * Only do a login on an initialized {@link LoginPage}
	 *
	 * @param username Name to login with
	 * @param password Password to login with
	 */
	public void login(String username, String password) {
		this.username.sendKeys(username);
		this.password.sendKeys(password);
		submitAndWait(this.submitBtn);
	}
}
