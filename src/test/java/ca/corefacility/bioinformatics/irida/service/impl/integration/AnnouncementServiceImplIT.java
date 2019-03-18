package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
        IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class })
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
        Announcement an = new Announcement("This is a message", user);
        try {
            announcementService.create(an);
        } catch (AccessDeniedException e) {
            fail("Admin should be able to create a new announcement.");
        } catch (Exception e) {
            fail("Failed for unknown reason, stack trace follows: ");
            e.printStackTrace();
        }

    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "user", roles = "USER")
    public void testCreateAnnouncementNotAdmin() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.getUserByUsername(auth.getName());
        announcementService.create(new Announcement("This is a message", user));
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

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "user", roles = "USER")
    public void testDeleteAnnouncementAsUserFail() {
        announcementService.delete(1L);
    }

    @Test (expected = EntityNotFoundException.class)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteAnnouncementNotExists() {
        announcementService.delete(100L);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateAnnouncementAsAdminSuccess() {
        Announcement announcement = announcementService.read(1L);
        final String newMessage = "A new message";
        announcement.setMessage(newMessage);
        announcement = announcementService.update(announcement);

        assertEquals("Message content doesn't match", newMessage, announcement.getMessage());
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "user", roles = "USER")
    public void testUpdateAnnouncementAsUserFail() {
        final Announcement a = announcementService.read(1L);
        announcementService.update(a);
    }

    @Test (expected = InvalidDataAccessApiUsageException.class)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateAnnouncementNotExists() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.getUserByUsername(auth.getName());
        final Announcement a = new Announcement("Doesn't exist", user);
        announcementService.update(a);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetSingleAnnouncementById() {
        Announcement a = announcementService.read(3L);
        Long idExpected = 3L;
        String messageExpected = "You cannot have your cake and eat it too.";

        assertEquals("IDs for announcement doesn't match", idExpected, a.getId());
        assertEquals("Announcement message content doesn't match expected", messageExpected, a.getMessage());
    }

    @Test(expected = EntityNotFoundException.class)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAnnouncementNotExist() {
        announcementService.read(800L);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testSearchReturnsExistingAnnouncement() {
        String searchString = "Downtime";
        Page<Announcement> searchAnnouncement = announcementService.search(AnnouncementSpecification.searchAnnouncement(searchString),
				new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "id")));
		assertEquals("Unexpected number of announcements returned", 2, searchAnnouncement.getContent().size());
        for(Announcement a : searchAnnouncement) {
            assertTrue(a.getMessage().contains(searchString));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testSearchReturnsNoResults() {
        String searchString = "ThisShouldn'tMatchAnything!!";
        Page<Announcement> searchAnnouncement = announcementService.search(AnnouncementSpecification.searchAnnouncement(searchString),
				new PageRequest(1, 10, new Sort(Sort.Direction.ASC, "id")));
		assertEquals("Unexpected number of announcements returned", 0, searchAnnouncement.getContent().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testSearchNullSearchString() {
		Page<Announcement> searchAnnouncement = announcementService
				.search(AnnouncementSpecification.searchAnnouncement(null),
						new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "id")));
		assertEquals("Unexpected number of announcements returned", 0, searchAnnouncement.getContent().size());
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

    @Test (expected = EntityExistsException.class)
    @WithMockUser(username = "user", roles = "USER")
    public void testUserMarkAnnouncementAsReadFailed() {
        final Announcement a = announcementService.read(1L);
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.getUserByUsername(auth.getName());
        announcementService.markAnnouncementAsReadByUser(a, user);
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

        //for checking whether announcements or users have been incorrectly deleted from the database

        final int numUsersBefore = Lists.newArrayList(userService.findAll()).size();
        final int numAnnsBefore = announcementService.getAllAnnouncements().size();

        announcementService.markAnnouncementAsUnreadByUser(a1, user);
        announcementService.markAnnouncementAsUnreadByUser(a2, user);

        final int numUsersAfter = Lists.newArrayList(userService.findAll()).size();
        final int numAnnsAfter = announcementService.getAllAnnouncements().size();

        assertEquals("User was incorrectly modified/deleted", numUsersBefore, numUsersAfter);
        assertEquals("Announcement was incorrectly modified/deleted", numAnnsBefore, numAnnsAfter);

    }

    @Test (expected = EntityNotFoundException.class)
    @WithMockUser(username = "user3", roles = "USER")
    public void testMarkAnnouncementAsUnreadFailed() {
        final Announcement a = announcementService.read(1L);
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.getUserByUsername(auth.getName());
        announcementService.markAnnouncementAsUnreadByUser(a, user);
    }

    @Test
    @WithMockUser (username = "admin", roles = "ADMIN")
    public void testGetReadUsersForAnnouncement() {
        List<AnnouncementUserJoin> list = announcementService.getReadUsersForAnnouncement(announcementService.read(1L));
        assertEquals("Number of read users was unexpected", 4, list.size());
    }

    @Test
    @WithMockUser (username = "admin", roles = "ADMIN")
    public void testGetUnreadUsersForAnnouncement() {
        List<User> list = announcementService.getUnreadUsersForAnnouncement(announcementService.read(1L));
        assertEquals("Number of unread users was unexpected", 2, list.size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testGetReadAnnouncementsForUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.getUserByUsername(auth.getName());
        List<AnnouncementUserJoin> readList = announcementService.getReadAnnouncementsForUser(user);

        assertEquals("Number of read announcements doesn't match expected value", 5, readList.size());
    }

    @Test
    @WithMockUser(username = "user3", roles = "USER")
    public void testGetUnreadAnnouncementsForUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.getUserByUsername(auth.getName());
        List<Announcement> announcementList = announcementService.getUnreadAnnouncementsForUser(user);

        assertEquals("Number of unread announcements doesn't match expected value", 6, announcementList.size());

        Announcement ann = announcementService.read(6L);
        announcementService.markAnnouncementAsReadByUser(ann, user);
        announcementList = announcementService.getUnreadAnnouncementsForUser(user);

        assertEquals("Number of unread announcements doesn't match expected value", 5, announcementList.size());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllAnnouncements() {
        List<Announcement> announcementList = announcementService.getAllAnnouncements();
        assertEquals("Unexpected total number of announcements, ", 6, announcementList.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAnnouncementsCreatedByAdmin() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.getUserByUsername(auth.getName());
        List<Announcement> announcements = announcementService.getAnnouncementsCreatedByUser(user);

        for (Announcement a: announcements) {
            assertEquals("Announcement was not created by the selected user", user, a.getUser());
        }

        int beforeSize = announcements.size();

        announcementService.create(new Announcement("The newest announcement", user));
        announcementService.create(new Announcement("No, this is the newest one!", user));

        assertEquals("Number of announcements doesn't match", beforeSize + 2,
                announcementService.getAnnouncementsCreatedByUser(user).size());

        announcementService.delete(1L);

        assertEquals("Number of announcements doesn't match", beforeSize + 1,
                announcementService.getAnnouncementsCreatedByUser(user).size());

        announcementService.create(new Announcement("Someone else made me do it!",
                userService.getUserByUsername("admin2")));

        announcements = announcementService.getAnnouncementsCreatedByUser(user);

        for (Announcement a: announcements) {
            assertEquals("Announcement was not created by the selected user", user, a.getUser());
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

        assertEquals("Number of reads for announcement doesn't match", 4L, (long) count1);
        assertEquals("Number of reads for announcement doesn't match", 1L, (long) count2);
        assertEquals("Number of reads for announcement doesn't match", 1L, (long) count3);
        assertEquals("Number of reads for announcement doesn't match", 1L, (long) count4);
        assertEquals("Number of reads for announcement doesn't match", 1L, (long) count5);
        assertEquals("Number of reads for announcement doesn't match", 0, (long) count6);

        announcementService.markAnnouncementAsReadByUser(announcementService.read(6L), userService.read(1L));
        announcementService.markAnnouncementAsReadByUser(announcementService.read(6L), userService.read(2L));
        announcementService.markAnnouncementAsReadByUser(announcementService.read(6L), userService.read(3L));

        Long newCount = announcementService.countReadsForOneAnnouncement(announcementService.read(6L));

        assertEquals("Number of reads for announcement doesn't match", 3L, (long) newCount);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCountReadUsersForAnnouncements() {
        Map<Announcement, Long> counts = announcementService.countReadsForAllAnnouncements();

        Set<Announcement> announcements = counts.keySet();

        String failMessage = "Announcement count doesn't match";

        for (Announcement a: announcements) {
            Long id = a.getId();
            if (id == 1) {
                assertEquals(failMessage, new Long(4), counts.get(a));
            } else if (id >= 2 && id <= 5) {
                assertEquals(failMessage, new Long(1), counts.get(a));
            } else if (id == 6) {
                assertEquals(failMessage, new Long(0), counts.get(a));
            } else {
                fail("Error in counting, this announcement shouldn't be counted");
            }
        }
    }
}
