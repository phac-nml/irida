package ca.corefacility.bioinformatics.irida.ria.integration.pages.groups;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AddMemberButton;
import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class UserGroupsDetailsPage extends AbstractPage {
	private static AntTable table;
	private static AddMemberButton addMemberButton;

	@FindBy(className = "t-group-name")
	private WebElement groupName;

	@FindBy(css = ".t-group-name .ant-typography-edit")
	private WebElement editNameBtn;

	@FindBy(css = ".t-group-name.ant-typography-edit-content textarea")
	private WebElement editNameInput;

	@FindBy(className = "t-tab-delete")
	private WebElement deleteTab;

	@FindBy(className = "t-delete-group-btn")
	private WebElement deleteBtn;

	@FindBy(className = "t-delete-confirm-btn")
	private WebElement deleteConfirmBtn;

	public UserGroupsDetailsPage(WebDriver driver) {
		super(driver);
	}

	public static UserGroupsDetailsPage initPage(WebDriver driver) {
		table = AntTable.getTable(driver);
		addMemberButton = AddMemberButton.getAddMemberButton(driver);
		return PageFactory.initElements(driver, UserGroupsDetailsPage.class);
	}

	public void gotoPage(int groupId) {
		get(driver, "groups/" + groupId);
	}

	public void gotoAdminPage(int groupId) {
		get(driver, "admin/groups/" + groupId);
	}

	public String getUserGroupName() {
		return groupName.getText();
	}

	public void updateUserGroupName(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.elementToBeClickable(editNameBtn));
		editNameBtn.click();
		wait.until(ExpectedConditions.visibilityOf(editNameInput));
		editNameInput.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		editNameInput.sendKeys(name);
		editNameInput.sendKeys(Keys.ENTER);
		wait.until(ExpectedConditions.textToBePresentInElement(groupName, name));
	}

	public int getNumberOfMembers() {
		return table.getRows().size();
	}

	public void addGroupMember(String searchTerm, String role) {
		addMemberButton.addMember(driver, searchTerm, role);
	}

	public void deleteGroup() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.elementToBeClickable(deleteTab));
		deleteTab.click();
		wait.until(ExpectedConditions.elementToBeClickable(deleteBtn));
		deleteBtn.click();
		wait.until(ExpectedConditions.elementToBeClickable(deleteConfirmBtn));
		deleteConfirmBtn.click();
	}

	public String getUserGroupMemberRole(int row) {
		List<WebElement> memberSelectedRoles = driver.findElements(By.className("ant-select-selection-item"));
		return memberSelectedRoles.get(row).getText();
	}
}
