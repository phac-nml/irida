package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.components.Announcements;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.DashboardPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test to ensure the Dashboard page works
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/DashboardPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class DashboardPageIT extends AbstractIridaUIITChromeDriver {

	private DashboardPage dashboardPage;

	@Override
	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsUser(driver());
		dashboardPage = new DashboardPage(driver());
	}

	@Test
	public void testReadingHighPriorityAnnouncements() {
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
}
