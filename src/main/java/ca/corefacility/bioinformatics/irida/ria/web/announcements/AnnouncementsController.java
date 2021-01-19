package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller for handling {@link ca.corefacility.bioinformatics.irida.model.announcements.Announcement} views
 */
@Controller
@RequestMapping(value = "/announcements")
public class AnnouncementsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementsController.class);

    private static final String ANNOUNCEMENT_LIST = "announcements/list";

    private final UserService userService;
    private final AnnouncementService announcementService;

    @Autowired
    public AnnouncementsController(UserService userService,
                                   AnnouncementService announcementService) {
        this.userService = userService;
        this.announcementService = announcementService;
    }

    /**
     * Request for the page to display a list of read and unread announcements for user.
     *
     * @return The name of the page.
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public String getAnnouncementsPage() {
        return ANNOUNCEMENT_LIST;
    }
}
