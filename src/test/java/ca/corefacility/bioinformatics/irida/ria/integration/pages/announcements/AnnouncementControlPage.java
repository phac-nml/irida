package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

/**
 * Page object to represent the Announcements Control Admin page
 */
public class AnnouncementControlPage extends AbstractPage {

    public AnnouncementControlPage(WebDriver driver) {
        super(driver);
    }

    public void goTo() {
        get(driver, "announcements/admin");
        waitForAjax();
    }

    /**
     * Get the size of the announcement table currently visible on the control page
     * @return size of the table
     */
    public int announcementTableSize() {
        WebElement element = driver.findElement(By.id("announcementTable"));
        return element.findElements(By.tagName("tr")).size();
    }

    /**
     * Get a list of {@link WebElement}s describing currently visible announcements on the page
     * @return list of web elements
     */
    public List<Date> getCreatedDates() {
        WebElement table = driver.findElement(By.id("announcementTable"));
        List<WebElement> datesText = table.findElements(By.cssSelector("tbody>tr>td:last-of-type"));
        return datesText.stream().map(td -> new Date(td.getText())).collect(Collectors.toList());
    }

    public String getAnnouncement(int position) {
        WebElement table = driver.findElement(By.id("announcementTable"));
        List<WebElement> messages = table.findElements(By.cssSelector("tbody>tr>td:fist-of-type"));
        return messages.get(position).getText();
    }

    public void clickDateCreatedHeader() {
        WebElement header = driver.findElement(By.cssSelector("[data-data='createdDate']"));
        header.click();
        waitForAjax();
    }

    public void clickCreateNewAnnouncementButton() {
        WebElement createButton = driver.findElement(By.id("create-new-button"));
        createButton.click();
    }

    public void gotoMessageDetails(int index) {
        WebElement table = driver.findElement(By.id("announcementTable"));
        List<WebElement> messages = table.findElements(By.cssSelector("tbody a"));
        if (index < messages.size()) {
            messages.get(index).click();
            waitForTime(DEFAULT_WAIT);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private void waitForAjax() {
        Wait<WebDriver> wait = new WebDriverWait(driver, 60);
        wait.until(Ajax.waitForAjax(60000));
    }
}
