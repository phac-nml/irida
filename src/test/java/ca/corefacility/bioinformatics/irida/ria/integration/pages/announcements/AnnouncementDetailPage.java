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

    public String getTitle() {
        return driver.findElement(By.cssSelector("#title")).getAttribute("value");
    }

    public String getMessage() {
        return driver.findElement(By.cssSelector("#message")).getText();
    }

    public boolean getPriority() {
        return driver.findElement(By.cssSelector("#priority")).isSelected();
    }

    public void enterMessage(String title, String message, boolean priority) {
        WebElement titleElement = driver.findElement(By.id("title"));
        WebElement messageElement = driver.findElement(By.id("message"));
        WebElement priorityElement = driver.findElement(By.id("priority"));
        WebElement submitButton = driver.findElement(By.id("submitBtn"));

        titleElement.clear();
        titleElement.sendKeys(title);

        messageElement.clear();
        messageElement.sendKeys(message);

        if(priority && !priorityElement.isSelected()) {
            priorityElement.click();
        } else if (!priority && priorityElement.isSelected()) {
            priorityElement.click();
        }

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
