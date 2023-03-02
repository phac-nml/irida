package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.BaseController;

/**
 * Controller for handling {@link ca.corefacility.bioinformatics.irida.model.announcements.Announcement} views
 */
@Controller
@RequestMapping(value = "/announcements")
public class AnnouncementsController extends BaseController {

	/**
	 * Request for the page to display a list of read and unread announcements for user.
	 *
	 * @return The name of the page.
	 */
	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public String getAnnouncementsPage() {
		return "announcements/list";
	}
}
