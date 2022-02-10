package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserReadDetails;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnnouncementSpecification;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Integration tests for testing out Announcements
 */
@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/AnnouncementServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")

public class AnnouncementServiceImplIT {

	@Autowired
	private AnnouncementService announcementService;
	@Autowired
	private UserService userService;

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateAnnouncementAsAdmin() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		Announcement an = new Announcement("This is a message title", "This is a message", false, user);
		try {
			announcementService.create(an);
		} catch (AccessDeniedException e) {
			fail("Admin should be able to create a new announcement.");
		} catch (Exception e) {
			fail("Failed for unknown reason, stack trace follows: ");
			e.printStackTrace();
		}

	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testCreateAnnouncementNotAdmin() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());

		assertThrows(AccessDeniedException.class, () -> {
			announcementService.create(new Announcement("This is a message title", "This is a message", false, user));
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteAnnouncementAsAdminSuccess() {
		try {
			announcementService.delete(1L);
		} catch (EntityNotFoundException e) {
			fail("Admin trying to delete announcement that doesn't exist.");
		}
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testDeleteAnnouncementAsUserFail() {
		assertThrows(AccessDeniedException.class, () -> {
			announcementService.delete(1L);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteAnnouncementNotExists() {
		assertThrows(EntityNotFoundException.class, () -> {
			announcementService.delete(100L);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testUpdateAnnouncementAsAdminSuccess() {
		Announcement announcement = announcementService.read(1L);
		final String newMessage = "A new message";
		announcement.setMessage(newMessage);
		announcement = announcementService.update(announcement);

		assertEquals(newMessage, announcement.getMessage(), "Message content doesn't match");
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testUpdateAnnouncementAsUserFail() {
		final Announcement a = announcementService.read(1L);
		assertThrows(AccessDeniedException.class, () -> {
			announcementService.update(a);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testUpdateAnnouncementNotExists() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		final Announcement a = new Announcement("Doesn't exist", "Doesn't exist", false, user);
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			announcementService.update(a);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetSingleAnnouncementById() {
		Announcement a = announcementService.read(3L);
		Long idExpected = 3L;
		String messageExpected = "You cannot have your cake and eat it too.";

		assertEquals(idExpected, a.getId(), "IDs for announcement doesn't match");
		assertEquals(messageExpected, a.getMessage(), "Announcement message content doesn't match expected");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetAnnouncementNotExist() {
		assertThrows(EntityNotFoundException.class, () -> {
			announcementService.read(800L);
		});
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testSearchReturnsExistingAnnouncement() {
		String searchString = "Downtime";
		Page<Announcement> searchAnnouncement = announcementService.search(
				AnnouncementSpecification.searchAnnouncement(searchString),
				PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")));
		assertEquals(2, searchAnnouncement.getContent().size(), "Unexpected number of announcements returned");
		for (Announcement a : searchAnnouncement) {
			assertTrue(a.getMessage().contains(searchString));
		}
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testSearchReturnsNoResults() {
		String searchString = "ThisShouldn'tMatchAnything!!";
		Page<Announcement> searchAnnouncement = announcementService.search(
				AnnouncementSpecification.searchAnnouncement(searchString),
				PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id")));
		assertEquals(0, searchAnnouncement.getContent().size(), "Unexpected number of announcements returned");
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testSearchNullSearchString() {
		Page<Announcement> searchAnnouncement = announcementService.search(
				AnnouncementSpecification.searchAnnouncement(null),
				PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")));
		assertEquals(0, searchAnnouncement.getContent().size(), "Unexpected number of announcements returned");
	}

	@Test
	@WithMockUser(username = "user3", roles = "USER")
	public void testUserMarkAnnouncementAsReadSuccess() {
		final Announcement a = announcementService.read(2L);
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		try {
			announcementService.markAnnouncementAsReadByUser(a, user);
		} catch (AccessDeniedException e) {
			fail("User should be able able to mark announcement as read.");
		} catch (EntityExistsException e) {
			fail("Failed for unknown reason, stack trace follows:");
			e.printStackTrace();
		}

	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testUserMarkAnnouncementAsReadFailed() {
		final Announcement a = announcementService.read(1L);
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		assertThrows(EntityExistsException.class, () -> {
			announcementService.markAnnouncementAsReadByUser(a, user);
		});
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testMarkAnnouncementAsUnreadSuccess() {
		final Announcement a = announcementService.read(1L);
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		announcementService.markAnnouncementAsUnreadByUser(a, user);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testMarkAnnouncementAsUnreadForOtherUser() {
		final Announcement a1 = announcementService.read(1L);
		final Announcement a2 = announcementService.read(2L);

		final User user = userService.getUserByUsername("user");

		// for checking whether announcements or users have been incorrectly
		// deleted from the database

		final int numUsersBefore = Lists.newArrayList(userService.findAll()).size();
		final int numAnnsBefore = announcementService.getAllAnnouncements().size();

		announcementService.markAnnouncementAsUnreadByUser(a1, user);
		announcementService.markAnnouncementAsUnreadByUser(a2, user);

		final int numUsersAfter = Lists.newArrayList(userService.findAll()).size();
		final int numAnnsAfter = announcementService.getAllAnnouncements().size();

		assertEquals(numUsersBefore, numUsersAfter, "User was incorrectly modified/deleted");
		assertEquals(numAnnsBefore, numAnnsAfter, "Announcement was incorrectly modified/deleted");

	}

	@Test
	@WithMockUser(username = "user3", roles = "USER")
	public void testMarkAnnouncementAsUnreadFailed() {
		final Announcement a = announcementService.read(1L);
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		assertThrows(EntityNotFoundException.class, () -> {
			announcementService.markAnnouncementAsUnreadByUser(a, user);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetReadUsersForAnnouncement() {
		List<AnnouncementUserJoin> list = announcementService.getReadUsersForAnnouncement(announcementService.read(1L));
		assertEquals(4, list.size(), "Number of read users was unexpected");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUnreadUsersForAnnouncement() {
		List<User> list = announcementService.getUnreadUsersForAnnouncement(announcementService.read(1L));
		assertEquals(2, list.size(), "Number of unread users was unexpected");
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testAnnouncementsForUser() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());

		List<AnnouncementUserReadDetails> list = announcementService.getAnnouncementsForUser(user);
		assertEquals(6, list.size(), "Number of read and unread announcements doesn't match expected value");

		Long readListCount = list.stream().filter(a -> a.isRead()).count();
		assertEquals(5L, (long) readListCount, "Number of unread announcements doesn't match expected value");
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testGetReadAnnouncementsForUser() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		List<AnnouncementUserJoin> readList = announcementService.getReadAnnouncementsForUser(user);

		assertEquals(5, readList.size(), "Number of read announcements doesn't match expected value");
	}

	@Test
	@WithMockUser(username = "user3", roles = "USER")
	public void testGetUnreadAnnouncementsForUser() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		List<Announcement> announcementList = announcementService.getUnreadAnnouncementsForUser(user);

		assertEquals(6, announcementList.size(), "Number of unread announcements doesn't match expected value");

		Announcement ann = announcementService.read(6L);
		announcementService.markAnnouncementAsReadByUser(ann, user);
		announcementList = announcementService.getUnreadAnnouncementsForUser(user);

		assertEquals(5, announcementList.size(), "Number of unread announcements doesn't match expected value");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetAllAnnouncements() {
		List<Announcement> announcementList = announcementService.getAllAnnouncements();
		assertEquals(6, announcementList.size(), "Unexpected total number of announcements, ");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetAnnouncementsCreatedByAdmin() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User user = userService.getUserByUsername(auth.getName());
		List<Announcement> announcements = announcementService.getAnnouncementsCreatedByUser(user);

		for (Announcement a : announcements) {
			assertEquals(user, a.getUser(), "Announcement was not created by the selected user");
		}

		int beforeSize = announcements.size();

		announcementService.create(new Announcement("First message", "The newest announcement", false, user));
		announcementService.create(new Announcement("Second message", "No, this is the newest one!", false, user));

		assertEquals(beforeSize + 2, announcementService.getAnnouncementsCreatedByUser(user).size(),
				"Number of announcements doesn't match");

		announcementService.delete(1L);

		assertEquals(beforeSize + 1, announcementService.getAnnouncementsCreatedByUser(user).size(),
				"Number of announcements doesn't match");

		announcementService.create(new Announcement("Third message", "Someone else made me do it!", false,
				userService.getUserByUsername("admin2")));

		announcements = announcementService.getAnnouncementsCreatedByUser(user);

		for (Announcement a : announcements) {
			assertEquals(user, a.getUser(), "Announcement was not created by the selected user");
		}
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetReadCountsForSingleAnnouncements() {
		Long count1 = announcementService.countReadsForOneAnnouncement(announcementService.read(1L));
		Long count2 = announcementService.countReadsForOneAnnouncement(announcementService.read(2L));
		Long count3 = announcementService.countReadsForOneAnnouncement(announcementService.read(3L));
		Long count4 = announcementService.countReadsForOneAnnouncement(announcementService.read(4L));
		Long count5 = announcementService.countReadsForOneAnnouncement(announcementService.read(5L));
		Long count6 = announcementService.countReadsForOneAnnouncement(announcementService.read(6L));

		assertEquals(4L, (long) count1, "Number of reads for announcement doesn't match");
		assertEquals(1L, (long) count2, "Number of reads for announcement doesn't match");
		assertEquals(1L, (long) count3, "Number of reads for announcement doesn't match");
		assertEquals(1L, (long) count4, "Number of reads for announcement doesn't match");
		assertEquals(1L, (long) count5, "Number of reads for announcement doesn't match");
		assertEquals(0, (long) count6, "Number of reads for announcement doesn't match");

		announcementService.markAnnouncementAsReadByUser(announcementService.read(6L), userService.read(1L));
		announcementService.markAnnouncementAsReadByUser(announcementService.read(6L), userService.read(2L));
		announcementService.markAnnouncementAsReadByUser(announcementService.read(6L), userService.read(3L));

		Long newCount = announcementService.countReadsForOneAnnouncement(announcementService.read(6L));

		assertEquals(3L, (long) newCount, "Number of reads for announcement doesn't match");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCountReadUsersForAnnouncements() {
		Map<Announcement, Long> counts = announcementService.countReadsForAllAnnouncements();

		Set<Announcement> announcements = counts.keySet();

		String failMessage = "Announcement count doesn't match";

		for (Announcement a : announcements) {
			Long id = a.getId();
			if (id == 1) {
				assertEquals(Long.valueOf(4), counts.get(a), failMessage);
			} else if (id >= 2 && id <= 5) {
				assertEquals(Long.valueOf(1), counts.get(a), failMessage);
			} else if (id == 6) {
				assertEquals(Long.valueOf(0), counts.get(a), failMessage);
			} else {
				fail("Error in counting, this announcement shouldn't be counted");
			}
		}
	}
}
