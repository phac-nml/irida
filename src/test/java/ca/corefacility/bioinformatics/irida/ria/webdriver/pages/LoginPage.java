package ca.corefacility.bioinformatics.irida.ria.webdriver.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Created by josh on 2014-06-05.
 */
public class LoginPage extends AbstractPage {
	@FindBy(name = "username")
	private WebElement emailTF;

	@FindBy(name = "password")
	private WebElement passwordTF;

	@FindBy(className = "btn-primary")
	private WebElement submit;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public static void login(WebDriver driver) {
		driver.get("http://localhost:8080/login");
		LoginPage loginPage = PageFactory.initElements(driver, LoginPage.class);
		loginPage.login("tester", "password1");
	}

	public void login(String email, String password) {
		this.emailTF.sendKeys(email);
		this.passwordTF.sendKeys(password);
		this.submit.click();
	}
}
