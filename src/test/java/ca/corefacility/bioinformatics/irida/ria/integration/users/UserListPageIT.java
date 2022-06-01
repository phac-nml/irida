package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.CreateNewUserComponent;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserListPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserListPageIT extends AbstractIridaUIITChromeDriver {
	private UserListPage usersPage;

	@Test
	public void testUsersTableAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		usersPage = UserListPage.goToAdminPanel(driver());
		assertEquals(3, usersPage.usersTableSize(), "Projects table should be populated by 3 projects");
		assertTrue(usersPage.canUserModifyUserState(), "Admin should be able to modify user state");
		assertTrue(usersPage.isTableSortedByModifiedDate(), "Table should be sorted by the modified date initially");
		assertFalse(usersPage.isTableSortedByUsername(), "Table should not be sorted by username");

		usersPage.sortTableByUsername();
		assertTrue(usersPage.isTableSortedByUsername(), "Table should be sorted by username");

		usersPage.sortTableByModifiedDate();
		assertFalse(usersPage.isTableSortedByUsername(), "Table should not be sorted by username");
	}

	@Test
	public void testUsersTableAsManager() {
		LoginPage.loginAsManager(driver());
		usersPage = UserListPage.goTo(driver());
		assertEquals(3, usersPage.usersTableSize(), "Table should be populated by 3 projects");
		assertFalse(usersPage.canUserModifyUserState(), "Manager should not be able to modify user state");
	}

	@Test
	public void createGoodUserAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		usersPage = UserListPage.goToAdminPanel(driver());
		CreateNewUserComponent createNewUserComponent = CreateNewUserComponent.goTo(driver());
		usersPage.clickAddNewUserButton();
		createNewUserComponent.enterUserDetailsWithPassword("tom", "tom@somewhere.com", "Password1!");
		assertFalse(createNewUserComponent.hasErrors());
		createNewUserComponent.clickSubmit();
		assertTrue(createNewUserComponent.hasSuccessfulNotification());
	}

	@Test
	public void createGoodUserAsAdminAsManager() {
		LoginPage.loginAsManager(driver());
		usersPage = UserListPage.goTo(driver());
		CreateNewUserComponent createNewUserComponent = CreateNewUserComponent.goTo(driver());
		usersPage.clickAddNewUserButton();
		createNewUserComponent.enterUserDetailsWithPassword("tom", "tom@somewhere.com", "Password1!");
		assertFalse(createNewUserComponent.hasErrors());
		createNewUserComponent.clickSubmit();
		assertTrue(createNewUserComponent.hasSuccessfulNotification());
	}

	@Test
	public void createExistingUsername() {
		LoginPage.loginAsAdmin(driver());
		usersPage = UserListPage.goToAdminPanel(driver());
		CreateNewUserComponent createNewUserComponent = CreateNewUserComponent.goTo(driver());
		usersPage.clickAddNewUserButton();
		createNewUserComponent.enterUserDetailsWithPassword("mrtest", "tom@somewhere.com", "Password1!");
		createNewUserComponent.clickSubmit();
		assertTrue(createNewUserComponent.hasErrors());
		assertFalse(createNewUserComponent.hasSuccessfulNotification());
	}

	@Test
	public void createExistingEmail() {
		LoginPage.loginAsAdmin(driver());
		usersPage = UserListPage.goToAdminPanel(driver());
		CreateNewUserComponent createNewUserComponent = CreateNewUserComponent.goTo(driver());
		usersPage.clickAddNewUserButton();
		createNewUserComponent.enterUserDetailsWithPassword("tom", "manager@nowhere.com", "Password1!");
		createNewUserComponent.clickSubmit();
		assertTrue(createNewUserComponent.hasErrors());
		assertFalse(createNewUserComponent.hasSuccessfulNotification());
	}

	@Test
	public void testCreateUserWithoutPassword() {
		LoginPage.loginAsAdmin(driver());
		usersPage = UserListPage.goToAdminPanel(driver());
		CreateNewUserComponent createNewUserComponent = CreateNewUserComponent.goTo(driver());
		usersPage.clickAddNewUserButton();
		createNewUserComponent.enterUserDetailsWithoutPassword("tom", "tom@somewhere.com");
		assertFalse(createNewUserComponent.hasErrors());
		createNewUserComponent.clickSubmit();
		assertTrue(createNewUserComponent.hasSuccessfulNotification());
	}
}
