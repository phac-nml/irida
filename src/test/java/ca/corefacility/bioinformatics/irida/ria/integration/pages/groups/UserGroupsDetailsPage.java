package ca.corefacility.bioinformatics.irida.ria.integration.pages.groups;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AddMemberButton;
import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class UserGroupsDetailsPage extends AbstractPage {
	private static AntTable table;
	private static AddMemberButton addMemberButton;

	@FindBy(className = "t-group-name")
	private WebElement groupName;

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

	public String getUserGroupName() {
		return groupName.getText();
	}

	public int getNumberOfMembers() {
		return table.getRows().size();
	}

	public void addGroupMember(String searchTerm, String role) {
		addMemberButton.addMember(driver, searchTerm);
	}
}
