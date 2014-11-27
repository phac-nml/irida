package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Class to get project event elements from a supported page
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class ProjectEventsUtilities {
	private WebDriver driver;

	public ProjectEventsUtilities(WebDriver driver) {
		this.driver = driver;
	}

	public List<WebElement> getEvents() {
		WebElement eventContainer = driver.findElement(By.id("events"));
		return eventContainer.findElements(By.className("event-content"));
	}
}
