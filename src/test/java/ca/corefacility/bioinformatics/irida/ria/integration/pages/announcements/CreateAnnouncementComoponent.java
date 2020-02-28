package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page object to represent the Announcements Creation page
 */
public class CreateAnnouncementComoponent extends AbstractPage {
    @FindBy(className = "mde-text")
    private WebElement textarea;

    @FindBy(className = "t-submit-announcement")
    private WebElement submitButton;

    public CreateAnnouncementComoponent(WebDriver driver) {
        super(driver);
    }

    public static CreateAnnouncementComoponent goTo(WebDriver driver) {
        return PageFactory.initElements(driver, CreateAnnouncementComoponent.class);
    }

    public void enterMessage(String message) {
        textarea.sendKeys(message);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, 10);
    }
}
