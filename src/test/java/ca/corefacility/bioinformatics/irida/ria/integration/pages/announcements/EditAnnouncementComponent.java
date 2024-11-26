package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page object to represent the edit announcements modal
 */
public class EditAnnouncementComponent extends AbstractPage {

	@FindBy(css = "form.ant-form input#title")
	private WebElement input;

	@FindBy(xpath = "//div/div[contains(@aria-label, 'editable markdown')]/p")
	private WebElement textarea;

	@FindBy(css = "form.ant-form input#priority")
	private WebElement checkbox;

	@FindBy(css = "form.ant-form button.t-submit-announcement")
	private WebElement submitButton;

	@FindBy(css = "button.ant-modal-close")
	private WebElement cancelButton;

	public EditAnnouncementComponent(WebDriver driver) {
		super(driver);
	}

	public static EditAnnouncementComponent goTo(WebDriver driver) {
		return PageFactory.initElements(driver, EditAnnouncementComponent.class);
	}

	public String getTitle() {
		return input.getAttribute("value");
	}

	public String getMessage() {
		return textarea.getText();
	}

	public boolean getPriority() {
		return checkbox.isSelected();
	}

	public void enterAnnouncement(String title, String message, Boolean priority) {
		input.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		input.sendKeys(title);

		textarea.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		textarea.sendKeys(message);

		if (priority && !checkbox.isSelected()) {
			checkbox.click();
		} else if (!priority && checkbox.isSelected()) {
			checkbox.click();
		}

		submitButton.click();
		waitForElementInvisible(By.className("ant-modal-content"));
	}

	public void clickCancelButton() {
		cancelButton.click();
	}

}
