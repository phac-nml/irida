package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class RemoteAPIDetailsPage extends AbstractPage {
	public static String REMOTEAPI_LIST = "remote_api";

	public static String RELATIVE_URL = "remote_api/";

	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIDetailsPage.class);

	private Long clientId;

	public RemoteAPIDetailsPage(WebDriver driver, Long clientId) {
		super(driver);
		this.clientId = clientId;
		get(driver, RELATIVE_URL + clientId);
	}

	public RemoteAPIDetailsPage(WebDriver driver) {
		super(driver);
	}

	public String getClientName() {
		WebElement clientIdSpan = driver.findElement(By.id("remoteapi-name"));
		return clientIdSpan.getText();
	}

	public String getClientId() {
		WebElement clientIdSpan = driver.findElement(By.id("remoteapi-clientid"));
		return clientIdSpan.getText();
	}

	public void clickDeleteButton() {
		logger.debug("clicking remove button");

		WebElement findElement = new WebDriverWait(driver, TIME_OUT_IN_SECONDS)
				.until(ExpectedConditions.presenceOfElementLocated(By.className("t-remove-btn")));
		findElement.click();
	}

	public void confirmDelete() {
		logger.debug("clicking confirm-delete button");
		WebElement confirmButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.className("t-confirm-delete")));
		confirmButton.click();
	}

	public ApiStatus getRemoteApiStatus() {
		WebElement connectionStatus = (new WebDriverWait(driver, 10)).until(ExpectedConditions
				.presenceOfElementLocated(By.className("status-label")));

		String labelClass = connectionStatus.getAttribute("class");
		if (labelClass.contains("api-connected")) {
			return ApiStatus.CONNECTED;
		} else if (labelClass.contains("api-invalid")) {
			return ApiStatus.INVALID;
		} else if (labelClass.contains("api-error")) {
			return ApiStatus.ERROR;
		}

		throw new ElementNotVisibleException("Coudldn't get api status");
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

	public boolean checkDeleteSuccess() {
		boolean deleted = false;

		logger.debug("Checking for client existence");
		do {
			try {
				WebElement el = driver.findElement(By.tagName("h1"));
				if (el.getText().equals("Remote IRIDA Connections")) {
					logger.debug("Successfully loaded client list page");
					waitForAjax();
					logger.debug("Table loaded");
					List<WebElement> findElements = driver.findElements(By.className("remoteApiCol"));
					deleted = true;
					// check if the element is in the table
					for (WebElement ele : findElements) {
						if (ele.getText().equals(clientId)) {
							deleted = false;
						}
					}
				}
			} catch (StaleElementReferenceException e) {
				logger.debug("Got stale element reference exception when trying to get text on h1, trying again.");
			}
		} while (!deleted);


		return deleted;
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}

	public enum ApiStatus {
		CONNECTED, INVALID, ERROR;
	}
}
