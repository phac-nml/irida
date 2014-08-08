package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class ClientsPage {
	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(ClientsPage.class);

	public ClientsPage(WebDriver driver) {
		this.driver = driver;
		driver.get("http://localhost:8080/clients");
		waitForAjax();
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}

	public int clientsTableSize() {
		logger.trace("Getting table size");
		WebElement element = driver.findElement(By.xpath("//table[@id='clientsTable']/tbody"));
		return element.findElements(By.tagName("tr")).size();
	}
}
