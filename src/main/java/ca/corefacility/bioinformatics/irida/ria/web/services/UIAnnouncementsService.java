package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
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
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * A utility class for formatting responses for the admin announcements page UI.
 */
@Component
public class UIAnnouncementsService {
	private final AnnouncementService announcementService;
	private final UserService userService;

	@Autowired
	public UIAnnouncementsService(AnnouncementService announcementService, UserService userService) {
		this.announcementService = announcementService;
		this.userService = userService;
	}

	/**
	 * Returns a paged list of announcements for an administrator.
	 *
	 * @param tableRequest details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of announcements.
	 */
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
	public void deleteAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		announcementService.delete(announcementRequest.getId());
	}

	/**
	 * Get user read status for current announcement
	 * @param announcementID {@link Long} identifier for the {@link Announcement}
	 * @param params {@link DataTablesParams} parameters for current DataTable
	 * @return {@link DataTablesResponse} containing the list of users.
	 */
	public @ResponseBody
	DataTablesResponse getUserAnnouncementInfoTable(
			@PathVariable Long announcementID,
			final @DataTablesRequest DataTablesParams params) {

		final Announcement currentAnnouncement = announcementService.read(announcementID);

		final Page<User> page = userService.search(
				UserSpecification.searchUser(params.getSearchValue()), PageRequest.of(params.getCurrentPage(), params.getLength(), params.getSort()));
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
