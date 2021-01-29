package ca.corefacility.bioinformatics.irida.ria.integration.announcements;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements.*;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test to ensure the Announcement Control page works
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/announcements/AnnouncementPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnnouncementPageIT extends AbstractIridaUIITChromeDriver {

	//Page objects
	private AnnouncementControlPage controlPage;
	private AnnouncementReadPage readPage;
	private AnnouncementDashboardPage dashboardPage;

	@Override
	@Before
	public void setUpTest() {
		LoginPage.loginAsAdmin(driver());
		controlPage = new AnnouncementControlPage(driver());
		readPage = new AnnouncementReadPage(driver());
		dashboardPage = new AnnouncementDashboardPage(driver());
	}

	@Test
	public void testConfirmTablePopulatedByAnnouncements() {
		controlPage.goTo();
		assertEquals("Announcement table should be populated by 6 announcements", 6,
				controlPage.announcementTableSize());
	}

	@Test
	public void testSortAnnouncementsByDate() throws ParseException {
		controlPage.goTo();
		controlPage.clickDateCreatedHeader();

		List<Date> announcementDates = controlPage.getCreatedDates();

		assertTrue("List of announcements is not sorted correctly", checkDatesSortedDescending(announcementDates));

		controlPage.clickDateCreatedHeader();

		announcementDates = controlPage.getCreatedDates();

		assertTrue("List of announcements is not sorted correctly", checkDatesSortedAscending(announcementDates));
	}

    @Test
    public void testSubmitNewAnnouncement() {
		final String title = "Announcement Title";
		final String message = "This is a the announcement message content.";
		final Boolean priority = true;
		controlPage.goTo();

		int numAnnouncementsBefore = controlPage.getCreatedDates().size();
		CreateAnnouncementComponent createAnnouncementComponent = CreateAnnouncementComponent.goTo(driver());
		controlPage.clickCreateNewAnnouncementButton();
		createAnnouncementComponent.enterAnnouncement(title, message, priority);

		// New messages should appear first in the table
		String newTitle = controlPage.getAnnouncementTitle(0);

		assertTrue("Unexpected announcement content.", newTitle.equals(title));
		assertEquals("Unexpected number of announcements visible", numAnnouncementsBefore + 1,
				controlPage.getCreatedDates()
						.size());
	}

    @Test
    public void testCheckDetailsPage() {
        controlPage.goTo();

		String title0 = controlPage.getAnnouncementTitle(0);
		String title1 = controlPage.getAnnouncementTitle(1);
		String title2 = controlPage.getAnnouncementTitle(2);

		EditAnnouncementComponent editAnnouncementComponent = EditAnnouncementComponent.goTo(driver());

		controlPage.gotoEditMessage(0);
		compareMessages(editAnnouncementComponent.getTitle(), title0);
		editAnnouncementComponent.clickCancelButton();

		driver().navigate().refresh();
		controlPage.gotoEditMessage(1);
		compareMessages(editAnnouncementComponent.getTitle(), title1);
		editAnnouncementComponent.clickCancelButton();

		driver().navigate().refresh();
		controlPage.gotoEditMessage(2);
		compareMessages(editAnnouncementComponent.getTitle(), title2);
		editAnnouncementComponent.clickCancelButton();
	}

	private void compareMessages(String announcement, String preview) {
		assertTrue("Announcement preview does not match the message.", announcement.contains(preview));
	}

    @Test
    public void testUpdateAnnouncement() {
		final String newTitle = "Updated Title!!!";
		final String newMessage = "Updated Message Content!!!";
		final boolean newPriority = true;

		controlPage.goTo();
		EditAnnouncementComponent editAnnouncementComponent = EditAnnouncementComponent.goTo(driver());
		controlPage.gotoEditMessage(4);
		editAnnouncementComponent.enterAnnouncement(newTitle, newMessage, newPriority);

		String announcementTitle = controlPage.getAnnouncementTitle(4);
		assertTrue("Unexpected message content", newTitle.contains(announcementTitle));
	}

    @Test
    public void testDeleteAnnouncement() {
        controlPage.goTo();
		List<Date> dates = controlPage.getCreatedDates();
		controlPage.deleteAnnouncement(2);
		assertEquals("Unexpected number of announcements", dates.size() - 1, controlPage.getCreatedDates().size());
	}

    @Test
    public void testAnnouncementUserTablePopulated() {
        controlPage.goTo();
		ViewAnnouncementComponent viewAnnouncementComponent = ViewAnnouncementComponent.goTo(driver());
		controlPage.gotoViewMessage(0);
		assertEquals("Unexpected number of user information rows in table", 6, viewAnnouncementComponent.getTableDataSize());
    }

    @Test
    public void testMarkAnnouncementAsRead() {
        List<WebElement> announcements = dashboardPage.getCurrentUnreadAnnouncements();
        assertEquals("Unexpected number of announcements", 5, announcements.size());

        dashboardPage.markTopAnnouncementAsRead();

        announcements = dashboardPage.getCurrentUnreadAnnouncements();
        assertEquals("Unexpected number of announcements", 4, announcements.size());

        dashboardPage.viewReadAnnouncements();

        List<WebElement> readAnnouncements = readPage.getAllReadAnnouncements();
        assertEquals("Unexpected number of announcements displayed as read", 2, readAnnouncements.size());
    }

    /**
	 * Checks if a List of {@link Date} is sorted in ascending order.
	 *
	 * @param dates
	 * 		List of {@link Date}
	 *
     * @return if the list is sorted ascending
     */
	private boolean checkDatesSortedAscending(List<Date> dates) {
		boolean isSorted = true;
		for (int i = 1; i < dates.size(); i++) {
			if (dates.get(i).compareTo(dates.get(i - 1)) < 0) {
				isSorted = false;
                break;
            }
        }
        return isSorted;
    }

    /**
	 * Checks if a list of {@link Date} is sorted in descending order.
	 *
	 * @param dates
	 * 		List of {@link Date}
	 *
     * @return if the list is sorted ascending
     */
	private boolean checkDatesSortedDescending(List<Date> dates) {
		boolean isSorted = true;
		for (int i = 1; i < dates.size(); i++) {
			if (dates.get(i).compareTo(dates.get(i - 1)) > 0) {
				isSorted = false;
                break;
            }
        }
        return isSorted;
    }
}
