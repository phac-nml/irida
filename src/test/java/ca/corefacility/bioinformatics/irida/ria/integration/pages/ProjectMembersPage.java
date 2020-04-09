package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 */
public class ProjectMembersPage extends AbstractPage {
	private static AntTable table;

	@FindBy(className = "ant-page-header-heading-title")
	private WebElement title;

	@FindBy(className = "t-remove-member-btn")
	private List<WebElement> removeMemberButtons;

	@FindBy(className = "t-remove-success")
	private WebElement removeSuccessNotification;

	@FindBy(className = "t-remove-error")
	private WebElement removeErrorNotification;

	@FindBy(className = "t-add-member-btn")
	private WebElement addMemberBtn;

	@FindBy(className = "t-add-member-modal")
	private WebElement addMemberModal;

	@FindBy(css = ".t-add-member-modal .ant-select-selection-search-input")
	private WebElement addMemberInput;

	public ProjectMembersPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectMembersPage goTo(WebDriver driver) {
		get(driver, "projects/1/settings/members");
		table = AntTable.getTable(driver);
		return PageFactory.initElements(driver, ProjectMembersPage.class);
	}

	public String getPageHeaderTitle() {
		return title.getText();
	}

	public int getNumberOfMembers() {
		return table.getRows().size();
	}

	public void removeUser(int row) {
		removeMember(row);
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(removeSuccessNotification));
	}

	public void removeManager(int row) {
		removeMember(row);
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(removeErrorNotification));
	}

	private void removeMember(int row) {
		removeMemberButtons.get(row).click();
		driver.findElement(By.className("t-remove-confirm")).click();
	}

	public boolean isRemoveMemberErrorNotificationDisplayed() {
		return removeErrorNotification.isDisplayed();
	}

	public boolean isRemoveMemberSuccessNotificationDisplayed() {
		return removeSuccessNotification.isDisplayed();
	}

	public void addUserToProject(String name) {
		addMemberBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(addMemberModal));
		waitForTime(2000);
		// TODO: this does not seem to stay active
		driver.switchTo().activeElement().sendKeys("te");
		String f = "b";
	}

	//	public static final String RELATIVE_URL = "projects/1/settings/members";
//	public static final String GROUPS_URL = "projects/1/settings/groups";
//
//	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersPage.class);
//
//	public ProjectMembersPage(WebDriver driver) {
//		super(driver);
//	}
//
//	public void goToPage() {
//		get(driver, RELATIVE_URL);
//	}
//
//	public void goToGroupsPage() {
//		get(driver, GROUPS_URL);
//	}
//
//	public String getTitle() {
//		return title.getText();
//	}
//
//	public List<String> getProjectMembersNames() {
//		List<WebElement> els = driver.findElements(By.cssSelector("td:first-child a"));
//		return els.stream()
//				.map(WebElement::getText)
//				.collect(Collectors.toList());
//	}
//
//	public void clickRemoveUserButton(Long id) {
//		logger.debug("Clicking remove user button for " + id);
//		WebElement row = waitForElementVisible(By.cssSelector("[data-user='" + id + "']"));
//		WebElement removeUserButton = row.findElement(By.className("remove-btn"));
//		removeUserButton.click();
//	}
//
//	public void clickModalPopupButton() {
//		logger.debug("Confirming user removal");
//		WebElement myDynamicElement = (new WebDriverWait(driver, 10)).until(
//				ExpectedConditions.elementToBeClickable(By.id("remove-member-button")));
//
//		myDynamicElement.click();
//		waitForAjax();
//	}
//
//	public void setRoleForUser(Long id, String roleValue) {
//		logger.debug("Setting user " + id + " role to " + roleValue);
//		WebElement findElement = driver.findElement(By.id(id + "-role-select"));
//		Select roleSelect = new Select(findElement);
//		roleSelect.selectByValue(roleValue);
//		waitForAjax();
//	}
//
//	public boolean checkSuccessNotification() {
//		PageUtilities utilities = new PageUtilities(driver);
//		return utilities.checkSuccessNotification();
//	}
//
//	public boolean addGroupButtonDisplayed() {
//		logger.debug("Checking if add group button is displayed");
//		boolean present = false;
//
//		if (!driver.findElements(By.id("add-members-button"))
//				.isEmpty()) {
//			present = true;
//		}
//
//		return present;
//	}
//
//	public void clickAddMember() {
//		WebDriverWait wait = new WebDriverWait(driver, 10);
//		WebElement addMembers = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-members-button")));
//		addMembers.click();
//		waitForAjax();
//	}
//
//	public void addUserToProject(final String username, final ProjectRole role) {
//		WebDriverWait wait = new WebDriverWait(driver, 10);
//		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("addMemberModal"))));
//		Select2Utility select2Utility = new Select2Utility(driver);
//		select2Utility.openSelect2Input();
//		select2Utility.searchByText(username);
//		select2Utility.selectDefaultMatch();
//
//		WebElement roleElement = driver.findElement(By.id("add-member-role"));
//		Select roleSelect = new Select(roleElement);
//		roleSelect.selectByValue(role.toString());
//
//		WebElement submit = driver.findElement(By.id("submitAddMember"));
//		submit.click();
//		waitForAjax();
//	}
//
//	public void clickGroupsLink() {
//		driver.findElement(By.id("project-groups-link"))
//				.click();
//	}
//
//	private void waitForAjax() {
//		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
//		wait.until(Ajax.waitForAjax(60000));
//	}
}
