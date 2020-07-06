package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnnouncementSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementRequest;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTAnnouncementUser;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnnouncementsService;
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
	 * @param params {@link DataTablesParams} parameters for current DataTable
	 * @return {@link DataTablesResponse} containing the list of users.
	 */
	@RequestMapping(value = "/{announcementID}/details/list", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody
	DataTablesResponse getUserAnnouncementInfoTable(
			@PathVariable Long announcementID,
			final @DataTablesRequest DataTablesParams params) {
		return UIAnnouncementsService.getUserAnnouncementInfoTable(announcementID, params);
	}
}

