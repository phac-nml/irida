package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page object to represent the Announcements Dashboard page
 */
public class AnnouncementDashboardPage extends AbstractPage {

	public AnnouncementDashboardPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		driver.get("dashboard");
	}

	public List<WebElement> getCurrentUnreadAnnouncements() {
		return driver.findElements(By.cssSelector(".t-announcement-item"));
	}

}
