package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.UsersPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UsersPageIT.xml")
public class UsersPageIT extends AbstractIridaUIITChromeDriver {
	private UsersPage usersPage;

	@Test
	public void testUsersTableAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		usersPage = UsersPage.goToAdminPanel(driver());

		assertEquals(3, usersPage.usersTableSize(), "Projects table should be populated by 3 projects");

		assertTrue(usersPage.canUserModifyUserState(), "Admin should be able to modify user state");
		assertTrue(usersPage.canUserAccessUserEditPage(), "Admin should be able to directly go to edit user page");

		// Test sorting
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
		usersPage = UsersPage.goTo(driver());

		assertEquals(3, usersPage.usersTableSize(), "Projects table should be populated by 3 projects");

		assertFalse(usersPage.canUserModifyUserState(), "Manager should not be able to modify user state");
		assertFalse(usersPage.canUserAccessUserEditPage(), "Manager should not be able to access the edit user page");
	}
}
