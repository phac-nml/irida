package ca.corefacility.bioinformatics.irida.ria.integration;

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

	@Test
	public void testReadingHighPriorityAnnouncements() {
		LoginPage.loginAsUser(driver());
		new DashboardPage(driver());
		Announcements announcements = Announcements.goTo(driver());
		assertTrue(announcements.isModalVisible(), "The priority announcements modal should be visible");
		assertEquals(2, announcements.getTotalUnreadAnnouncements(),
				"The total unread priority announcements count does not match");
		assertEquals(0, announcements.getTotalReadAnnouncements(),
				"The total read priority announcements count does not match");
		announcements.getNextAnnouncement();
		assertEquals(1, announcements.getTotalReadAnnouncements(),
				"The total read priority announcements count does not match");
		assertEquals(5, announcements.getBadgeCount(), "The announcements badge count does not match");

	}

	@Test
	void testRecentActivityAdmin() {
		DashboardPage dashboardPage = DashboardPage.initPage(driver());
		LoginPage.loginAsAdmin(driver());

		Announcements announcements = Announcements.goTo(driver());
		assertTrue(announcements.isModalVisible(), "The priority announcements modal should be visible");
		assertEquals(2, announcements.getTotalUnreadAnnouncements(),
				"The total unread priority announcements count does not match");
		assertEquals(0, announcements.getTotalReadAnnouncements(),
				"The total read priority announcements count does not match");
		announcements.getNextAnnouncement();
		assertEquals(1, announcements.getTotalReadAnnouncements(),
				"The total read priority announcements count does not match");
		announcements.clickCloseButton();

		assertEquals(4, dashboardPage.getStatsForType(0), "Number of Projects stats should display 4");
		assertEquals(4, dashboardPage.getStatsForType(1), "Number of Samples stats should display 4");
		assertEquals(0, dashboardPage.getStatsForType(2), "Number of Analyses stats should display 0");

		assertTrue(dashboardPage.userProjectsRecentActivityTitleDisplayed(),
				"Admin user projects recent activity tile should be displayed");
		assertTrue(dashboardPage.allProjectsButtonDisplayed(),
				"Logged in as admin and viewing their own projects so the All Projects button should be visible");

		assertEquals("Loaded 5 / 5", dashboardPage.getTotalLoadedActivitiesText(),
				"Five of five activities should be loaded for users projects");
		assertTrue(dashboardPage.isLoadMoreButtonDisabled(),
				"Load more button should be disabled as all activities are loaded for admin user's projects");

		dashboardPage.clickAllProjectsButton();
		assertTrue(dashboardPage.adminAllProjectsRecentActivityTitleDisplayed(),
				"Admin all projects recent activity tile should be displayed");
		assertEquals("Loaded 6 / 6", dashboardPage.getTotalLoadedActivitiesText(),
				"Six of six activities should be loaded for all projects");
		assertTrue(dashboardPage.isLoadMoreButtonDisabled(),
				"Load more button should be disabled as all activities are loaded for all projects");

		dashboardPage.clickYourProjectsButton();
		assertTrue(dashboardPage.userProjectsRecentActivityTitleDisplayed(),
				"Admin user projects recent activity tile should be displayed");
		assertEquals("Loaded 5 / 5", dashboardPage.getTotalLoadedActivitiesText(),
				"Five of five activities should be loaded for users projects");
		assertTrue(dashboardPage.isLoadMoreButtonDisabled(),
				"Load more button should be disabled as all activities are loaded for admin user's projects");
	}

	@Test
	void testRecentActivityUser() {
		DashboardPage dashboardPage = DashboardPage.initPage(driver());
		LoginPage.loginAsUser(driver());

		Announcements announcements = Announcements.goTo(driver());
		assertTrue(announcements.isModalVisible(), "The priority announcements modal should be visible");
		assertEquals(2, announcements.getTotalUnreadAnnouncements(),
				"The total unread priority announcements count does not match");
		assertEquals(0, announcements.getTotalReadAnnouncements(),
				"The total read priority announcements count does not match");
		announcements.getNextAnnouncement();
		assertEquals(1, announcements.getTotalReadAnnouncements(),
				"The total read priority announcements count does not match");
		announcements.clickCloseButton();

		assertEquals(3, dashboardPage.getStatsForType(0), "Number of Projects stats should display 3");
		assertEquals(3, dashboardPage.getStatsForType(1), "Number of Samples stats should display 3");
		assertEquals(0, dashboardPage.getStatsForType(2), "Number of Analyses stats should display 0");

		assertTrue(dashboardPage.userProjectsRecentActivityTitleDisplayed(),
				"User recent activity tile should be displayed");
		assertFalse(dashboardPage.allProjectsButtonDisplayed(),
				"Logged in as a user so should not be able to view All Projects Button");
		assertFalse(dashboardPage.yourProjectsButtonDisplayed(),
				"Logged in as a user so should not be able to view Your Projects Button");
		assertEquals("Loaded 4 / 4", dashboardPage.getTotalLoadedActivitiesText(),
				"Four of four activities should be loaded for users projects");
		assertTrue(dashboardPage.isLoadMoreButtonDisabled(),
				"Load more button should be disabled as all activities are loaded for users projects");
	}
}
