package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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

    public void viewReadAnnouncements() {
        WebElement viewReadButton = driver.findElement(By.id("view-read-announcements"));
        viewReadButton.click();
    }

    public List<WebElement> getCurrentUnreadAnnouncements() {
        return driver.findElements(By.cssSelector(".announcement-item"));
    }

    public void markTopAnnouncementAsRead() {
        WebElement markReadButton = driver.findElement(By.cssSelector("div.announcement-markread>span"));
        markReadButton.click();
        waitForTime(DEFAULT_WAIT);
    }
}
