package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page object to represent the Announcements Control Admin page
 */
public class AnnouncementControlPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(AnnouncementControlPage.class);

	public AnnouncementControlPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, "admin/announcements");
	}

	/**
	 * Get the size of the announcement table currently visible on the control page
	 * 
	 * @return size of the table
	 */
	public int announcementTableSize() {
		return driver.findElements(By.cssSelector("td.t-announcement")).size();
	}

	/**
	 * Get a list of {@link WebElement}s describing currently visible announcements on the page
	 * 
	 * @return list of web elements
	 */
	public List<Date> getCreatedDates() {
		//May 20, 2016 11:42 AM
		DateFormat df = new SimpleDateFormat("MMM d, yyyy, hh:mm a");
		List<WebElement> dateElements = driver.findElements(By.cssSelector("td.t-created-date"));
		List<Date> dates = new ArrayList<>();
		for (WebElement element : dateElements) {
			String dateText = element.getText();
			try {
				dates.add(df.parse(dateText));
			} catch (ParseException e) {
				logger.debug("Cannot convert value to date: ", dateText);
			}
		}
		return dates;
	}

	public String getAnnouncementTitle(int position) {
		List<WebElement> messages = driver.findElements(By.cssSelector("td.t-announcement"));
		return messages.get(position).getText();
	}

	public void clickDateCreatedHeader() {
		WebElement header = driver.findElement(By.cssSelector("th.t-created-date"));
		header.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.urlContains("/admin/announcements"));
	}

	public void clickCreateNewAnnouncementButton() {
		waitForElementVisible(By.className("t-create-announcement"));
		WebElement createButton = driver.findElement(By.className("t-create-announcement"));
		createButton.click();
	}

	public void gotoViewMessage(int index) {
		List<WebElement> messages = driver.findElements(By.cssSelector("button.t-view-announcement"));
		if (index < messages.size()) {
			messages.get(index).click();
			waitForElementVisible(By.className("ant-modal-content"));
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void gotoEditMessage(int index) {
		List<WebElement> messages = driver.findElements(By.cssSelector("button.t-edit-announcement"));
		if (index < messages.size()) {
			messages.get(index).click();
			waitForElementVisible(By.className("ant-modal-content"));
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void deleteAnnouncement(int index) {
		List<WebElement> delete_button = driver.findElements(By.cssSelector("button.t-delete-announcement"));
		delete_button.get(index).click();
		waitForElementVisible(By.className("ant-popover-message"));
		WebElement confirm_delete_button = driver
				.findElement(By.cssSelector("div.ant-popover-buttons > button.ant-btn-primary"));
		confirm_delete_button.click();
		waitForElementInvisible(By.className("ant-popover-message"));
	}

}
