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
		announcements.waitForModal();
		assertTrue("The priority announcements modal should be visible", announcements.isModalVisible());
		assertEquals("The total unread priority announcements count does not match", 2, announcements.getTotalUnreadAnnouncements());
		assertEquals("The total read priority announcements count does not match", 0, announcements.getTotalReadAnnouncements());
		announcements.clickNextButton();
		announcements.waitForPreviousButton();
		assertEquals("The total read priority announcements count does not match", 1, announcements.getTotalReadAnnouncements());
		announcements.clickCloseButton();
	}

	@Test
	public void testMainNavigationAnnouncementsBadgeCount() {
		Announcements announcements = Announcements.goTo(driver());
		announcements.waitForModal();
		announcements.closeModal();
		assertEquals("The announcements badge count does not match", 6, announcements.getBadgeCount());
	}

	@Test
	public void testMainNavigationAnnouncementsSubmenu() {
		Announcements announcements = Announcements.goTo(driver());
		announcements.waitForModal();
		announcements.closeModal();
		announcements.hoverOverBadge();
		announcements.waitForSubmenu();
		assertEquals("The announcements title in the submenu does not match", "No cake", announcements.getSubmenuAnnouncementTitle(3));
	}

}
