package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page object to represent the Read Announcements page
 */
public class AnnouncementReadPage extends AbstractPage {
	public AnnouncementReadPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, "announcements/user/list");
	}

	public List<WebElement> getAllReadAnnouncements() {
		WebElement readButton = driver.findElement(By.cssSelector(".t-read-announcements"));
		readButton.click();
		return driver.findElements(By.cssSelector(".t-announcement-item"));
	}
}
