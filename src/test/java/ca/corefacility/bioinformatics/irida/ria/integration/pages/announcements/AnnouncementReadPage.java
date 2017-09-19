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

    public List<WebElement> getAllReadAnnouncements() {
        return driver.findElements(By.cssSelector(".announcement-item"));
    }
}
