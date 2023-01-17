package ca.corefacility.bioinformatics.irida.web.controller.test.unit.announcements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.web.announcements.AnnouncementsController;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AnnouncementsController}
 */
public class AnnouncementsControllerTest {

	private static final String ANNOUNCEMENT_LIST = "announcements/list";

	private AnnouncementsController announcementsController;

	@BeforeEach
	public void setUp() {
		announcementsController = new AnnouncementsController();
	}

	@Test
	public void testGetAnnouncementsAsUser() {
		String page = announcementsController.getAnnouncementsPage();
		assertTrue(ANNOUNCEMENT_LIST.equals(page), "Unexpected page returned");
	}

}
