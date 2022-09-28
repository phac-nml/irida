package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Used to test the state of the React AnnouncementsModal component. This component can be found at login.
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

	@FindBy(css = ".t-announcements-badge")
	private WebElement badge;

	@FindBy(className = "t-announcements-submenu")
	private WebElement submenu;

	@FindBy(className = "t-announcements-view-all")
	private WebElement viewAllLink;

	private static WebDriverWait wait;
	private static Actions actions;

	public static Announcements goTo(WebDriver driver) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(5L));
		actions = new Actions(driver);
		return PageFactory.initElements(driver, Announcements.class);
	}

	public boolean isModalVisible() {
		waitForModal();
		return modal.isDisplayed();
	}

	public void closeModal() {
		closeButton.sendKeys(Keys.ESCAPE);
	}

	public int getTotalReadAnnouncements() {
		int numerator = 0;
		Pattern pattern = Pattern.compile(RATIO_REGULAR_EXPRESSION);
		Matcher matcher = pattern.matcher(readOverUnreadRatio.getText());
		if (matcher.find()) {
			numerator = Integer.parseInt(matcher.group(1));
		}
		return numerator;
	}

	public int getTotalUnreadAnnouncements() {
		int denominator = 0;
		Pattern pattern = Pattern.compile(RATIO_REGULAR_EXPRESSION);
		Matcher matcher = pattern.matcher(readOverUnreadRatio.getText());
		if (matcher.find()) {
			denominator = Integer.parseInt(matcher.group(2));
		}
		return denominator;
	}

	public void clickNextButton() {
		nextButton.click();
	}

	public void clickCloseButton() {
		closeButton.click();
	}

	public WebElement waitForModal() {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-announcements-modal")));
	}

	public WebElement waitForPreviousButton() {
		return wait.until(ExpectedConditions.visibilityOf(previousButton));
	}

	public WebElement waitForSubmenu() {
		return wait.until(ExpectedConditions.visibilityOf(submenu));
	}

	public void openAnnouncementPopover() {
		actions.moveToElement(badge).perform();
		wait.until(ExpectedConditions.visibilityOf(viewAllLink));
	}

	public int getBadgeCount() {
		closeModal();
		return Integer.parseInt(badge.findElement(By.className("ant-scroll-number-only-unit")).getText());
	}

	public String getSubmenuAnnouncementTitle(int position) {
		return submenu.findElements(By.className("ant-list-item"))
				.get(position)
				.findElement(By.cssSelector(".ant-list-item-meta-title a"))
				.getText();
	}

	public void getSubmenuAnnouncement() {
		badge.click();
		waitForSubmenu();
	}

	public void getNextAnnouncement() {
		clickNextButton();
		waitForPreviousButton();
	}

}