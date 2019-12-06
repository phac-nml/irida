package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class ClientDetailsPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(ClientsPage.class);
	public static String RELATIVE_URL = "clients";

	private Long clientId;

	public ClientDetailsPage(WebDriver driver) {
		super(driver);
	}

	public void goToPage(Long id){
		this.clientId = id;
		get(driver, RELATIVE_URL + "/" + clientId);
	}

	public boolean verifyClient(Long id, String clientId) {
		logger.trace("Getting table size");
		WebElement idSpan = driver.findElement(By.id("client-id"));
		WebElement clientIdSpan = driver.findElement(By.id("client-clientid"));

		String idtext = idSpan.getText();
		if (!idtext.equals(id.toString())) {
			logger.error("id not equal.  Found: " + idtext);
			return false;
		}

		String clientIdText = clientIdSpan.getText();
		if (!clientIdText.equals(clientId)) {
			logger.error("clientId not equal.  Found: " + clientIdText);
			return false;
		}

		return true;
	}
	
	public String getClientSecret(){
		return driver.findElement(By.id("client-secret")).getText();
	}

	public void clickDeleteButton() {
		logger.debug("clicking remove button");
		WebElement findElement = driver.findElement(By.className("t-client-remove-btn"));
		findElement.click();
	}

	public void confirmDelete() {
		logger.debug("clicking confirm-delete button");
		WebElement confirmButton = waitForElementToBeClickable(driver.findElement(By.className("t-confirm-delete")));
		confirmButton.click();
	}

	public boolean checkDeleteSuccess() {
		boolean deleted = false;

		logger.debug("Checking for client existence");
		if (driver.getCurrentUrl().contains(RELATIVE_URL)) {
			logger.debug("Succesfully loaded client list page");
			waitForAjax();
			logger.debug("Table loaded");
			List<WebElement> findElements = driver.findElements(By.className("clientIdCol"));
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
