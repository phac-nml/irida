package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 *  Controller for handling {@link ca.corefacility.bioinformatics.irida.model.announcements.Announcement} views
 */
@Controller
public class AnnouncementsController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementsController.class);

    public static final String ANNOUNCEMENT_VIEW = "announcements/announcements";
    public static final String ANNOUNCEMENT_ADMIN_VIEW = "announcements/announcement_control";

    private final UserService userService;
    private final AnnouncementService announcementService;
    private final MessageSource messageSource;

    @Autowired
    public AnnouncementsController(UserService userService,
                                   AnnouncementService announcementService,
                                   MessageSource messageSource) {
        this.userService = userService;
        this.announcementService = announcementService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/announcements/announcements", method = RequestMethod.GET)
    public String getAllAnnouncementsAsUser(@PathVariable("userId") Long userId,
                                            final Model model, Principal principal) {

        User user = userService.getUserByUsername(principal.getName());
        List<Join<Announcement, User>> joins = announcementService.getReadAnnouncementsForUser(user);
        List<Announcement> announcements = new ArrayList<>();

        for (Join<Announcement, User> j: joins) {
            announcements.add(j.getSubject());
        }

        logger.debug("Announcements list size: " + announcements.size());

        model.addAttribute("announcements", announcements);

        return ANNOUNCEMENT_VIEW;
    }

    @RequestMapping(value = "announcements/admin")
    public String openControlCentreAdmin(final Model model) {
        List<Announcement> announcements = announcementService.getAllAnnouncements();

        logger.debug("Announcements list size: " + announcements.size());

        model.addAttribute("announcements", announcements);
        return ANNOUNCEMENT_ADMIN_VIEW;
    }


}
