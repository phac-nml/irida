package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;

/**
 * UI Testing for the UsersPage react component.
 */
public class UsersPage extends AbstractPage {
	private static AntTable table;

	@FindBy(css = ".t-cb-enable input[type='checkbox']")
	private List<WebElement> enableCheckboxes;

	public UsersPage(WebDriver driver) {
		super(driver);
	}

	public static UsersPage goTo(WebDriver driver) {
		get(driver, "users");
		table = AntTable.getTable(driver);
		return PageFactory.initElements(driver, UsersPage.class);
	}

	public static UsersPage goToAdminPanel(WebDriver driver) {
		get(driver, "admin/users");
		table = AntTable.getTable(driver);
		return PageFactory.initElements(driver, UsersPage.class);
	}

	public int usersTableSize() {
		return table.getRows()
				.size();
	}

	public boolean isTableSortedByUsername() {
		return table.isColumnSorted("t-username", null);
	}

	public boolean isTableSortedByModifiedDate() {
		return table.isColumnSorted("t-modified", AntTable.LONG_DATE_FORMAT);
	}

	public void sortTableByUsername() {
		table.sortColumn("t-username-col");
	}

	public void sortTableByModifiedDate() {
		table.sortColumn("t-modified-col");
	}

	public boolean canUserModifyUserState() {
		return enableCheckboxes.get(0)
				.isEnabled();
	}
}
