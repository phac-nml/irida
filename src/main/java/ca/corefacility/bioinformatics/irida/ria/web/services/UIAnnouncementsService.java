package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserReadDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnnouncementSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementRequest;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * A utility class for formatting responses for the announcements page UI.
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
	public TableResponse<AnnouncementTableModel> getAnnouncementsAdmin(TableRequest tableRequest) {
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
	 * Returns a list of read and unread announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of {@link AnnouncementUserReadDetails} objects representing read and unread announcements for a user.
	 */
	public List<AnnouncementUserReadDetails> getAnnouncementsUser(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		List<AnnouncementUserReadDetails> announcements = announcementService.getAnnouncementsForUser(user);
		Collections.sort(announcements);
		return announcements;
	}

	/**
	 * Returns a list of read announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of unread {@link AnnouncementUserJoin}s for a user.
	 */
	public List<AnnouncementUserJoin> getReadAnnouncementsUser(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		List<AnnouncementUserJoin> readAnnouncements = announcementService.getReadAnnouncementsForUser(user);
		Collections.sort(readAnnouncements);
		return readAnnouncements;
	}

	/**
	 * Returns a list of unread announcements for a user.
	 *
	 * @param principal the currently logged in user
	 * @return a {@link List} of unread {@link Announcement}s for a user.
	 */
	public List<Announcement> getUnreadAnnouncementsUser(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		List<Announcement> unreadAnnouncements = announcementService.getUnreadAnnouncementsForUser(user);
		Collections.sort(unreadAnnouncements);
		return unreadAnnouncements;
	}

	/**
	 * Marks an announcement as read.
	 *
	 * @param aID ID of the {@link Announcement} to be marked
	 * @param principal the currently logged in user
	 */
	public void markAnnouncementAsReadByUser(Long aID, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		Announcement announcement = announcementService.read(aID);
		announcementService.markAnnouncementAsReadByUser(announcement, user);
	}

	/**
	 * Creates a new announcement
	 *
	 * @param announcementRequest details about the announcement to create.
	 * @param principal           the currently logged in user
	 */
	public void createNewAnnouncement(AnnouncementRequest announcementRequest, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		Announcement announcement = new Announcement(announcementRequest.getTitle(), announcementRequest.getMessage(), announcementRequest.getPriority(), user);
		announcementService.create(announcement);
	}

	/**
	 * Update an existing announcement
	 *
	 * @param announcementRequest - the details of the announcement to update.
	 */
	public void updateAnnouncement(AnnouncementRequest announcementRequest) {
		Announcement announcement = announcementService.read(announcementRequest.getId());
		announcement.setTitle(announcementRequest.getTitle());
		announcement.setMessage(announcementRequest.getMessage());
		announcement.setPriority(announcementRequest.getPriority());
		announcementService.update(announcement);
	}

	/**
	 * Delete an existing announcement.
	 *
	 * @param announcementRequest - the announcement to delete
	 */
	public void deleteAnnouncement(AnnouncementRequest announcementRequest) {
		announcementService.delete(announcementRequest.getId());
	}

	/**
	 * Get an announcement.
	 *
	 * @param aID ID of the {@link Announcement}
	 */
	public Announcement getAnnouncement(Long aID) {
		Announcement announcement = announcementService.read(aID);
		return announcement;
	}

	/**
	 * Get user read status for current announcement
	 * @param announcementID {@link Long} identifier for the {@link Announcement}
	 * @param tableRequest details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of users.
	 */
	public TableResponse<AnnouncementUserTableModel> getUserAnnouncementInfoTable(Long announcementID, TableRequest tableRequest) {

		final Announcement currentAnnouncement = announcementService.read(announcementID);

		final Page<User> page = userService.search(
				UserSpecification.searchUser(tableRequest.getSearch()), PageRequest.of(tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort()));
		final List<AnnouncementUserTableModel> announcementUsers = page.getContent().stream()
				.map(user -> new AnnouncementUserTableModel(user, userHasRead(user, currentAnnouncement)))
				.collect(Collectors.toList());

		return new TableResponse<>(announcementUsers, page.getTotalElements());
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
