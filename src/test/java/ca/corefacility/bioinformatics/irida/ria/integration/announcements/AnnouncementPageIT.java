package ca.corefacility.bioinformatics.irida.ria.integration.announcements;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements.AnnouncementControlPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements.CreateAnnouncementComponent;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements.EditAnnouncementComponent;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements.ViewAnnouncementComponent;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test to ensure the Announcement Control page works
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/announcements/AnnouncementPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnnouncementPageIT extends AbstractIridaUIITChromeDriver {

	// Page objects
	private AnnouncementControlPage controlPage;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsAdmin(driver());
		controlPage = new AnnouncementControlPage(driver());
	}

	@Test
	public void testConfirmTablePopulatedByAnnouncements() {
		controlPage.goTo();
		assertEquals(6, controlPage.announcementTableSize(),
				"Announcement table should be populated by 6 announcements");
	}

	@Test
	public void testSortAnnouncementsByDate() throws ParseException {
		controlPage.goTo();
		controlPage.clickDateCreatedHeader();

		List<Date> announcementDates = controlPage.getCreatedDates();

		assertTrue(checkDatesSortedDescending(announcementDates), "List of announcements is not sorted correctly");

		controlPage.clickDateCreatedHeader();

		announcementDates = controlPage.getCreatedDates();

		assertTrue(checkDatesSortedAscending(announcementDates), "List of announcements is not sorted correctly");
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

		assertTrue(newTitle.equals(title), "Unexpected announcement content.");
		assertEquals(numAnnouncementsBefore + 1, controlPage.getCreatedDates().size(),
				"Unexpected number of announcements visible");
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
		assertTrue(announcement.contains(preview), "Announcement preview does not match the message.");
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
		assertEquals(newTitle, announcementTitle, "Unexpected message content");
	}

	@Test
	public void testDeleteAnnouncement() {
		controlPage.goTo();
		List<Date> dates = controlPage.getCreatedDates();
		controlPage.deleteAnnouncement(2);
		assertEquals(dates.size() - 1, controlPage.getCreatedDates().size(), "Unexpected number of announcements");
	}

	@Test
	public void testAnnouncementUserTablePopulated() {
		controlPage.goTo();
		ViewAnnouncementComponent viewAnnouncementComponent = ViewAnnouncementComponent.goTo(driver());
		controlPage.gotoViewMessage(0);
		assertEquals(6, viewAnnouncementComponent.getTableDataSize(),
				"Unexpected number of user information rows in table");
	}

	/**
	 * Checks if a List of {@link Date} is sorted in ascending order.
	 *
	 * @param dates List of {@link Date}
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
	 * @param dates List of {@link Date}
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
