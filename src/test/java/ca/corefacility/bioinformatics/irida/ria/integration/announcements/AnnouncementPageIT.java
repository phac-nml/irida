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
	private AnnouncementDetailPage detailPage;
	private AnnouncementDashboardPage dashboardPage;

	@Override
	@Before
	public void setUpTest() {
		LoginPage.loginAsAdmin(driver());
		controlPage = new AnnouncementControlPage(driver());
		readPage = new AnnouncementReadPage(driver());
		detailPage = new AnnouncementDetailPage(driver());
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
		final String message = "This is a great announcement";
		controlPage.goTo();

		int numAnnouncementsBefore = controlPage.getCreatedDates()
				.size();
		CreateAnnouncementComponent createAnnouncementComponent = CreateAnnouncementComponent.goTo(driver());
		controlPage.clickCreateNewAnnouncementButton();
		createAnnouncementComponent.enterMessage(message);

		// New messages should appear first in the table
		String newMessage = controlPage.getAnnouncement(0);

		assertTrue("Unexpected announcement content.", newMessage.equals(message));
		assertEquals("Unexpected number of announcements visible", numAnnouncementsBefore + 1,
				controlPage.getCreatedDates()
						.size());
	}

    @Test
    public void testCheckDetailsPage() {
        controlPage.goTo();

		String preview0 = controlPage.getAnnouncement(0);
		String preview1 = controlPage.getAnnouncement(1);
		String preview2 = controlPage.getAnnouncement(2);

		controlPage.gotoMessageDetails(0);
		compareMessages(detailPage.getInputText(), preview0);
		detailPage.clickCancelButton();

		controlPage.gotoMessageDetails(1);
		compareMessages(detailPage.getInputText(), preview1);
		detailPage.clickCancelButton();

		controlPage.gotoMessageDetails(2);
		compareMessages(detailPage.getInputText(), preview2);
		detailPage.clickCancelButton();
	}

	private void compareMessages(String announcement, String preview) {
		assertTrue("Announcement preview does not match the message.", announcement.contains(preview));
	}

    @Test
    public void testUpdateAnnouncement() {
		final String newMessage = "Updated!!!";

		controlPage.goTo();
		controlPage.gotoMessageDetails(4);
		detailPage.enterMessage(newMessage);

		String announcementMessage = controlPage.getAnnouncement(4);
		assertTrue("Unexpected message content", newMessage.contains(announcementMessage));
	}

    @Test
    public void testDeleteAnnouncement() {
        controlPage.goTo();
		List<Date> dates = controlPage.getCreatedDates();

		String messagePreview = controlPage.getAnnouncement(2);

		controlPage.gotoMessageDetails(2);
		assertTrue("Announcement content doesn't match expected", detailPage.getInputText().contains(messagePreview));

        detailPage.clickDeleteButton();

		assertEquals("Unexpected number of announcements", dates.size() - 1, controlPage.getCreatedDates().size());
	}

    @Test
    public void testDetailsTablePopulated() {
        controlPage.goTo();
		controlPage.gotoMessageDetails(0);
		assertEquals("Unexpected number of user information rows in table", 6, detailPage.getTableDataSize());
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
