package ca.corefacility.bioinformatics.irida.ria.web;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnnouncementSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTAnnouncementAdmin;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTAnnouncementUser;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 *  Controller for handling {@link ca.corefacility.bioinformatics.irida.model.announcements.Announcement} views
 */
@Controller
@RequestMapping(value = "/announcements")
public class AnnouncementsController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementsController.class);

    private static final String ANNOUNCEMENT_VIEW = "announcements/announcements";
    private static final String ANNOUNCEMENT_VIEW_READ = "announcements/read";
    private static final String ANNOUNCEMENT_ADMIN = "announcements/control";
    private static final String ANNOUNCEMENT_CREATE = "announcements/create";
    private static final String ANNOUNCEMENT_DETAILS = "announcements/details";

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
    @RequestMapping(value = "/user/read", method = RequestMethod.GET)
    public String getReadAnnouncementsAsUser(final Model model, Principal principal) {

        User user = userService.getUserByUsername(principal.getName());
        List<AnnouncementUserJoin> readAnnouncements = announcementService.getReadAnnouncementsForUser(user);

        logger.trace("Announcements list size: " + readAnnouncements.size());

        model.addAttribute("readAnnouncements", readAnnouncements);

        return ANNOUNCEMENT_VIEW_READ;
    }

    /**
     * Gets a list of Announcements that the current user hasn't read yet
     *
     * @param model
     *              Model for the view
     * @param principal
     *              The current user
     * @return the fragment for viewing announcements in the dashboard
     */
    @RequestMapping(value = "/user/unread")
    public String getUnreadAnnouncementsForUser(final Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());

        List<Announcement> unreadAnnouncements = announcementService.getUnreadAnnouncementsForUser(user);

        Collections.sort(unreadAnnouncements,Collections.reverseOrder());

        model.addAttribute("announcements", unreadAnnouncements);

        return ANNOUNCEMENT_VIEW;
    }

    /**
     *  Marks the announcement as read by the current user
     *
     * @param aID
     *          ID of the {@link Announcement} to be marked
     * @param principal
     *          The current user
     * @return The fragment for viewing announcements in the dashboard
     */
    @RequestMapping(value = "/read/{aID}", method = RequestMethod.POST)
    public String markAnnouncementRead(@PathVariable Long aID, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        Announcement announcement = announcementService.read(aID);

        announcementService.markAnnouncementAsReadByUser(announcement, user);

        return ANNOUNCEMENT_VIEW;
    }

    /**
     * Get the admin-accessible announcement control page
     *
     * @param model
     *              The model for the returned view
     * @return The announcement control page
     */
    @RequestMapping(value = "/admin")
    public String getControlCentreAdminPage(final Model model) {
        List<Announcement> announcements = announcementService.getAllAnnouncements();

        logger.trace("Announcements list size: " + announcements.size());

        model.addAttribute("announcements", announcements);

        return ANNOUNCEMENT_ADMIN;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getCreateAnnouncementPage() {

        return ANNOUNCEMENT_CREATE;
    }

    /**
     * Create a new announcement
     *
     * @param message
     *              The content of the message for the announcement
     * @param model
     *              The model for the view
     * @param principal
     *              The currently logged in user creating the announcement
     * @return A redirect to the announcement control centre page
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String submitCreateAnnouncement(@RequestParam(required = true) String message,
                                   Model model,
                                   Principal principal) {
        User creator = userService.getUserByUsername(principal.getName());
        Announcement a = new Announcement(message, creator);

        try {
            announcementService.create(a);
        } catch (Exception e) {
            model.addAttribute("errors", "Announcement was not created successfully");
        }

        return "redirect:/announcements/admin";
    }

    /**
     * Updates an existing announcement object in the database
     *
     * @param announcementID
     *                  The ID of the announcement to be updated
     * @param message
     *                  The content of the updated announcement
     * @param model
     *                  The model for the view
     * @return A redirect to the announcement control center page
     */
    @RequestMapping(value = "/{announcementID}/details", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String submitUpdatedAnnouncement(@PathVariable long announcementID,
                                            @RequestParam(required = false) String message,
                                            Model model) {
        Announcement announcement = announcementService.read(announcementID);
        announcement.setMessage(message);

        try {
            announcementService.update(announcement);
        } catch (Exception e) {
            model.addAttribute("errors", "Announcement was not updated successfully");
            logger.error("Announcement could not be updated or saved to the database.", e.getMessage());
        }
        return "redirect:/announcements/admin";
    }

    /**
     * Delete an announcement from the database
     *
     * @param model
     *                  The model for the view
     * @param announcementID
     *                  The ID of the announcement to be deleted
     * @return A redirect to the announcement control center page
     */
    @RequestMapping(value = "/{announcementID}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteAnnouncement(final Model model,
                                     @PathVariable long announcementID) {

        try {
            announcementService.delete(announcementID);
        } catch (Exception e) {
            model.addAttribute("errors", "Announcement could not be deleted");
            logger.error(e.getMessage());
        }

        return "redirect:/announcements/admin";
    }

    /**
     * Get the details page for a particular announcement
     *
     * @param announcementID
     *                  The announcement whose data will be displayed
     * @param model
     *                  The model for the view
     * @return Returns the detail page for the announcement
     */
    @RequestMapping(value = "/{announcementID}/details", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAnnouncementDetailsPage(@PathVariable long announcementID, Model model) {
        Announcement announcement = announcementService.read(announcementID);

        long numberOfReads = announcementService.countReadsForOneAnnouncement(announcement);
        long totalUsers = userService.count();

        logger.trace("Announcement " + announcement.getId() + ": " +
            announcement.getMessage());

        model.addAttribute("announcement", announcement);
        model.addAttribute("numReads", numberOfReads);
        model.addAttribute("numTotal", totalUsers);

        return ANNOUNCEMENT_DETAILS;
    }

    /**
     * Get all announcements to be displayed in a DataTables for admin control centre
     *
     * @param params
     * 		{@link DataTablesParams} for the current DataTable.
     *
     * @return {@link DataTablesResponse}
     */
    @RequestMapping(value = "/control/ajax/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody DataTablesResponse getAnnouncementsAdmin(@DataTablesRequest DataTablesParams params) {
        final Page<Announcement> page = announcementService
                .search(AnnouncementSpecification.searchAnnouncement(params.getSearchValue()),
                        new PageRequest(params.getCurrentPage(), params.getLength(), params.getSort()));

        final List<DataTablesResponseModel> announcements = page.getContent().stream().map(DTAnnouncementAdmin::new)
                .collect(Collectors.toList());
        return new DataTablesResponse(params, page, announcements);
    }

    /**
     * Get user read status for current announcement
     * @param announcementID {@link Long} identifier for the {@link Announcement}
     * @param params {@link DataTablesParams} parameters for current DataTable
     * @return {@link DataTablesResponse} containing the list of users.
     */
    @RequestMapping(value = "/{announcementID}/details/ajax/list", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody DataTablesResponse getUserAnnouncementInfoTable(
            @PathVariable Long announcementID,
            final @DataTablesRequest DataTablesParams params) {

        final Announcement currentAnnouncement = announcementService.read(announcementID);

        final Page<User> page = userService.search(UserSpecification.searchUser(params.getSearchValue()), new PageRequest(params.getCurrentPage(), params.getLength(), params.getSort()));
        final List<DataTablesResponseModel> announcementUsers = page.getContent().stream()
                .map(user -> new DTAnnouncementUser(user, userHasRead(user, currentAnnouncement)))
                .collect(Collectors.toList());

        return new DataTablesResponse(params, page, announcementUsers);
    }

    /**
     * Utility method for checking whether the {@link Announcement} has been read by the {@link User}
     *
     * @param user
     *          The user we want to check
     * @param announcement
     *          The announcement we want to check.
     * @return {@link AnnouncementUserJoin} representing that the user has read the announcement, or null
     *              if the user hasn't read the announcement.
     */
    private AnnouncementUserJoin userHasRead(final User user, final Announcement announcement) {
        final List<AnnouncementUserJoin> readUsers = announcementService.getReadUsersForAnnouncement(announcement);
        final Optional<AnnouncementUserJoin> currentAnnouncement = readUsers.stream()
                .filter(j -> j.getObject().equals(user)).findAny();
        return currentAnnouncement.orElse(null);
    }
}
