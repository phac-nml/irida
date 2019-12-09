package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateClientPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(CreateClientPage.class);

	private final String CREATE_PAGE = "clients/create";

	public CreateClientPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, CREATE_PAGE);
	}

	public void createClientWithDetails(String id, String grant, String redirectUri, boolean scope_read,
			boolean scope_write) {
		logger.trace("Creating client with id: " + id + " and grant: " + grant);

		WebElement idField = driver.findElement(By.id("clientId"));
		idField.sendKeys(id);

		WebElement grantField = driver.findElement(By.id("authorizedGrantTypes"));
		grantField.sendKeys(grant);

		//if we have a redirect uri, enter it
		if (redirectUri != null && !redirectUri.isEmpty()) {
			WebElement redirectField = driver.findElement(By.id("registeredRedirectUri"));
			redirectField.sendKeys(redirectUri);
		}

		WebElement submit = driver.findElement(By.id("create-client-submit"));

		//The read scope has been selected by default:
		if (scope_write) {
			driver.findElement(By.id("scope_read"))
					.click();
			driver.findElement(By.id("scope_write"))
					.click();
		}

		submit.click();
	}

	public boolean checkSuccess() {
		try {
			WebElement el = waitForElementVisible(By.className("client-details-heading"));
			return el.getText()
					.equals("Client Details");
		} catch (Exception e) {
			return false;
		}
	}
}
