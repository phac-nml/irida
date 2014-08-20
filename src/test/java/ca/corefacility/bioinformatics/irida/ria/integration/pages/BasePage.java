package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by josh on 14-08-06.
 */
public class BasePage {
	public static final String URL = "http://localhost:8080/";

	public static WebDriver initializeDriver() {
		WebDriver driver = new ChromeDriver();
		driver.manage().window().setSize(new Dimension(1024, 900));
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();
		return driver;
	}

	public static void destroyDriver(WebDriver driver) {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}
}
