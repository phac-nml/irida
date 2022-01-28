package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.WebDriver;

/**
 * Page object to represent the Dashboard page
 */
public class DashboardPage extends AbstractPage {

	public DashboardPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, "/");
	}

}
