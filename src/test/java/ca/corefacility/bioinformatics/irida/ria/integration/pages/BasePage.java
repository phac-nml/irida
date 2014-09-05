package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 * Created by josh on 14-08-06.
 */
public class BasePage {
	public static final String URL = "http://localhost:8080/";

	public static WebDriver initializeDriver() {
		WebDriver driver = new PhantomJSDriver();
		driver.manage().window().setSize(new Dimension(1024, 900));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();
		return driver;
	}

	public static WebDriver initializeChromeDriver() {
		WebDriver driver = new ChromeDriver();
		driver.manage().window().setSize(new Dimension(1024, 900));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
