package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class PasswordResetPage extends AbstractPage {
	private static final String RELATIVE_URL = "password_reset/";

	@FindBy(id = "password")
	private WebElement firstPassword;

	@FindBy(id = "confirmPassword")
	private WebElement secondPassword;

	@FindBy(className = "t-submit-btn")
	private WebElement submitBtn;

	@FindBy(className = "t-reset-success")
	private WebElement resetSuccess;

	@FindBy(css = ".error.help-block")
	private List<WebElement> errors;

	public PasswordResetPage(WebDriver driver) {
		super(driver);
	}

	public static PasswordResetPage initializePage(WebDriver driver) {
		return PageFactory.initElements(driver, PasswordResetPage.class);
	}

	public static PasswordResetPage getPasswordResetPageByKey(WebDriver driver, String key) {
		get(driver, RELATIVE_URL + key);
		return PageFactory.initElements(driver, PasswordResetPage.class);
	}

	public void updatePassword(WebDriver driver, String password, String confirmPassword) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(firstPassword));
		firstPassword.sendKeys(password);
		secondPassword.sendKeys(confirmPassword);
	}

	public void submitReset(){
		submitBtn.click();
	}

	public boolean isSubmitButtonEnabled() {
		return submitBtn.isEnabled();
	}

	public boolean checkSuccess() {
		return resetSuccess.isDisplayed();
	}
}
