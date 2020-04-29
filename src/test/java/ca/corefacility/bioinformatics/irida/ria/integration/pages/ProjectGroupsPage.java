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

public class ProjectGroupsPage extends AbstractPage {
	private static AntTable table;

	@FindBy(className = "t-remove-btn")
	private List<WebElement> removeGroupButtons;

	@FindBy(className = "t-remove-success")
	private WebElement removeSuccessNotification;

	@FindBy(className = "t-add-btn")
	private WebElement addGroupBtn;

	@FindBy(className = "t-add-group-modal")
	private WebElement addGroupModal;

	@FindBy(className = "t-new-group")
	private List<WebElement> newGroupList;

	public ProjectGroupsPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectGroupsPage goTo(WebDriver driver) {
		get(driver, "/projects/1/settings/groups");
		table = AntTable.getTable(driver);
		return PageFactory.initElements(driver, ProjectGroupsPage.class);
	}

	public int getNumberOfGroups() {
		return table.getRows().size();
	}

	public void removeGroup() {
		removeGroupButtons.get(0).click();
		driver.findElement(By.className("t-remove-confirm")).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(removeSuccessNotification));
	}

	public void addGroup(String groupName) {
		addGroupBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(addGroupModal));
		WebElement input = driver.switchTo().activeElement();
		input.sendKeys(groupName);
		newGroupList.get(0).click();
		WebElement modalOkBtn = addGroupModal.findElement(By.cssSelector(".ant-btn.ant-btn-primary"));
		wait.until(ExpectedConditions.elementToBeClickable(modalOkBtn));
		modalOkBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(addGroupModal));
	}
}
