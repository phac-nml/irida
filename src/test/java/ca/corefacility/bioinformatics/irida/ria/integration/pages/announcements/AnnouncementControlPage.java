package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

/**
 * Page object to represent the Announcements Control Admin page
 */
public class AnnouncementControlPage extends AbstractPage {
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementControlPage.class);

    public AnnouncementControlPage(WebDriver driver) {
        super(driver);
    }

    public void goTo() {
        get(driver, "announcements/admin");
		waitForTime(1000);
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
        //May 20, 2016 11:42 AM
        DateFormat df = new SimpleDateFormat("MMM d, yyyy HH:mm aaa");
        WebElement table = driver.findElement(By.id("announcementTable"));
        List<WebElement> dateElements = table.findElements(By.cssSelector("tbody td:last-of-type"));
        List<Date> dates = new ArrayList<>();
        for (WebElement element : dateElements) {
            String dateText = element.getText();
            try {
                dates.add(df.parse(dateText));
            } catch (ParseException e) {
                logger.debug("Cannot convert value to date: ", dateText);
            }
        }
        return dates;
    }

    public String getAnnouncement(int position) {
        List<WebElement> messages = driver.findElements(By.cssSelector("tbody .preview-column a"));
        return messages.get(position).getText();
    }

    public void clickDateCreatedHeader() {
        WebElement header = driver.findElement(By.cssSelector("[data-data='createdDate']"));
        header.click();
        waitForAjax();
    }

    public void clickCreateNewAnnouncementButton() {
        waitForTime(2000);
        WebElement createButton = driver.findElement(By.cssSelector("a#create-announcement"));
        createButton.click();
    }

    public void gotoMessageDetails(int index) {
        List<WebElement> messages = driver.findElements(By.cssSelector("tbody .preview-column a"));
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
