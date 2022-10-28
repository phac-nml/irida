package ca.corefacility.bioinformatics.irida.ria.integration.admin;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.admin.AdminPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/admin/AdminPageView.xml")
public class AdminPageIT extends AbstractIridaUIITChromeDriver {


	@Test
	public void accessPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		AdminPage page = AdminPage.initPage(driver());
		assertTrue(page.adminPanelButtonVisible(), "Admin button should be displayed");
		page.clickAdminButton();
		assertTrue(page.comparePageTitle("Statistics"), "Admin can navigate to admin panel, admin page title should be present");
		// Navigate to users page
		page.clickUsersLink();
		assertTrue(page.comparePageTitle("Users"), "Admin can navigate to admin panel, admin page title should be present");
		assertTrue(page.adminAddUserVisible(), "Create New User button should be present");
		// Navigate to user groups page
		page.clickGroupsLink();
		assertTrue(page.comparePageTitle("Groups"), "Admin can navigate to groups page, groups page title should be present");
		assertTrue(page.adminCreateGroupVisible(), "Create New User Group button should be present");
		// Navigate to clients page
		page.clickClientsLink();
		assertTrue(page.comparePageTitle("Clients"), "Admin can navigate to clients page, clients page title should be present");
		assertTrue(page.adminAddClientVisible(), "Add Client button should be present");
		// Navigate to remote api page
		page.clickRemoteApiLink();
		assertTrue(page.comparePageTitle("Remote IRIDA Connections"), "Admin can navigate to remote api page, remote api page title should be present");
		assertTrue(page.adminAddRemoteApiVisible(), "Add Remote Connection button should be present");
		// Navigate to sequencing runs page
		page.clickSequencingRunsLink();
		assertTrue(page.comparePageTitle("Sequencing Runs"), "Admin can navigate to sequencing runs page, sequencing runs page title should be present");
		// Navigate to ncbi exports page
		page.clickNcbiExportsLink();
		assertTrue(page.comparePageTitle("NCBI Exports"), "Admin can navigate to ncbi exports page, ncbi exports page title should be present");
		// Navigate to announcements page
		page.clickAnnouncementsLink();
		assertTrue(page.comparePageTitle("Announcements"), "Admin can navigate to announcements page, announcements page title should be present");
		assertTrue(page.adminAddAnnouncementVisible(), "Add Announcement button should be present");
		// Navigate back to users page
		page.clickUsersLink();
		assertTrue(page.comparePageTitle("Users"), "Admin can navigate to users page, user page title should be present");
		assertTrue(page.adminAddUserVisible(), "Add User button should be present");
	}

	@Test
	public void accessPageFailure() {
		LoginPage.loginAsUser(driver());
		AdminPage page = AdminPage.initPage(driver());
		assertFalse(page.adminPanelButtonVisible(), "Admin button should not be displayed");
		// No admin button, so attempt to go to admin page by modifying the URL
		page.goToAdminPage(driver());
		assertFalse(page.comparePageTitle("Statistics"), "User cannot navigate to admin panel, admin page title should not be present");
	}


	@Test
	public void testPageSetUp() {
		LoginPage.loginAsAdmin(driver());
		AdminPage page = AdminPage.initPage(driver());
		page.clickAdminButton();
		// Check that side menu is visible
		assertTrue(page.adminSideMenuVisible(), "Admin side menu should be visible");
		// Check that all top level links are visible
		assertTrue(page.adminStatisticsLinkVisible(), "Admin statistics link should be visible");
		assertTrue(page.adminClientsLinkVisible(), "Admin clients link should be visible");
		assertTrue(page.adminRemoteApiLinkVisible(), "Admin remote api link should be visible");
		assertTrue(page.adminSequencingRunsLinkVisible(), "Admin sequencing runs link should be visible");
		assertTrue(page.adminNcbiExportsLinkVisible(), "Admin ncbi exports link should be visible");
		assertTrue(page.adminAnnouncementsLinkVisible(), "Admin announcements link should be visible");
		// Open sub menu to view other links
		assertTrue(page.adminUsersLinkVisible(), "Admin users link should be visible");
		assertTrue(page.adminGroupsLinkVisible(), "Admin groups link should be visible");
		// Check if content has a page title (check if the class name exists only not the actual title)
		assertTrue(page.adminContentTitleVisible(), "Admin page title should be visible");
	}
}
