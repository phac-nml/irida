package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SpaBase {
	protected WebDriver driver;
	protected WebDriverWait wait;
	protected static String BASE_URL;

	public SpaBase(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
	}

	public static void setBaseUrl(String baseUrl) {
		BASE_URL = baseUrl;
	}

	protected static void get(WebDriver driver, String relativeUrl) {
		String url = BASE_URL + relativeUrl;
		driver.get(url);
	}

	public int getCartCount() {
		return Integer.parseInt(driver.findElement(By.className("ant-badge-count")).getAttribute("title"));
	}
}
