package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page object to represent the Announcements Detail page
 */
public class AnnouncementDetailPage extends AbstractPage {

    public AnnouncementDetailPage(WebDriver driver) {
        super(driver);
    }

    public String getInputText() {
        return driver.findElement(By.cssSelector("textarea.announcement-input")).getText();
    }

    public void enterMessage(String message) {
        WebElement messageElement = driver.findElement(By.id("message"));
        WebElement submitButton = driver.findElement(By.id("submitBtn"));

        messageElement.clear();
        messageElement.sendKeys(message);
        submitAndWait(submitButton);
    }

    public int getTableDataSize() {
        WebElement table = driver.findElement(By.id("announcementUsersTable"));
        return table.findElements(By.cssSelector("tbody>tr")).size();
    }

    public void clickCancelButton() {
        WebElement cancelButton = driver.findElement(By.id("cancelBtn"));
        cancelButton.click();
        waitForTime(DEFAULT_WAIT);
    }

    public void clickDeleteButton() {
        WebElement cancelButton = driver.findElement(By.id("deleteBtn"));
        cancelButton.click();
        waitForTime(DEFAULT_WAIT);
    }
}
