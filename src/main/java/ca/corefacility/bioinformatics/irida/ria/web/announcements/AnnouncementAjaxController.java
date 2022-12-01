package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementRequest;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserReadDetails;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnnouncementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller for all ajax requests from the UI for announcements.
 */
@RestController
@RequestMapping("/ajax/announcements")
public class AnnouncementAjaxController {
	private final UIAnnouncementsService service;

	@Autowired
	public AnnouncementAjaxController(UIAnnouncementsService service) {
		this.service = service;
	}

	/**
	 * Handle request for getting a paged list of announcements for an administrator.
	 *
	 * @param tableRequest details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of announcements.
	 */
	@RequestMapping(value = "/control/list")
	public TableResponse<AnnouncementTableModel> getAnnouncementsAdmin(@RequestBody TableRequest tableRequest) {
		return service.getAnnouncementsAdmin(tableRequest);
	}

	/**
	 * Get the total number of unread announcement for a user.
	 *
	 * @return number of unread announcements
	 */
	@GetMapping("/count")
	public int getUnreadAnnouncementCount() {
		return service.getUnreadAnnouncementsCount();
	}

	/**
	 * Handle request for getting a list of read and unread announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of {@link AnnouncementUserReadDetails} objects representing the read and unread announcements for a user.
	 */
	@RequestMapping(value = "/user/list")
	public ResponseEntity<List<AnnouncementUserReadDetails>> getAnnouncementsUser(Principal principal) {
		List<AnnouncementUserReadDetails> announcements = service.getAnnouncementsUser(principal);
		return ResponseEntity.ok(announcements);
	}

	/**
	 * Handle request for getting a list of read announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of unread {@link AnnouncementUserJoin}s for a user.
	 */
	@RequestMapping(value = "/user/read")
	public ResponseEntity<List<AnnouncementUserJoin>> getReadAnnouncementsUser(Principal principal) {
		List<AnnouncementUserJoin> readAnnouncements = service.getReadAnnouncementsUser(principal);
		return ResponseEntity.ok(readAnnouncements);
	}

	/**
	 * Handle request for getting a list of unread announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of unread {@link Announcement}s for a user.
	 */
	@RequestMapping(value = "/user/unread")
	public ResponseEntity<List<Announcement>> getUnreadAnnouncementsUser(Principal principal) {
		List<Announcement> unreadAnnouncements = service.getUnreadAnnouncementsUser(principal);
		return ResponseEntity.ok(unreadAnnouncements);
	}

	/**
	 * Marks the announcement as read by the current user.
	 *
	 * @param aID ID of the {@link Announcement} to be marked
	 * @param principal the currently logged in user
	 */
	@RequestMapping(value = "/read/{aID}", method = RequestMethod.POST)
	public void markAnnouncementRead(@PathVariable Long aID, Principal principal) {
		service.markAnnouncementAsReadByUser(aID, principal);
	}

	/**
	 * Handles request to create a new announcement
	 *
	 * @param announcementRequest details about the announcement to create.
	 * @param principal           the currently logged in user
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void createNewAnnouncement(@RequestBody AnnouncementRequest announcementRequest, Principal principal) {
		service.createNewAnnouncement(announcementRequest, principal);
	}

	/**
	 * Handles request to update an existing announcement
	 *
	 * @param announcementRequest - the details of the announcement to update.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public void updateAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		service.updateAnnouncement(announcementRequest);
	}

	/**
	 * Handles request to delete an existing announcement.
	 *
	 * @param announcementRequest - the announcement to delete
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public void deleteAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		service.deleteAnnouncement(announcementRequest);
	}

	/**
	 * Handles request for getting an announcement.
	 *
	 * @param aID ID of the {@link Announcement}
	 * @return an {@link Announcement}
	 */
	@RequestMapping(value = "/{aID}")
	public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long aID) {
		Announcement announcement = service.getAnnouncement(aID);
		return ResponseEntity.ok(announcement);
	}

	/**
	 * Handles request for getting user read status for current announcement
	 * @param aID {@link Long} identifier for the {@link Announcement}
	 * @param tableRequest details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of users.
	 */
	@RequestMapping(value = "/{aID}/details/list")
	public TableResponse<AnnouncementUserTableModel> getUserAnnouncementInfoTable(@PathVariable Long aID, @RequestBody TableRequest tableRequest) {
		return service.getUserAnnouncementInfoTable(aID, tableRequest);
	}
}
