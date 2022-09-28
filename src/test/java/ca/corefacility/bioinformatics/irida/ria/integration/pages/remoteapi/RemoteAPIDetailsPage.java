package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class RemoteAPIDetailsPage extends AbstractPage {
	public static String REMOTEAPI_LIST = "remote_api";

	public static String RELATIVE_URL = "admin/remote_api/";

	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIDetailsPage.class);

	@FindBy(className = "t-remote-name")
	private WebElement remoteName;

	@FindBy(className = "t-remote-status-connect")
	private WebElement remoteStatusConnect;

	@FindBy(className = "t-remote-status-connected")
	private WebElement remoteStatusConnected;

	@FindBy(className = "t-remote-clientId")
	private WebElement remoteClientId;

	@FindBy(className = "t-delete-tab")
	private WebElement deleteTab;

	@FindBy(className = "t-delete-btn")
	private WebElement deleteButton;

	@FindBy(className = "t-delete-confirm")
	private WebElement deleteConfirmButton;

	public static RemoteAPIDetailsPage gotoDetailsPage(WebDriver driver, Long remoteId) {
		get(driver, RELATIVE_URL + remoteId);
		return PageFactory.initElements(driver, RemoteAPIDetailsPage.class);
	}

	public static RemoteAPIDetailsPage gotoDetailsPage(WebDriver driver) {
		return PageFactory.initElements(driver, RemoteAPIDetailsPage.class);
	}

	public RemoteAPIDetailsPage(WebDriver driver) {
		super(driver);
	}

	public String getClientName() {
		return remoteName.getText();
	}

	public String getClientId() {
		return remoteClientId.getText();
	}

	public void clickDeleteButton() {
		deleteTab.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.visibilityOf(deleteButton));
		deleteButton.click();
		wait.until(ExpectedConditions.visibilityOf(deleteConfirmButton));
		deleteConfirmButton.click();
	}

	public void confirmDelete() {
		logger.debug("clicking confirm-delete button");
		WebElement confirmButton = (new WebDriverWait(driver, Duration.ofSeconds(10)))
				.until(ExpectedConditions.elementToBeClickable(By.className("t-confirm-delete")));
		confirmButton.click();
	}

	public boolean isRemoteAPIConnected() {
		try {
			return remoteStatusConnected.isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public void clickConnect() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.elementToBeClickable(remoteStatusConnect));
		remoteStatusConnect.click();

	}

	public void clickAuthorize() {
		String parentWindowHandler = driver.getWindowHandle(); // Store your parent window
		String subWindowHandler = null;

		Set<String> handles = driver.getWindowHandles(); // get all window handles
		for (String handle : handles) {
			subWindowHandler = handle;
		}
		driver.switchTo().window(subWindowHandler); // switch to popup window

		// Now you are in the popup window, perform necessary actions here
		WebElement authorizeButton = driver.findElement(By.id("authorize-btn"));
		authorizeButton.click();

		driver.switchTo().window(parentWindowHandler); // switch back to parent window

		waitForTime(8000);
	}

	public enum ApiStatus {
		CONNECTED,
		INVALID,
		ERROR
	}
}
