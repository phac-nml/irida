package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.util.List;

import org.openqa.selenium.By;
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

	public boolean verifyRemoteAPI(Long id, String apiName) {
		logger.trace("Getting table size");
		WebElement idSpan = driver.findElement(By.id("remoteapi-id"));
		WebElement clientIdSpan = driver.findElement(By.id("remoteapi-name"));

		String idtext = idSpan.getText();
		if (!idtext.equals(id.toString())) {
			logger.error("id not equal.  Found: " + idtext);
			return false;
		}

		String clientIdText = clientIdSpan.getText();
		if (!clientIdText.equals(apiName)) {
			logger.error("clientId not equal.  Found: " + clientIdText);
			return false;
		}

		return true;
	}

	public void clickDeleteButton() {
		logger.debug("clicking remove button");
		WebElement findElement = driver.findElement(By.id("remove-btn"));
		findElement.click();
	}

	public void confirmDelete() {
		logger.debug("clicking confirm-delete button");
		WebElement confirmButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.className("confirm-delete")));
		confirmButton.click();
	}

	public boolean checkDeleteSuccess() {
		boolean deleted = false;

		logger.debug("Checking for client existence");
		if (driver.getCurrentUrl().matches(BASE_URL + REMOTEAPI_LIST)) {
			logger.debug("Succesfully loaded client list page");
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

		return deleted;
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
