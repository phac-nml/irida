package ca.corefacility.bioinformatics.irida.ria.integration.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Used to test the state of the React Announcements component.
 * This component can be found on the dashboard page.
 */
public class Announcements {
	@FindBy(className = "t-modal")
	private WebElement modal;

	@FindBy(className = "t-read-over-unread-ratio")
	private WebElement readOverUnreadRatio;

	@FindBy(css = ".t-modal button.ant-btn-primary")
	private WebElement readButton;

	public static Announcements goTo(WebDriver driver) {
		return PageFactory.initElements(driver, Announcements.class);
	}

	public boolean isModalVisible(){ return modal.isDisplayed(); }

	public int getTotalReadAnnouncements() {
		String numerator = readOverUnreadRatio.getText().split("/")[0].trim();
		return Integer.parseInt(numerator);
	}

	public int getTotalUnreadAnnouncements() {
		String denominator = readOverUnreadRatio.getText().split("/")[1].trim();
		return Integer.parseInt(denominator);
	}

	public void clickReadButton() {
		readButton.click();
	}
}