package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.User;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface AnnouncementService extends CRUDService<Long, Announcement> {

	/**
	 * Mark an {@link Announcement} object as read by a {@link User}
	 *
	 * @param announcement The announcement to mark
	 * @param user the user who's read the announcement
	 * @return An {@link AnnouncementUserJoin} object representing the relationship between
	 * the announcement and the user
	 */
	public AnnouncementUserJoin markAnnouncementAsReadByUser(Announcement announcement, User user);

	/**
	 * Mark an {@link Announcement} as unread by a {@link User}
	 *
	 * @param announcement the announcement to mark
	 * @param user the user to mark the announcement for
	 */
	public void markAnnouncementAsUnreadByUser(Announcement announcement, User user);

	/**
	 * Get all of the {@link Join}s describing users that have comfirmed they've
	 * read a particular {@link Announcement}
	 *
	 * @param announcement The {@link Announcement} for which we want to load users that have read it
	 * @return List of {@link User}s that have read the announcement
	 */
	public List<AnnouncementUserJoin> getReadUsersForAnnouncement(Announcement announcement);

	/**
	 * Get a list of all of the {@link User}s that have not confirmed they've read
	 * the {@link Announcement}
	 *
	 * @param announcement The
	 * @return List of {@link User}s that haven't confirmed they've read the announcement
	 */
	public List<User> getUnreadUsersForAnnouncement(Announcement announcement);

	/**
	 * Get a list of {@link Announcement}s that have been read by {@link User}
	 *
	 * @param user {@link User} for whom we want to get unread announcements
	 * @return list of {@link Join} objects representing announcements marked as read by a user
	 */
	public List<AnnouncementUserJoin> getReadAnnouncementsForUser(User user);

	/**
	 * Get a list of {@link Announcement}s that have not been read by {@link User}
	 *
	 * @param user {@link User} for whom we want to get unread announcements
	 * @return List of {@link Announcement}s that have not been read by the user
	 */
	public List<Announcement> getUnreadAnnouncementsForUser(User user);

	/**
	 * Get a list of all of the {@link Announcement}s that currently exist
	 *
	 * @return List of {@link Announcement}s
	 */
	public List<Announcement> getAllAnnouncements();

	/**
	 * Get a list of {@link Announcement}s created by specific admin {@link User}
	 *
	 * @param user The admin {@link User} who created the announcements
	 * @return List of announcements created by the admin, empty list if {@link User} is not an admin
	 */
	public List<Announcement> getAnnouncementsCreatedByUser(User user);

	/**
	 * Get a count of the number of {@link User}s who have read {@link Announcement}
	 *
	 * @param announcement {@link Announcement} for which we want the number of users who have read it
	 * @return number of users who have read {@link Announcement}
	 */
	public Long countReadsForOneAnnouncement(Announcement announcement);

	/**
	 * Get a map where the keys are {@link Announcement}s and the values are {@link Integer}s
	 * representing the number of {@link User}s who have read that announcement.
	 *
	 * @return A Map of announcements and counts of users who have read that announcement
	 */
	public Map<Announcement, Long> countReadsForAllAnnouncements();
}
