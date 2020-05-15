package ca.corefacility.bioinformatics.irida.ria.integration.pages.groups;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AddMemberButton;
import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page to represent the group page.
 */
public class UserGroupsPage extends AbstractPage {
	private static final String GROUP_LISTING_PAGE = "groups";
	private static final String GROUP_RELATIVE_URL = "groups/{groupId}";

	private static AntTable table;
	private static AddMemberButton addMemberButton;

	public UserGroupsPage(WebDriver driver) {
		super(driver);
	}

	public static UserGroupsPage goToGroupsPage(WebDriver driver) {
		get(driver, GROUP_LISTING_PAGE);
		table = AntTable.getTable(driver);
		addMemberButton = AddMemberButton.getAddMemberButton(driver);
		return PageFactory.initElements(driver, UserGroupsPage.class);
	}

	public static void goToGroupPage(WebDriver driver, int groupId) {
		get(driver, GROUP_RELATIVE_URL.replace("{groupId}", String.valueOf(groupId)));
	}

	public int getNumberOfMembers() {
		return table.getRows().size();
	}

	public void addGroupMember(String searchTerm, String role) {
		addMemberButton.addMember(driver, searchTerm);
	}
}
