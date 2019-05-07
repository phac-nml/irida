package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class RemoteAPIsPage extends AbstractPage {

	private static final String RELATIVE_URL = "remote_api";
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIsPage.class);

	public RemoteAPIsPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
	}

	public int remoteApisTableSize() {
		logger.trace("Getting table size");
		WebElement element = driver.findElement(By.xpath("//table[@id='remoteapiTable']/tbody"));
		return element.findElements(By.tagName("tr")).size();
	}

	public boolean checkRemoteApiExistsInTable(String clientName) {
		List<WebElement> findElements = driver.findElements(By.className("t-api-name"));
		for (WebElement ele : findElements) {
			if (ele.getText().equals(clientName)) {
				return true;
			}
		}

		return false;
	}
}
