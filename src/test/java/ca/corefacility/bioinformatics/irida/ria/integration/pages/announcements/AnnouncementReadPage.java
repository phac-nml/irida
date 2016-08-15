package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page object to represent the Read Announcements page
 */
public class AnnouncementReadPage extends AbstractPage {
    public AnnouncementReadPage(WebDriver driver) {
        super(driver);
    }

    public List<WebElement> getAllReadAnnouncements() {
        return driver.findElements(By.cssSelector("li.announcement-item"));
    }
}
