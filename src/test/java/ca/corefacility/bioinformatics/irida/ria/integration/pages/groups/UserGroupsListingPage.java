package ca.corefacility.bioinformatics.irida.ria.integration.pages.groups;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page to represent the group page.
 */
public class UserGroupsListingPage extends AbstractPage {
	private static final String GROUP_LISTING_PAGE = "groups";
	private static WebDriver webDriver;
	private static AntTable table;

	@FindBy(className = "t-create-group-btn")
	private WebElement createGroupBtn;

	@FindBy(className = "t-new-group-modal")
	private WebElement newGroupModal;

	@FindBy(className = "t-confirm-new-group")
	private WebElement newGroupConfirmButton;

	public UserGroupsListingPage(WebDriver driver) {
		super(driver);
	}

	public static UserGroupsListingPage initPage(WebDriver driver) {
		webDriver = driver;
		table = AntTable.getTable(driver);
		return PageFactory.initElements(driver, UserGroupsListingPage.class);
	}

	public void gotoPage() {
		get(driver, GROUP_LISTING_PAGE);
	}

	public int getNumberOfExistingUserGroups() {
		return table.getRows().size();
	}

	public void createNewUserGroup(String name) {
		createGroupBtn.click();
		WebDriverWait wait = new WebDriverWait(webDriver, 5);
		wait.until(ExpectedConditions.visibilityOf(newGroupModal));
		WebElement input = driver.switchTo().activeElement();
		input.sendKeys(name);
		newGroupConfirmButton.click();
	}
}
