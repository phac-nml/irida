package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object to represent the create new announcements modal
 */
public class CreateAnnouncementComponent extends AbstractPage {
	@FindBy(id = "title")
	private WebElement titleInput;

	@FindBy(xpath = "//div/div[contains(@aria-label, 'editable markdown')]/p")
	private WebElement textarea;

	@FindBy(id = "priority")
	private WebElement checkbox;

	@FindBy(className = "t-submit-announcement")
	private WebElement submitButton;

	public CreateAnnouncementComponent(WebDriver driver) {
		super(driver);
	}

	public static CreateAnnouncementComponent goTo(WebDriver driver) {
		return PageFactory.initElements(driver, CreateAnnouncementComponent.class);
	}

	public void enterAnnouncement(String title, String message, Boolean priority) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.elementToBeClickable(titleInput));

		titleInput.sendKeys(title);
		// Send the message one character at a time to avoid issues with selenium
		for (char c : message.toCharArray()) {
			textarea.sendKeys(String.valueOf(c));
		}

		if (priority) {
			checkbox.click();
		}

		submitButton.click();
		waitForElementInvisible(By.className("ant-modal-content"));
	}
}
