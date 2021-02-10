package ca.corefacility.bioinformatics.irida.web.controller.test.unit.announcements;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.web.announcements.AnnouncementsController;

import static org.junit.Assert.assertTrue;


/**
 * Unit tests for {@link AnnouncementsController}
 */
public class AnnouncementsControllerTest {

    private static final String ANNOUNCEMENT_LIST = "announcements/list";

    private AnnouncementsController announcementsController;

    @Before
    public void setUp() {
        announcementsController = new AnnouncementsController();
    }

	@Test
    public void testGetAnnouncementsAsUser() {
        String page = announcementsController.getAnnouncementsPage();
        assertTrue("Unexpected page returned", ANNOUNCEMENT_LIST.equals(page));
    }

}
