package ca.corefacility.bioinformatics.irida.ria.webdriver.pages;

import org.openqa.selenium.WebDriver;

/**
 * Created by josh on 2014-06-05.
 */
public class AbstractPage {
	protected WebDriver driver;

	public AbstractPage(WebDriver driver) {
		setDriver(driver);
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
}
