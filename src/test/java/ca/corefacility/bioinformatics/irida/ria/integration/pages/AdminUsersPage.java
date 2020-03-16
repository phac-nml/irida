package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;

/**
 * UI Testing for the AdminUsersPage react component.
 */
public class AdminUsersPage extends AbstractPage {
	private static AntTable table;

	public AdminUsersPage(WebDriver driver) {
		super(driver);
	}

	public static AdminUsersPage goTo(WebDriver driver) {
		get(driver, "users");
		table = AntTable.getTable(driver);
		return PageFactory.initElements(driver, AdminUsersPage.class);
	}
	
	public int usersTableSize() {
		return table.getRows().size();
	}

	public boolean isTableSortedByUsername() {
		return table.isColumnSorted("t-username");
	}

	public boolean isTableSortedByModifiedDate() {
		return table.isColumnSorted("t-modified");
	}

	public void sortTableByUsername() {
		table.sortFixedColumn("t-username-col");
	}

	public void sortTableByModifiedDate() { table.sortColumn("t-modified-col");}
}
