package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

/**
 *  Controller for handling {@link ca.corefacility.bioinformatics.irida.model.announcements.Announcement} views
 */
@Controller
@RequestMapping("/announcements")
public class AnnouncementsController {

    public static final String ANNOUNCEMENT_VIEW = "/announcements/announcements";
    public static final String ANNOUNCEMENT_ADMIN_VIEW = "/announcements/admin";

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

    @RequestMapping(value = "/announcements", method = RequestMethod.GET)
    public String getUnreadAnnouncements(Model model, Principal principal) {

        List<Announcement> announcements = announcementService.getUnreadAnnouncementsForUser(userService.getUserByUsername(principal.getName()));

        model.addAllAttributes(announcements);

        return ANNOUNCEMENT_VIEW;
    }
}
