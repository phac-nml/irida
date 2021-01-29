package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementRequest;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnnouncementsService;

/**
 * Controller for all ajax requests from the UI for announcements.
 */
@RestController
@RequestMapping("/ajax/announcements")
public class AnnouncementAjaxController {
	private final UIAnnouncementsService UIAnnouncementsService;

	@Autowired
	public AnnouncementAjaxController(UIAnnouncementsService UIAnnouncementsService) {
		this.UIAnnouncementsService = UIAnnouncementsService;
	}

	/**
	 * Handle request for getting a paged list of announcements for an administrator.
	 *
	 * @param tableRequest details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of announcements.
	 */
	@RequestMapping(value = "/control/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public TableResponse<AnnouncementTableModel> getAnnouncementsAdmin(@RequestBody TableRequest tableRequest) {
		return UIAnnouncementsService.getAnnouncementsAdmin(tableRequest);
	}

	/**
	 * Handle request for getting a list of read announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of unread {@link AnnouncementUserJoin}s for a user.
	 */
	@RequestMapping(value = "/user/read")
	@PreAuthorize("hasRole('ROLE_USER')")
	@ResponseBody
	public ResponseEntity<List<AnnouncementUserJoin>> getReadAnnouncementsUser(Principal principal) {
		List<AnnouncementUserJoin> readAnnouncements = UIAnnouncementsService.getReadAnnouncementsUser(principal);
		Collections.sort(readAnnouncements);
		return ResponseEntity.ok(readAnnouncements);
	}

	/**
	 * Handle request for getting a list of unread announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of unread {@link Announcement}s for a user.
	 */
	@RequestMapping(value = "/user/unread")
	@PreAuthorize("hasRole('ROLE_USER')")
	@ResponseBody
	public ResponseEntity<List<Announcement>> getUnreadAnnouncementsUser(Principal principal) {
		List<Announcement> unreadAnnouncements = UIAnnouncementsService.getUnreadAnnouncementsUser(principal);
		Collections.sort(unreadAnnouncements);
		return ResponseEntity.ok(unreadAnnouncements);
	}

	/**
	 * Marks the announcement as read by the current user.
	 *
	 * @param aID ID of the {@link Announcement} to be marked
	 * @param principal the currently logged in user
	 */
	@RequestMapping(value = "/read/{aID}", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_USER')")
	public void markAnnouncementRead(@PathVariable Long aID, Principal principal) {
		UIAnnouncementsService.markAnnouncementAsReadByUser(aID, principal);
	}

	/**
	 * Handles request to create a new announcement
	 *
	 * @param announcementRequest details about the announcement to create.
	 * @param principal           the currently logged in user
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void createNewAnnouncement(@RequestBody AnnouncementRequest announcementRequest, Principal principal) {
		UIAnnouncementsService.createNewAnnouncement(announcementRequest, principal);
	}

	/**
	 * Handles request to update an existing announcement
	 *
	 * @param announcementRequest - the details of the announcement to update.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		UIAnnouncementsService.updateAnnouncement(announcementRequest);
	}

	/**
	 * Handles request to delete an existing announcement.
	 *
	 * @param announcementRequest - the announcement to delete
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		UIAnnouncementsService.deleteAnnouncement(announcementRequest);
	}

	/**
	 * Handles request for getting user read status for current announcement
	 * @param announcementID {@link Long} identifier for the {@link Announcement}
	 * @param tableRequest details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of users.
	 */
	@RequestMapping(value = "/{announcementID}/details/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody
	TableResponse<AnnouncementUserTableModel> getUserAnnouncementInfoTable(
			@PathVariable Long announcementID, @RequestBody TableRequest tableRequest) {
		return UIAnnouncementsService.getUserAnnouncementInfoTable(announcementID, tableRequest);
	}
}

