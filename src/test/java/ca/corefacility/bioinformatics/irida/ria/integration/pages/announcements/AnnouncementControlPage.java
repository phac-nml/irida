package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

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
    public List<WebElement> getVisibleAnnouncementsContent() {
        WebElement table = driver.findElement(By.id("announcementTable"));
        return table.findElements(By.cssSelector("tbody>tr>td:first-of-type"));
    }

    public void clickMessageHeader() {
        WebElement header = driver.findElement(By.id("message-header"));
        header.click();
        waitForAjax();
    }

    public void clickCreateNewAnnouncementButton() {
        WebElement createButton = driver.findElement(By.id("create-new-button"));
        createButton.click();
    }

    public void clickDetailsButton(int index) {
        List<WebElement> buttons = driver.findElements(By.cssSelector("div>button.details-btn"));
        if (index < buttons.size()) {
            buttons.get(index).click();
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
