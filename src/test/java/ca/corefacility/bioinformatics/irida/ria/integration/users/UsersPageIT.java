package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.UsersPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UsersPageIT.xml")
public class UsersPageIT extends AbstractIridaUIITChromeDriver {
	private UsersPage usersPage;

	@Test
	public void testUsersTableAsAsmin() {
		LoginPage.loginAsAdmin(driver());
		usersPage = UsersPage.goTo(driver());

		assertEquals("Projects table should be populated by 3 projects", 3, usersPage.usersTableSize());

		assertTrue("Admin should be able to modify user state", usersPage.canUserModifyUserState());
		assertTrue("Admin should be able to directly go to edit user page", usersPage.canUserAccessUserEditPage());

		// Test sorting
		assertTrue("Table should be sorted by the modified date initially", usersPage.isTableSortedByModifiedDate());
		assertFalse("Table should not be sorted by username", usersPage.isTableSortedByUsername());
		usersPage.sortTableByUsername();
		assertTrue("Table should be sorted by username", usersPage.isTableSortedByUsername());
		usersPage.sortTableByModifiedDate();
		assertFalse("Table should not be sorted by username", usersPage.isTableSortedByUsername());

	}

	@Test
	public void testUsersTableAsManager() {
		LoginPage.loginAsManager(driver());
		usersPage = UsersPage.goTo(driver());

		assertEquals("Projects table should be populated by 3 projects", 3, usersPage.usersTableSize());

		assertFalse("Manager should not be able to modify user state", usersPage.canUserModifyUserState());
		assertFalse("Manager should not be able to access the edit user page", usersPage.canUserAccessUserEditPage());
	}
}
