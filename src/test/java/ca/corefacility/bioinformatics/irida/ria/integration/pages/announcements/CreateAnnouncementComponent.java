package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page object to represent the create new announcements modal
 */
public class CreateAnnouncementComponent extends AbstractPage {
	@FindBy(id = "title")
	private WebElement input;

	@FindBy(className = "mde-text")
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
		input.sendKeys(title);
		textarea.sendKeys(message);

		if (priority) {
			checkbox.click();
		}

		submitButton.click();
		waitForElementInvisible(By.className("ant-modal-content"));
	}
}
