package ca.corefacility.bioinformatics.irida.ria.integration.pages.admin;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AdminPage extends AbstractPage {
	public static final String RELATIVE_URL = "/";
	public static final String ADMIN_RELATIVE_URL = "admin/";

	@FindBy(className="t-admin-panel-btn")
	private List<WebElement> adminPanelBtn;

	@FindBy(className="t-admin-stats-title")
	private List<WebElement> adminStatsTitle;

	@FindBy(className="t-admin-users-title")
	private List<WebElement> adminUsersTitle;

	@FindBy(className="t-admin-groups-title")
	private List<WebElement> adminGroupsTitle;

	@FindBy(className="t-admin-stats-menu")
	private List<WebElement> adminStatsMenu;

	@FindBy(className="t-admin-users-sub-menu")
	private List<WebElement> adminUsersSubMenu;

	@FindBy(className="t-admin-users-menu")
	private List<WebElement> adminUsersMenu;

	@FindBy(className="t-admin-groups-menu")
	private List<WebElement> adminGroupsMenu;

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
	 *  Determines if admin stats title is
	 *  visible on the admin panel page.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminStatsTitleVisible() {
		return adminStatsTitle.size() == 1;
	}

	/**
	 *  Determines if admin users title is
	 *  visible on the admin panel page.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminUsersTitleVisible() {
		return adminUsersTitle.size() == 1;
	}

	/**
	 *  Determines if admin user groups title is
	 *  visible on the admin panel page.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminGroupsTitleVisible() {
		return adminGroupsTitle.size() == 1;
	}

	/**
	 *  Determines if admin panel button is
	 *  visible on the navbar.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminPanelButtonVisible() {
		return adminPanelBtn.size() == 1;
	}

	/**
	 *  Clicks on the admin panel button to navigate
	 *  to the admin panel page.
	 */
	public void clickAdminButton() {
		adminPanelBtn.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin"));
	}

	/**
	 *  Clicks on the admin stats menu button to navigate
	 *  to the admin stats page.
	 */
	public void clickStatsMenu() {
		adminStatsMenu.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/statistics"));
	}

	/**
	 *  Clicks on the users submenu to open it up
	 *  and give access to its options.
	 */
	public void clickUsersSubMenu() {
		adminUsersSubMenu.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin"));
	}

	/**
	 *  Clicks on the admin users menu button to navigate
	 *  to the admin users page.
	 */
	public void clickUsersMenu() {
		adminUsersMenu.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/users"));
	}

	/**
	 *  Clicks on the admin user groups menu button to navigate
	 *  to the admin user groups page.
	 */
	public void clickGroupsMenu() {
		adminGroupsMenu.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/groups"));
	}
}
