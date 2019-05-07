package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import static org.mockito.Matchers.any;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.AnnouncementsController;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import org.springframework.ui.ExtendedModelMap;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for {@link AnnouncementsController}
 */
public class AnnouncementsControllerTest {

    //HTML Page names
    private static final String ANNOUNCEMENT_VIEW = "announcements/announcements";
    private static final String ANNOUNCEMENT_VIEW_READ = "announcements/read";
    private static final String ANNOUNCEMENT_ADMIN = "announcements/control";
    private static final String ANNOUNCEMENT_CREATE = "announcements/create";
    private static final String ANNOUNCEMENT_DETAILS = "announcements/details";

    private static final String USER_NAME = "testme";

    List<Announcement> announcementList;
    List<AnnouncementUserJoin> announcementUserList;

    //Services
    private UserService userService;
    private AnnouncementService announcementService;
    private AnnouncementsController announcementsController;

    private User user;

    @Before
    public void setUp() {
        userService = mock(UserService.class);
        announcementService = mock(AnnouncementService.class);
        announcementsController = new AnnouncementsController(userService, announcementService);

        user = new User("testme", "test@me.com", "aaa", "John", "Curatcha", "2222222");

        Announcement a1 = new Announcement("First message", user);
        Announcement a2 = new Announcement("Second message", user);

        announcementList = Lists.newArrayList(a1, a2);

        AnnouncementUserJoin auj1 = new AnnouncementUserJoin(a1, user);
        AnnouncementUserJoin auj2 = new AnnouncementUserJoin(a2, user);

        announcementUserList = Lists.newArrayList(auj1, auj2);
    }

    @SuppressWarnings("rawtypes")
	@Test
    public void testGetReadAnnouncementsAsUser() {
        Principal principal = () -> USER_NAME;
        ExtendedModelMap model = new ExtendedModelMap();

        when(announcementService.getReadAnnouncementsForUser(user)).thenReturn(announcementUserList);
        when(userService.getUserByUsername(USER_NAME)).thenReturn(user);

        String page = announcementsController.getReadAnnouncementsAsUser(model, principal);

        Map<String, Object> modelMap = model.asMap();

        assertEquals("Unexpected number attributes in model", 1, modelMap.keySet().size());
        assertEquals("Unexpected number joins in model", 2, ((List) modelMap.get("readAnnouncements")).size());
        assertTrue("Unexpected page returned", ANNOUNCEMENT_VIEW_READ.equals(page));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetUnreadAnnouncementsForUser() {
        Principal principal = () -> USER_NAME;
        ExtendedModelMap model = new ExtendedModelMap();

        when(announcementService.getUnreadAnnouncementsForUser(user)).thenReturn(announcementList);
        when(userService.getUserByUsername(USER_NAME)).thenReturn(user);

        String page = announcementsController.getUnreadAnnouncementsForUser(model, principal);

        Map<String, Object> modelMap = model.asMap();

        assertEquals("Unexpected number attributes in model", 1, modelMap.keySet().size());
        assertEquals("Unexpected number joins in model", 2, ((List) modelMap.get("announcements")).size());
        assertTrue("Unexpected page returned", ANNOUNCEMENT_VIEW.equals(page));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testGetControlCentreAdminPage() {
        ExtendedModelMap model = new ExtendedModelMap();

        when(announcementService.getAllAnnouncements()).thenReturn(announcementList);

        String page = announcementsController.getControlCentreAdminPage(model);

        Map<String, Object> modelMap = model.asMap();

        assertEquals("Unexpected number attributes in model", 1, modelMap.keySet().size());
        assertEquals("Unexpected number joins in model", 2, ((List) modelMap.get("announcements")).size());
        assertTrue("Unexpected page returned", ANNOUNCEMENT_ADMIN.equals(page));
    }

    @Test
    public void testGetCreateAnnouncementPage() {
        assertTrue("Unexpected page returned", ANNOUNCEMENT_CREATE.equals(announcementsController.getCreateAnnouncementPage()));
    }

    @Test
    public void testSubmitCreateAnnouncement() {
        Principal principal = () -> USER_NAME;
        ExtendedModelMap model = new ExtendedModelMap();

        String message = "The newest message";
        Announcement a = new Announcement(message, user);

        when(announcementService.create(any(Announcement.class))).thenReturn(a);
        when(userService.getUserByUsername(USER_NAME)).thenReturn(user);

        announcementsController.submitCreateAnnouncement(message, model, principal);

        assertTrue("Unexpected page returned", ANNOUNCEMENT_CREATE.equals(announcementsController.getCreateAnnouncementPage()));
        verify(announcementService).create(any(Announcement.class));
        verify(userService, times(1)).getUserByUsername(USER_NAME);
    }

    @Test
    public void testSubmitUpdatedAnnouncement() {
        long id = 1L;
        String message = "Updated message and announcement.";
        ExtendedModelMap model = new ExtendedModelMap();

        Announcement a = new Announcement(message, user);

        when(announcementService.read(any(Long.class))).thenReturn(a);
        when(announcementService.update(any(Announcement.class))).thenReturn(a);

        String page = announcementsController.submitUpdatedAnnouncement(id, message, model);

        assertTrue("Unexpected redirect to page", page.equals("redirect:/announcements/admin"));
        verify(announcementService, times(1)).update(any(Announcement.class));
        verify(announcementService, times(1)).read(any(Long.class));
    }

    @Test
    public void testDeleteAnnouncement() {
        long id = 1L;
        ExtendedModelMap model = new ExtendedModelMap();

        doNothing().when(announcementService).delete(any(Long.class));

        String page = announcementsController.deleteAnnouncement(model, id);

        assertTrue("Unexpected redirect to page", page.equals("redirect:/announcements/admin"));
        verify(announcementService, times(1)).delete(any(Long.class));
    }

    @Test
    public void testGetAnnouncementDetailsPage() {
        long id = 1L;
        ExtendedModelMap model = new ExtendedModelMap();
        String message = "A new announcement";
        String page = null;

        Announcement a = new Announcement(message, user);

        when(announcementService.read(id)).thenReturn(a);
        when(announcementService.countReadsForOneAnnouncement(any(Announcement.class))).thenReturn(0L);
        when(userService.count()).thenReturn(1L);

        page = announcementsController.getAnnouncementDetailsPage(id, model);

        assertTrue("Unexpected redirect to a page", page.equals(ANNOUNCEMENT_DETAILS));
        assertEquals("Unexpected number of users", 1L, (model.get("numTotal")));
        assertEquals("Unexpected number of reads", 0L, (model.get("numReads")));
        assertTrue("Unexpected announcement", message.equals(((Announcement) model.get("announcement")).getMessage()));

        verify(announcementService, times(1)).read(id);
        verify(announcementService).countReadsForOneAnnouncement(a);
        verify(userService).count();
    }
}
