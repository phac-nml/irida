package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.components.Announcements;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.DashboardPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to ensure the Dashboard page works
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/DashboardPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class DashboardPageIT extends AbstractIridaUIITChromeDriver {

	@Override
	@BeforeEach
	public void setUpTest() {
	}

	@Test
	public void testReadingHighPriorityAnnouncements() {
		LoginPage.loginAsUser(driver());
		new DashboardPage(driver());
		Announcements announcements = Announcements.goTo(driver());
		assertTrue(announcements.isModalVisible(), "The priority announcements modal should be visible");
		assertEquals(2, announcements.getTotalUnreadAnnouncements(), "The total unread priority announcements count does not match");
		assertEquals(0, announcements.getTotalReadAnnouncements(), "The total read priority announcements count does not match");
		announcements.getNextAnnouncement();
		assertEquals(1, announcements.getTotalReadAnnouncements(), "The total read priority announcements count does not match");

		assertEquals(5, announcements.getBadgeCount(), "The announcements badge count does not match");

		announcements.getSubmenuAnnouncement();
		assertEquals("No cake", announcements.getSubmenuAnnouncementTitle(2), "The announcements title in the submenu does not match");

	}

	@Test void testRecentActivityAdmin() {
		DashboardPage dashboardPage = DashboardPage.initPage(driver());
		LoginPage.loginAsAdmin(driver());
		assertTrue(dashboardPage.userProjectsRecentActivityTitleDisplayed(), "Admin User recent activity tile should be displayed");
		assertTrue(dashboardPage.allProjectsButtonDisplayed(), "Logged in as admin and viewing their own projects so the All Projects button should be visible");

	}

	@Test void testRecentActivityUser() {
		DashboardPage dashboardPage = DashboardPage.initPage(driver());
		LoginPage.loginAsUser(driver());
		assertTrue(dashboardPage.userProjectsRecentActivityTitleDisplayed(), "User recent activity tile should be displayed");

		assertFalse(dashboardPage.allProjectsButtonDisplayed(), "Logged in as a user so should not be able to view All Projects Button");
		assertFalse(dashboardPage.yourProjectsButtonDisplayed(), "Logged in as a user so should not be able to view Your Projects Button");

	}
}
