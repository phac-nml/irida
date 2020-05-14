package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AdminUsersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UsersPageIT.xml")
public class AdminUsersPageIT extends AbstractIridaUIITChromeDriver {
	private AdminUsersPage adminUsersPage;

	@Before
	public void setUp() {
		LoginPage.loginAsAdmin(driver());
		adminUsersPage = AdminUsersPage.goTo(driver());
	}

	@Test
	public void testTableSetUp() {
		assertEquals("Projects table should be populated by 3 projects", 3, adminUsersPage.usersTableSize());

		// Test sorting
		assertTrue("Table should be sorted by the modified date initially", adminUsersPage.isTableSortedByModifiedDate());
		assertFalse("Table should not be sorted by username", adminUsersPage.isTableSortedByUsername());
		adminUsersPage.sortTableByUsername();
		assertTrue("Table should be sorted by username", adminUsersPage.isTableSortedByUsername());
		adminUsersPage.sortTableByModifiedDate();
		assertFalse("Table should not be sorted by username", adminUsersPage.isTableSortedByUsername());

	}
}
