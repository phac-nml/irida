package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page object to represent the Announcements Creation page
 */
public class CreateAnnouncementComponent extends AbstractPage {
    @FindBy(className = "mde-text")
    private WebElement textarea;

    @FindBy(className = "t-submit-announcement")
    private WebElement submitButton;

    public CreateAnnouncementComponent(WebDriver driver) {
        super(driver);
    }

    public static CreateAnnouncementComponent goTo(WebDriver driver) {
        return PageFactory.initElements(driver, CreateAnnouncementComponent.class);
    }

    public void enterMessage(String message) {
        textarea.sendKeys(message);
        submitButton.click();
        waitForTime(400);
    }
}
