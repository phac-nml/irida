package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDetailsPage {
	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(ClientsPage.class);

	public ClientDetailsPage(WebDriver driver, Long clientId) {
		this.driver = driver;
		driver.get("http://localhost:8080/clients/" + clientId);
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
}
