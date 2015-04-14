package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class CreateClientPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(CreateClientPage.class);

	private final String CREATE_PAGE = "clients/create";
	public static String SUCCESS_PAGE = "clients/\\d+";

	public CreateClientPage(WebDriver driver) {
		super(driver);
		get(driver, CREATE_PAGE);
	}

	public void createClientWithDetails(String id, String grant, boolean scope_read, boolean scope_write) {
		logger.trace("Creating client with id: " + id + " and grant: " + grant);

		WebElement idField = driver.findElement(By.id("clientId"));
		idField.sendKeys(id);

		WebElement grantField = driver.findElement(By.id("authorizedGrantTypes"));
		grantField.sendKeys(grant);

		WebElement submit = driver.findElement(By.id("create-client-submit"));

		//The read scope has been selected by default:
		if (scope_write) {
			driver.findElement(By.id("scope_read")).click();
			driver.findElement(By.id("scope_write")).click();
		}

		submit.click();
	}

	public boolean checkSuccess() {
		if (driver.getCurrentUrl().matches(BASE_URL + SUCCESS_PAGE)) {
			return true;
		} else {
			return false;
		}
	}
}
