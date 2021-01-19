package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import java.security.Principal;
import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserReadDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
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
     * Gets a list of {@link Announcement}s for the current {@link User}
     *
     * @param model
     *              Model for the view
     * @param principal
     *              The user fetching the announcements (usually the one currently logged in)
     * @return The announcement page containing announcement information for the user
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public String getAnnouncementsAsUser(final Model model, Principal principal) {

        User user = userService.getUserByUsername(principal.getName());
        List<AnnouncementUserReadDetails> announcements = announcementService.getAnnouncementsForUser(user);

        logger.trace("Announcements list size: " + announcements.size());

        model.addAttribute("announcements", announcements);

        return ANNOUNCEMENT_LIST;
    }
}
