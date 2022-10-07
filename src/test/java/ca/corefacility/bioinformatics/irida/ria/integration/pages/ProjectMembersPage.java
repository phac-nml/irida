package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AddMemberButton;
import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 */
public class ProjectMembersPage extends AbstractPage {
	private static AntTable table;
	private static AddMemberButton addMemberButton;

	@FindBy(tagName = "h2")
	private WebElement title;

	@FindBy(className = "t-remove-btn")
	private List<WebElement> removeMemberButtons;

	@FindBy(className = "t-remove-popover")
	private WebElement removePopover;

	@FindBy(className = "t-remove-success")
	private WebElement removeSuccessNotification;

	@FindBy(className = "t-remove-error")
	private WebElement removeErrorNotification;

	@FindBy(className = "t-project-role-select")
	private List<WebElement> projectRoleSelect;

	@FindBy(className = "t-metadata-role-select")
	private List<WebElement> metadataRoleSelect;

	@FindBy(className = "ant-notification-notice-success")
	private WebElement antSuccessNotification;

	@FindBy(className = "ant-notification")
	private WebElement antNotification;

	@FindBy(className = "ant-notification-notice-close")
	private WebElement antNotificationClose;

	public ProjectMembersPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectMembersPage goTo(WebDriver driver) {
		get(driver, "projects/1/settings/members");
		table = AntTable.getTable(driver);
		addMemberButton = AddMemberButton.getAddMemberButton(driver);
		return PageFactory.initElements(driver, ProjectMembersPage.class);
	}

	public static ProjectMembersPage goToRemoteProject(WebDriver driver, Long projectId) {
		get(driver, "projects/" + projectId + "/settings/members");
		table = AntTable.getTable(driver);
		addMemberButton = AddMemberButton.getAddMemberButton(driver);
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
	}

	public void removeManager(int row) {
		removeMember(row);
	}

	private void removeMember(int row) {
		removeMemberButtons.get(row).click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		wait.until(ExpectedConditions.visibilityOf(removePopover));
		wait.until(ExpectedConditions.elementToBeClickable(By.className("t-remove-confirm")));
		driver.findElement(By.className("t-remove-confirm")).click();
		wait.until(ExpectedConditions.visibilityOf(antNotification));
	}

	public boolean isNotificationDisplayed() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(antNotification));
		antNotificationClose.click();
		wait.until(ExpectedConditions.invisibilityOf(antNotification));
		return true;
	}

	public void addUserToProject(String name) {
		addMemberButton.addMember(driver, name);
	}

	public boolean isAddMemberBtnVisible() {
		return driver.findElements(By.className("t-add-member-btn")).size() > 0;
	}

	public void updateUserRole(int row, String role) {
		WebElement roleSelect = projectRoleSelect.get(row);
		roleSelect.click();
		driver.findElement(By.className("t-" + role)).click();
	}

	public void updateMetadataRole(int row, String role) {
		WebElement roleSelect = metadataRoleSelect.get(row);
		roleSelect.click();
		driver.findElement(By.className("t-" + role)).click();
	}

	public boolean userMetadataRoleSelectEnabled(int row) {
		WebElement roleSelect = metadataRoleSelect.get(row);
		return !roleSelect.getAttribute("class").contains("disabled");
	}

	public boolean lastManagerRemoveButtonEnabled(int row) {
		WebElement removeButtonForLastManager = removeMemberButtons.get(row);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOf(removeButtonForLastManager));
		return removeButtonForLastManager.findElement(By.className("ant-btn")).isEnabled();
	}
}
