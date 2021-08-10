package ca.corefacility.bioinformatics.irida.ria.integration.pages;

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

	@FindBy(className = "t-remove-success")
	private WebElement removeSuccessNotification;

	@FindBy(className = "t-remove-error")
	private WebElement removeErrorNotification;

	@FindBy(className = "t-role-select")
	private List<WebElement> roleSelects;

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
		get(driver, "projects/"+ projectId +"/settings/members");
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
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(removeSuccessNotification));
	}

	public void removeManager(int row) {
		WebDriverWait wait = new WebDriverWait(driver, 2);
		WebElement button = wait.until(ExpectedConditions.elementToBeClickable(removeMemberButtons.get(row)));
		button.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-remove-popover")));
		driver.findElement(By.className("t-remove-confirm")).click();
	}

	private void removeMember(int row) {
		removeMemberButtons.get(row).click();
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-remove-popover")));
		driver.findElement(By.className("t-remove-confirm")).click();
	}

	public boolean isUpdateMemberErrorNotificationDisplayed() {
		return removeErrorNotification.isDisplayed();
	}

	public boolean isUpdateMemberSuccessNotificationDisplayed() {
		return removeSuccessNotification.isDisplayed();
	}

	public void addUserToProject(String name) {
		addMemberButton.addMember(driver, name);
	}

	public boolean isAddMemberBtnVisible() {
		return driver.findElements(By.className("t-add-member-btn")).size() > 0;
	}

	public void updateUserRole(int row, String role) {
		WebElement roleSelect = roleSelects.get(row);
		roleSelect.click();
		driver.findElement(By.className("t-" + role)).click();
	}
}
