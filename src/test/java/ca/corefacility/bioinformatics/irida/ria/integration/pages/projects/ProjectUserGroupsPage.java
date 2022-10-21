package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectUserGroupsPage extends AbstractPage {
	@FindBy(className = "t-add-user-group-btn")
	private WebElement addUserGroupBtn;

	@FindBy(className = "t-add-user-group-modal")
	private WebElement addUserGroupModal;

	@FindBy(className = "t-new-member")
	private List<WebElement> addGroupNameList;

	@FindBy(className = "t-remove-btn")
	private List<WebElement> removeButtons;

	private static AntTable table;

	public ProjectUserGroupsPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectUserGroupsPage goToPage(WebDriver driver, Long projectId) {
		get(driver, "projects/" + projectId + "/settings/groups");
		table = AntTable.getTable(driver);
		return PageFactory.initElements(driver, ProjectUserGroupsPage.class);
	}

	public boolean isAddUserGroupButtonVisible() {
		return driver.findElements(By.className("t-add-user-group-btn")).size() == 1;
	}

	public int getNumberOfUserGroups() {
		return table.getRows().size();
	}

	public void removeUserGroups(int row) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		removeButtons.get(row).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-remove-confirm")));
		WebElement removeConfirm = driver.findElement(By.className("t-remove-confirm"));
		removeConfirm.click();
		wait.until(ExpectedConditions.invisibilityOf(removeConfirm));
	}

	public void addUserGroup(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		addUserGroupBtn.click();
		wait.until(ExpectedConditions.visibilityOf(addUserGroupModal));
		WebElement input = driver.switchTo().activeElement();
		input.sendKeys(name);
		wait.until(ExpectedConditions.visibilityOf(addGroupNameList.get(0)));
		addGroupNameList.get(0).click();
		WebElement modalOkBtn = addUserGroupModal.findElement(By.cssSelector(".ant-btn.ant-btn-primary"));
		wait.until(ExpectedConditions.elementToBeClickable(modalOkBtn));
		modalOkBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(addUserGroupModal));
	}
}
