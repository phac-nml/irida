package ca.corefacility.bioinformatics.irida.ria.integration.pages.admin;

import java.util.List;

import org.openqa.selenium.By;
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

	@FindBy(className = "t-admin-panel-btn")
	private List<WebElement> adminPanelBtn;

	@FindBy(className = "t-admin-side-menu")
	private List<WebElement> adminSideMenu;

	@FindBy(className = "t-admin-statistics-link")
	private List<WebElement> adminStatisticsLink;

	@FindBy(className = "t-admin-users-submenu")
	private List<WebElement> adminUsersSubMenu;

	@FindBy(className = "t-admin-users-link")
	private List<WebElement> adminUsersLink;

	@FindBy(className = "t-admin-groups-link")
	private List<WebElement> adminGroupsLink;

	@FindBy(className = "t-admin-clients-link")
	private List<WebElement> adminClientsLink;

	@FindBy(className = "t-admin-remote-api-link")
	private List<WebElement> adminRemoteApiLink;

	@FindBy(className = "t-admin-sequencing-runs-link")
	private List<WebElement> adminSequencingRunsLink;

	@FindBy(className = "t-admin-ncbi-exports-link")
	private List<WebElement> adminNcbiExportsLink;

	@FindBy(className = "t-admin-announcements-link")
	private List<WebElement> adminAnnouncementsLink;

	@FindBy(className = "t-add-user-btn")
	private List<WebElement> adminAddUserBtn;

	@FindBy(className = "t-create-group-btn")
	private List<WebElement> adminCreateGroupBtn;

	@FindBy(className = "t-add-client-btn")
	private List<WebElement> adminAddClientBtn;

	@FindBy(className = "t-add-remote-api-btn")
	private List<WebElement> adminAddRemoteApiBtn;

	@FindBy(className = "t-create-announcement")
	private List<WebElement> adminAddAnnouncementBtn;

	public AdminPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Initialize the page so that the default {@Link WebElement} has been found.
	 *
	 * @param driver {@Link WebDriver}
	 * @return The initialized {@Link AdminPage}
	 */
	public static AdminPage initPage(WebDriver driver) {
		get(driver, RELATIVE_URL);
		return PageFactory.initElements(driver, AdminPage.class);
	}

	/**
	 * Navigate to the admin panel page on path 'admin/'.
	 *
	 * @param driver {@Link WebDriver}
	 */
	public void goToAdminPage(WebDriver driver) {
		get(driver, ADMIN_RELATIVE_URL);
	}

	/**
	 * Compares the expected page title to the actual
	 *
	 * @return {@link Boolean}
	 */
	public boolean comparePageTitle(String pageTitle) {
		int titleFound = driver.findElements(By.xpath("//span[contains(text(),'" + pageTitle + "')]")).size();
		return titleFound > 0;
	}

	/**
	 * Determines if admin panel button is visible on the navbar.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminPanelButtonVisible() {
		return adminPanelBtn.size() == 1;
	}

	/**
	 * Determines if admin side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminSideMenuVisible() {
		return adminSideMenu.size() == 1;
	}

	/**
	 * Determines if admin users link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminUsersLinkVisible() {
		return adminUsersLink.size() == 1;
	}

	/**
	 * Determines if admin groups link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminGroupsLinkVisible() {
		return adminGroupsLink.size() == 1;
	}

	/**
	 * Determines if admin clients link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminClientsLinkVisible() {
		return adminClientsLink.size() == 1;
	}

	/**
	 * Determines if admin statistics link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminStatisticsLinkVisible() {
		return adminStatisticsLink.size() == 1;
	}

	/**
	 * Determines if admin remote api link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminRemoteApiLinkVisible() {
		return adminRemoteApiLink.size() == 1;
	}

	/**
	 * Determines if admin ncbi exports link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminNcbiExportsLinkVisible() {
		return adminNcbiExportsLink.size() == 1;
	}

	/**
	 * Determines if admin sequencing runs link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminSequencingRunsLinkVisible() {
		return adminSequencingRunsLink.size() == 1;
	}

	/**
	 * Determines if admin announcements link on side menu is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminAnnouncementsLinkVisible() {
		return adminAnnouncementsLink.size() == 1;
	}

	/**
	 * Determines if add user button is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminAddUserVisible() {
		return adminAddUserBtn.size() == 1;
	}

	/**
	 * Determines if create user group button is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminCreateGroupVisible() {
		return adminCreateGroupBtn.size() == 1;
	}

	/**
	 * Determines if add client button is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminAddClientVisible() {
		return adminAddClientBtn.size() == 1;
	}

	/**
	 * Determines if add remote connection button is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminAddRemoteApiVisible() {
		return adminAddRemoteApiBtn.size() == 1;
	}

	/**
	 * Determines if add announcement button is visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminAddAnnouncementVisible() {
		return adminAddAnnouncementBtn.size() == 1;
	}

	/**
	 * Determines if content portion of the page has a title visible.
	 *
	 * @return {@link Boolean}
	 */
	public boolean adminContentTitleVisible() {
		WebElement content = driver.findElement(By.xpath("//main[@class='ant-layout-content']"));
		int titleFound = content.findElements(By.className("ant-page-header-heading-title")).size();
		return titleFound > 0;
	}

	/**
	 * Clicks on the admin panel button to navigate to the admin panel page.
	 */
	public void clickAdminButton() {
		adminPanelBtn.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin"));
	}

	/**
	 * Clicks on the users submenu to open it up and give access to its options.
	 */
	public void clickUsersSubMenu() {
		adminUsersSubMenu.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin"));
	}

	/**
	 * Clicks on the admin users menu button to navigate to the admin users page.
	 */
	public void clickUsersLink() {
		adminUsersLink.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/users"));
	}

	/**
	 * Clicks on the admin user groups menu button to navigate to the admin user groups page.
	 */
	public void clickGroupsLink() {
		adminGroupsLink.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/groups"));
	}

	/**
	 * Clicks on the admin clients menu button to navigate to the admin clients page.
	 */
	public void clickClientsLink() {
		adminClientsLink.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/clients"));
	}

	/**
	 * Clicks on the admin remote api menu button to navigate to the admin remote api page.
	 */
	public void clickRemoteApiLink() {
		adminRemoteApiLink.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/remote_api"));
	}

	/**
	 * Clicks on the admin sequencing runs menu button to navigate to the admin sequencing runs page.
	 */
	public void clickSequencingRunsLink() {
		adminSequencingRunsLink.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/sequencing-runs"));
	}

	/**
	 * Clicks on the admin ncbi exports menu button to navigate to the admin ncbi exports page.
	 */
	public void clickNcbiExportsLink() {
		adminNcbiExportsLink.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/ncbi_exports"));
	}

	/**
	 * Clicks on the admin announcements menu button to navigate to the admin announcements page.
	 */
	public void clickAnnouncementsLink() {
		adminAnnouncementsLink.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.urlContains("/admin/announcements"));
	}
}
