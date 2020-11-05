package ca.corefacility.bioinformatics.irida.ria.integration.admin;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.admin.AdminPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/admin/AdminPageView.xml")
public class AdminPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void accessPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		AdminPage page = AdminPage.initPage(driver());
		assertTrue("Admin button should be displayed", page.adminPanelButtonVisible());
		page.clickAdminButton();
		assertTrue("Admin can navigate to admin panel, admin page title should be present", page.comparePageTitle("Users"));
		// Navigate to user groups page
		page.clickUsersSubMenu();
		page.clickGroupsLink();
		assertTrue("Admin can navigate to groups page, groups page title should be present", page.comparePageTitle("Groups"));
		assertTrue("Create New User Group button should be present", page.adminCreateGroupVisible());
		// Navigate to clients page
		page.clickClientsLink();
		assertTrue("Admin can navigate to clients page, clients page title should be present", page.comparePageTitle("Clients"));
		assertTrue("Add Client button should be present", page.adminAddClientVisible());
		// Navigate to remote api page
		page.clickRemoteApiLink();
		assertTrue("Admin can navigate to remote api page, remote api page title should be present", page.comparePageTitle("Remote IRIDA Connections"));
		assertTrue("Add Remote Connection button should be present", page.adminAddRemoteApiVisible());
		// Navigate to sequencing runs page
		page.clickSequencingRunsLink();
		assertTrue("Admin can navigate to sequencing runs page, sequencing runs page title should be present", page.comparePageTitle("Sequencing Runs"));
		// Navigate to ncbi exports page
		page.clickNcbiExportsLink();
		assertTrue("Admin can navigate to ncbi exports page, ncbi exports page title should be present", page.comparePageTitle("NCBI Exports"));
		// Navigate to announcements page
		page.clickAnnouncementsLink();
		assertTrue("Admin can navigate to announcements page, announcements page title should be present", page.comparePageTitle("Announcements"));
		assertTrue("Add Announcement button should be present", page.adminAddAnnouncementVisible());
		// Navigate back to users page
		page.clickUsersSubMenu();
		page.clickUsersLink();
		assertTrue("Admin can navigate to users page, user page title should be present", page.comparePageTitle("Users"));
		assertTrue("Add User button should be present", page.adminAddUserVisible());
	}

	@Test
	public void accessPageFailure() {
		LoginPage.loginAsUser(driver());
		AdminPage page = AdminPage.initPage(driver());
		assertFalse("Admin button should not be displayed", page.adminPanelButtonVisible());
		// No admin button, so attempt to go to admin page by modifying the URL
		page.goToAdminPage(driver());
		assertFalse("User cannot navigate to admin panel, admin page title should not be present", page.comparePageTitle("Users"));
	}

	@Test
	public void testPageSetUp() {
		LoginPage.loginAsAdmin(driver());
		AdminPage page = AdminPage.initPage(driver());
		page.clickAdminButton();
		// Check that side menu is visible
		assertTrue("Admin side menu should be visible", page.adminSideMenuVisible());
		// Check that all top level links are visible
		assertTrue("Admin clients link should be visible", page.adminClientsLinkVisible());
		assertTrue("Admin remote api link should be visible", page.adminRemoteApiLinkVisible());
		assertTrue("Admin sequencing runs link should be visible", page.adminSequencingRunsLinkVisible());
		assertTrue("Admin ncbi exports link should be visible", page.adminNcbiExportsLinkVisible());
		assertTrue("Admin announcements link should be visible", page.adminAnnouncementsLinkVisible());
		// Open sub menu to view other links
		page.clickUsersSubMenu();
		assertTrue("Admin users link should be visible", page.adminUsersLinkVisible());
		assertTrue("Admin groups link should be visible", page.adminGroupsLinkVisible());
		// Check if content has a page title (check if the class name exists only not the actual title)
		assertTrue("Admin page title should be visible", page.adminContentTitleVisible());
	}
}
