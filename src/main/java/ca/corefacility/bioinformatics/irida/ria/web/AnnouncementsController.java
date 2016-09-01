package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnnouncementSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesUtils;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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
     * @throws IOException
     */
    @RequestMapping(value = "/{announcementID}/details", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAnnouncementDetailsPage(@PathVariable long announcementID, Model model) throws IOException {
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
     * Get all announcements to be displayed in a datatable for admin control centre
     *
     * @param criteria
     *                  Criteria/options for the datatable when rendering table items/rows
     *
     * @return A map containing all of the data to be displayed in the datatables
     *
     */
    @RequestMapping(value = "/control/ajax/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody DatatablesResponse<Announcement> getAnnouncementsAdmin(
            final @DatatablesParams DatatablesCriterias criteria) {
        final int currentPage = DatatablesUtils.getCurrentPage(criteria);
        final Map<String, Object> sortProperties = DatatablesUtils.getSortProperties(criteria);
        final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
        String sortName = sortProperties.get("sort_string").toString();
        sortName = sortName.replaceAll("announcement.", "");
        if (sortName.equals("identifier")) {
            sortName = "id";
        }
        if (sortName.equals("createdById")) {
            sortName = "user";
        }

        final String searchString = criteria.getSearch();
        final Page<Announcement> announcements = announcementService.search(AnnouncementSpecification
                .searchAnnouncement(searchString), currentPage, criteria.getLength(), direction, sortName);
        final List<Announcement> announcementDataTableResponseList = announcements.getContent().stream()
                .collect(Collectors.toList());

        final DataSet<Announcement> announcementDataSet = new DataSet<>(announcementDataTableResponseList,
                announcements.getTotalElements(), announcements.getTotalElements());

        logger.trace("Total number of announcements: " + announcementDataSet.getTotalRecords());
        return DatatablesResponse.build(announcementDataSet, criteria);
    }

    /**
     * Get user read status for current announcement
     *
     * @param announcementID
     *              The announcement we want read status/information about
     * @param criterias
     *              Criteria/options for the datatable when rendering
     * @return A map of objects containing user and announcement read information
     */
    @RequestMapping(value = "/{announcementID}/details/ajax/list", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody DatatablesResponse<AnnouncementUserDataTableResponse> getUserAnnouncementInfoTable(
            @PathVariable Long announcementID,
            final @DatatablesParams DatatablesCriterias criterias) {

        final Announcement currentAnnouncement = announcementService.read(announcementID);

        final int currentPage = DatatablesUtils.getCurrentPage(criterias);
        final Map<String, Object> sortProperties = DatatablesUtils.getSortProperties(criterias);
        final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
        String sortName = sortProperties.get("sort_string").toString();
        sortName = sortName.replaceAll("announcement.", "");
        if (sortName.equals("user")) {
            sortName = "username";
        }

        final String searchString = criterias.getSearch();
        final Page<User> users = userService.search(UserSpecification.searchUser(searchString), currentPage,
                criterias.getLength(), direction, sortName);
        final List<AnnouncementUserDataTableResponse> announcementUserDataTableResponses = users.getContent().stream()
                .map(user -> new AnnouncementUserDataTableResponse(user.getUsername(), userHasRead(user, currentAnnouncement)))
                .collect(Collectors.toList());

        final DataSet<AnnouncementUserDataTableResponse> announcementUserDataSet = new DataSet<>(announcementUserDataTableResponses,
                users.getTotalElements(), users.getTotalElements());

        return DatatablesResponse.build(announcementUserDataSet, criterias);
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
        if (currentAnnouncement.isPresent()) {
            return currentAnnouncement.get();
        } else {
            return null;
        }
    }

    /**
     * Utility/Container class for returning information about {@link Announcement}s and {@link User}s and their read statuses
     */
    private static final class AnnouncementUserDataTableResponse implements Comparable<AnnouncementUserDataTableResponse> {
        private final String username;
        private final AnnouncementUserJoin join;
        private final Date createdDate;
        private final boolean hasRead;

        public AnnouncementUserDataTableResponse(final String username, final AnnouncementUserJoin join) {
            this.username = username;
            this.join = join;
            if (join != null) {
                createdDate = join.getCreatedDate();
                hasRead = true;
            } else {
                createdDate = new Date(0);
                hasRead = false;
            }
        }

        /**
         * Comparator method to compare dates for each read receipt
         * @param response
         *      The object to compare to
         * @return
         *      -1 if this object is newer than {@param response}
         *      0 if they have the same date
         *      1 if {@param repsonse} is newer than this object
         */
        public int compareTo(AnnouncementUserDataTableResponse response) {
            return this.createdDate.compareTo(response.createdDate);
        }

        @SuppressWarnings("unused")
        public String getUsername() {
            return this.username;
        }

        @SuppressWarnings("unused")
        public AnnouncementUserJoin getJoin() {
            return this.join;
        }

        @SuppressWarnings("unused")
        public Date getCreatedDate() {
            return this.createdDate;
        }

        @SuppressWarnings("unused")
        public boolean getHasRead() {
            return this.hasRead;
        }

    }
}
