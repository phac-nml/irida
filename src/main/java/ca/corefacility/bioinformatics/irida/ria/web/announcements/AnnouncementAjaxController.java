package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnnouncementSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementRequest;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller for all ajax requests from the UI for announcements.
 */
@RestController
@RequestMapping("/ajax/announcements")
public class AnnouncementAjaxController {
	private final AnnouncementService announcementService;
	private final UserService userService;

	@Autowired
	public AnnouncementAjaxController(AnnouncementService announcementService, UserService userService) {
		this.announcementService = announcementService;
		this.userService = userService;
	}

	/**
	 * Returns a paged list of announcements for an administrator.
	 *
	 * @param tableRequest details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of announcements.
	 */
	@RequestMapping(value = "/control/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public TableResponse<AnnouncementTableModel> getAnnouncementsAdmin(@RequestBody TableRequest tableRequest) {
		final Page<Announcement> page = announcementService.search(
				AnnouncementSpecification.searchAnnouncement(tableRequest.getSearch()),
				PageRequest.of(tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort()));

		final List<AnnouncementTableModel> announcements = page.getContent()
				.stream()
				.map(AnnouncementTableModel::new)
				.collect(Collectors.toList());
		return new TableResponse<>(announcements, page.getTotalElements());
	}

	/**
	 * Creates a new announcement
	 *
	 * @param announcementRequest details about the announcement to create.
	 * @param principal           the currently logged in user
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void createNewAnnouncement(@RequestBody AnnouncementRequest announcementRequest, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		Announcement announcement = new Announcement(announcementRequest.getMessage(), user);
		announcementService.create(announcement);
	}

	/**
	 * Update an existing announcement
	 *
	 * @param announcementRequest - the details of the announcement to update.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		Announcement announcement = announcementService.read(announcementRequest.getId());
		announcement.setMessage(announcementRequest.getMessage());
		announcementService.update(announcement);
	}

	/**
	 * Delete an existing announcement.
	 *
	 * @param announcementRequest - the announcement to delete
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		announcementService.delete(announcementRequest.getId());
	}
}

