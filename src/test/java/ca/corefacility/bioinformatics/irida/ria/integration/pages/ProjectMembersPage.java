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

	@FindBy(tagName = "h2")
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

	@FindBy(className = "t-new-member")
	private List<WebElement> newMemberList;

	@FindBy(className = "t-role-select")
	private List<WebElement> roleSelects;

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

	public boolean isUpdateMemberErrorNotificationDisplayed() {
		return removeErrorNotification.isDisplayed();
	}

	public boolean isUpdateMemberSuccessNotificationDisplayed() {
		return removeSuccessNotification.isDisplayed();
	}

	public void addUserToProject(String name) {
		addMemberBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(addMemberModal));
		WebElement input = driver.switchTo().activeElement();
		input.sendKeys(name);
		wait.until(ExpectedConditions.visibilityOf(newMemberList.get(0)));
		newMemberList.get(0).click();
		WebElement modalOkBtn = addMemberModal.findElement(By.cssSelector(".ant-btn.ant-btn-primary"));
		wait.until(ExpectedConditions.elementToBeClickable(modalOkBtn));
		modalOkBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(addMemberModal));
	}

	public void updateUserRole(int row, String role) {
		WebElement roleSelect = roleSelects.get(row);
		roleSelect.click();
		driver.findElement(By.className("t-" + role)).click();
	}
}
