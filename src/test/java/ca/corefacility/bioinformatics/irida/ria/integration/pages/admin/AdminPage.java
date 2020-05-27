package ca.corefacility.bioinformatics.irida.ria.integration.pages.admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AdminPage extends AbstractPage {
	public static final String RELATIVE_URL = "/";
	public static final String ADMIN_RELATIVE_URL = "admin/";

	@FindBy(id="t-admin-panel-btn")
	private WebElement adminPanelBtn;

	public AdminPage(WebDriver driver) { super(driver); }

	/**
	 * Initialize the page so that the default {@Link WebElement} has been found.
	 *
	 * @param driver	{@Link WebDriver}
	 * @return The initialized {@Link AdminPage}
	 */
	public static AdminPage initPage(WebDriver driver) {
		get(driver, RELATIVE_URL);
		return PageFactory.initElements(driver, AdminPage.class);
	}

	/**
	 * Navigate to the admin panel page on path 'admin/'.
	 *
	 * @param driver	{@Link WebDriver}
	 */
	public void goToAdminPage(WebDriver driver) {
		get(driver, ADMIN_RELATIVE_URL);
	}

	/**
	 *  Determines if admin panel title is
	 *  visible on the admin panel page.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminPanelTitleVisible() {
		return driver.findElements(By.id("t-admin-panel-title"))
				.size() == 1;
	}

	/**
	 *  Determines if admin panel button is
	 *  visible on the navbar.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminPanelButtonVisible() {
		return driver.findElements(By.id("t-admin-panel-btn"))
				.size() == 1;
	}

	/**
	 *  Clicks on the admin panel button to navigate
	 *  to the admin panel page.
	 */
	public void clickAdminButton() {
		adminPanelBtn.click();
		waitForTime(500);
	}
}
