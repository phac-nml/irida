package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateRemoteAPIPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(CreateRemoteAPIPage.class);

	private final String CREATE_PAGE = "remote_api/create";
	public static String SUCCESS_PAGE = "remote_api/\\d+";

	public CreateRemoteAPIPage(WebDriver driver) {
		super(driver);
		get(driver, CREATE_PAGE);
	}

	public void goTo() {
		get(driver, CREATE_PAGE);
	}

	public void createRemoteAPIWithDetails(String name, String serviceURI, String clientID, String clientSecret) {
		logger.trace("Creating client with name " + name);

		driver.findElement(By.id("name")).sendKeys(name);
		driver.findElement(By.id("clientSecret")).sendKeys(clientSecret);
		driver.findElement(By.id("clientId")).sendKeys(clientID);
		driver.findElement(By.id("serviceURI")).sendKeys(serviceURI);

		WebElement submit = driver.findElement(By.id("create-remoteapi-submit"));
		submit.click();
	}

	public boolean checkSuccess() {
		try {
			// if there's a remove button, we succeeded!
			driver.findElement(By.id("remove-btn")); 
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
}
