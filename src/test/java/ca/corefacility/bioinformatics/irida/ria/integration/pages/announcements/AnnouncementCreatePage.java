package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page object to represent the Announcements Creation page
 */
public class AnnouncementCreatePage extends AbstractPage {
    public AnnouncementCreatePage(WebDriver driver) {
        super(driver);
    }

    public void goTo() {
        get(driver, "announcements/create");
    }

    public void enterMessage(String message) {
        WebElement messageElement = driver.findElement(By.id("message"));
        WebElement submitButton = driver.findElement(By.id("submitBtn"));

        messageElement.sendKeys(message);
        submitAndWait(submitButton);
    }
}
