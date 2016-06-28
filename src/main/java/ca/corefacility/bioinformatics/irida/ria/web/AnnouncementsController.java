package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 *  Controller for handling {@link ca.corefacility.bioinformatics.irida.model.announcements.Announcement} views
 */
@Controller
public class AnnouncementsController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementsController.class);

    public static final String ANNOUNCEMENT_PAGE = "announcements/announcements";
    public static final String ANNOUNCEMENT_ADMIN_PAGE = "announcements/control";
    public static final String ANNOUNCEMENT_CREATE_PAGE = "announcements/create";
    public static final String ANNOUNCEMENT_DETAIL_PAGE = "announcements/create";

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

        return ANNOUNCEMENT_PAGE;
    }

    @RequestMapping(value = "/announcements/admin")
    public String openControlCentreAdmin(final Model model) {
        List<Announcement> announcements = announcementService.getAllAnnouncements();

        logger.debug("Announcements list size: " + announcements.size());

        model.addAttribute("announcements", announcements);
        return ANNOUNCEMENT_ADMIN_PAGE;
    }

    @RequestMapping(value = "/announcements/create", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getCreateAnnouncementPage(final Model model) {

        return ANNOUNCEMENT_CREATE_PAGE;
    }

    @RequestMapping(value = "/announcements/create", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String submitCreateAnnouncement(@RequestParam(required = false) String detail,
                                   Model model,
                                   Principal principal) {
        User creator = userService.getUserByUsername(principal.getName());
        Announcement a = new Announcement(detail, creator);

        Announcement announcement;
        try {
            announcement = announcementService.create(a);
        } catch (Exception e) {
            model.addAttribute("errors", "Announcement was not created successfully");
            return ANNOUNCEMENT_ADMIN_PAGE;
        }

        return openControlCentreAdmin(model);
    }

    @RequestMapping(value = "/announcements/{announcementID}/details")
    public String getAnnouncementDetailsPage(@PathVariable long announcementID,
                                             Model model,
                                             Principal principal) throws IOException {
        Announcement announcement = announcementService.read(announcementID);
        User user = userService.getUserByUsername(principal.getName());
        if (user.getSystemRole().equals(Role.ROLE_ADMIN)){
            logger.debug("Here is announcement " + announcement.getId() + ": " +
                announcement.getMessage());
        } else {
            throw new AccessDeniedException("Do not have permissions to edit this announcement");
        }
        return ANNOUNCEMENT_DETAIL_PAGE;
    }

}
