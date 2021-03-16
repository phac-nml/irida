package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.components.Announcements;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.DashboardPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test to ensure the Dashboard page works
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/DashboardPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class DashboardPageIT extends AbstractIridaUIITChromeDriver {

	private DashboardPage dashboardPage;

	@Override
	@Before
	public void setUpTest() {
		LoginPage.loginAsUser(driver());
		dashboardPage = new DashboardPage(driver());
	}

	@Test
	public void testReadingHighPriorityAnnouncements() {
		Announcements announcements = Announcements.goTo(driver());
		assertTrue("The priority announcements modal should be visible", announcements.isModalVisible());
		assertEquals("The total unread priority announcements count does not match", 2, announcements.getTotalUnreadAnnouncements());
		assertEquals("The total read priority announcements count does not match", 0, announcements.getTotalReadAnnouncements());
		announcements.getNextAnnouncement();
		assertEquals("The total read priority announcements count does not match", 1, announcements.getTotalReadAnnouncements());

		assertEquals("The announcements badge count does not match", 5, announcements.getBadgeCount());

		announcements.getSubmenuAnnouncement();
		assertEquals("The announcements title in the submenu does not match", "No cake", announcements.getSubmenuAnnouncementTitle(2));
	}
}
