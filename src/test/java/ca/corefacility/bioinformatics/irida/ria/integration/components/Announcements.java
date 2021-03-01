package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Used to test the state of the React Announcements component.
 * This component can be found on the dashboard page.
 */
public class Announcements {
	private static final String RATIO_REGULAR_EXPRESSION = "Read: (\\d+) / (\\d+)";

	@FindBy(className = "t-announcements-modal")
	private WebElement modal;

	@FindBy(className = "t-read-over-unread-ratio")
	private WebElement readOverUnreadRatio;

	@FindBy(css = ".t-announcements-modal button.t-previous-announcement-button")
	private WebElement previousButton;

	@FindBy(css = ".t-announcements-modal button.t-next-announcement-button")
	private WebElement nextButton;

	@FindBy(css = ".t-announcements-modal button.t-close-announcement-button")
	private WebElement closeButton;

	private static WebDriverWait wait;

	public static Announcements goTo(WebDriver driver) {
		wait = new WebDriverWait(driver, 3);
		return PageFactory.initElements(driver, Announcements.class);
	}

	public boolean isModalVisible(){ return modal.isDisplayed(); }

	public int getTotalReadAnnouncements() {
		int numerator = 0;
		Pattern pattern = Pattern.compile(RATIO_REGULAR_EXPRESSION);
		Matcher matcher = pattern.matcher(readOverUnreadRatio.getText());
		if(matcher.find()) {
			numerator = Integer.parseInt(matcher.group(1));
		}
		return numerator;
	}

	public int getTotalUnreadAnnouncements() {
		int denominator = 0;
		Pattern pattern = Pattern.compile(RATIO_REGULAR_EXPRESSION);
		Matcher matcher = pattern.matcher(readOverUnreadRatio.getText());
		if(matcher.find()) {
			denominator = Integer.parseInt(matcher.group(2));
		}
		return denominator;
	}

	public void clickPreviousButton() {
		previousButton.click();
	}

	public void clickNextButton() {
		nextButton.click();
	}

	public void clickCloseButton() {
		closeButton.click();
	}

	public void waitForModal() {
		wait.until(ExpectedConditions.visibilityOf(modal));
	}

	public void waitForPreviousButton() {
		wait.until(ExpectedConditions.visibilityOf(previousButton));
	}

	public void waitForNextButton() {
		wait.until(ExpectedConditions.visibilityOf(nextButton));
	}

	public void waitForCloseButton() {
		wait.until(ExpectedConditions.visibilityOf(closeButton));
	}
}