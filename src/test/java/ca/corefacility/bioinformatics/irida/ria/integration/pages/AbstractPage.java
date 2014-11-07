package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the common elements in a page within the application.
 *
 * @author Josh Adam
 */
public class AbstractPage {
	private static final String BASE_URL = "http://localhost:8080/";

	@FindBy(className = "error")
	private WebElement errors;

	protected WebDriver driver;

	public AbstractPage(WebDriver driver) {
		setDriver(driver);
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public String getErrors() {
		return errors.getText();
	}

	protected static void get(WebDriver driver, String relativeUrl) {
		String url = BASE_URL + relativeUrl;
		driver.get(url);
	}
}
