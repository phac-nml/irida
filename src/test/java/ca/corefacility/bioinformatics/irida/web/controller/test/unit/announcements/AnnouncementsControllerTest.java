package ca.corefacility.bioinformatics.irida.web.controller.test.unit.announcements;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.ria.web.clients.ClientsController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.AnnouncementsController;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AnnouncementsController}
 */
public class AnnouncementsControllerTest {

    //HTML Page names
    private static final String ANNOUNCEMENT_LIST = "announcements/list";

    //Services
    private UserService userService;
    private AnnouncementService announcementService;
    private AnnouncementsController announcementsController;

    @Before
    public void setUp() {
        userService = mock(UserService.class);
        announcementService = mock(AnnouncementService.class);
        announcementsController = new AnnouncementsController(userService, announcementService);
    }

    @SuppressWarnings("rawtypes")
	@Test
    public void testGetAnnouncementsAsUser() {
        String page = announcementsController.getAnnouncementsPage();
        assertTrue("Unexpected page returned", ANNOUNCEMENT_LIST.equals(page));
    }

}
