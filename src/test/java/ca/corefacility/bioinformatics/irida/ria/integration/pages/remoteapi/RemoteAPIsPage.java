package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class RemoteAPIsPage {

	private static final String BASE_URL = BasePage.URL + "/remote_api";
	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(ClientsPage.class);

	public RemoteAPIsPage(WebDriver driver) {
		this.driver = driver;
		driver.get(BASE_URL);
		waitForAjax();
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}

	public int remoteApisTableSize() {
		logger.trace("Getting table size");
		WebElement element = driver.findElement(By.xpath("//table[@id='remoteapiTable']/tbody"));
		return element.findElements(By.tagName("tr")).size();
	}

	public boolean checkRemoteApiExistsInTable(String clientName) {
		List<WebElement> findElements = driver.findElements(By.className("api-name"));
		for (WebElement ele : findElements) {
			if (ele.getText().equals(clientName)) {
				return true;
			}
		}

		return false;
	}
}
