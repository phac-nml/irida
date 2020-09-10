package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

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
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(deleteButton));
		deleteButton.click();
		wait.until(ExpectedConditions.visibilityOf(deleteConfirmButton));
		deleteConfirmButton.click();
		String foobar;
	}

	public void confirmDelete() {
		logger.debug("clicking confirm-delete button");
		WebElement confirmButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.className("t-confirm-delete")));
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
		WebElement connectButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.className("oauth-connect-link")));
		connectButton.click();

		waitForAjax();

	}

	public void clickAuthorize() {
		driver.switchTo().frame("oauth-connect-frame");
		WebElement authorizeButton = driver.findElement(By.id("authorize-btn"));
		authorizeButton.click();

		driver.switchTo().defaultContent();

		waitForTime(8000);
	}

//	public boolean checkDeleteSuccess() {
//		boolean deleted = false;
//
//		logger.debug("Checking for client existence");
//		do {
//			try {
//				WebElement el = driver.findElement(By.className("ant-page-header-heading-title"));
//				if (el.getText().equals("Remote IRIDA Connections")) {
//					logger.debug("Successfully loaded client list page");
//					waitForAjax();
//					logger.debug("Table loaded");
//					List<WebElement> findElements = driver.findElements(By.className("remoteApiCol"));
//					deleted = true;
//					// check if the element is in the table
//					for (WebElement ele : findElements) {
//						if (ele.getText().equals(clientId)) {
//							deleted = false;
//						}
//					}
//				}
//			} catch (StaleElementReferenceException e) {
//				logger.debug("Got stale element reference exception when trying to get text on h1, trying again.");
//			}
//		} while (!deleted);
//
//
//		return deleted;
//	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}

	public enum ApiStatus {
		CONNECTED, INVALID, ERROR
	}
}
