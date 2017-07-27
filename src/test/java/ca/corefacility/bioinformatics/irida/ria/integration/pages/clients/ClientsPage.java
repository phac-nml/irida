package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ClientsPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(ClientsPage.class);

	public ClientsPage(WebDriver driver) {
		super(driver);
	}

	public void goTo(){
		get(driver , "clients");
		waitForTime(400);
	}

	public int clientsTableSize() {
		logger.trace("Getting table size");
		WebElement element = driver.findElement(By.xpath("//table[@id='clientsTable']/tbody"));
		return element.findElements(By.tagName("tr")).size();
	}

	public boolean checkClientExistsInTable(String clientId) {
		List<WebElement> findElements = driver.findElements(By.className("btn-link"));
		for (WebElement ele : findElements) {
			if (ele.getText().equals(clientId)) {
				return true;
			}
		}

		return false;
	}
}
