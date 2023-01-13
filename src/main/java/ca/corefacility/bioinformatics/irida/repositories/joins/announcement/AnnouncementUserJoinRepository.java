package ca.corefacility.bioinformatics.irida.repositories.joins.announcement;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserReadDetails;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for managing {@link AnnouncementUserJoin}s
 */
public interface AnnouncementUserJoinRepository extends PagingAndSortingRepository<AnnouncementUserJoin, Long>,
		JpaSpecificationExecutor<AnnouncementUserJoin> {

	/**
	 * Get a collection of the {@link Announcement}s related to a {@link User}, meaning the collection of
	 * {@link Announcement}s read and unread by the {@link User}
	 *
	 * @param user The {@link User} to get read and unread announcements for
	 * @return A collection of {@link AnnouncementUserReadDetails}s representing the read and unread announcements
	 *         for a user
	 */
	@Query("select new ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserReadDetails(a.id, a.title, a.priority, a.createdDate, j.createdDate) from Announcement a left join AnnouncementUserJoin j on a.id = j.announcement.id and j.user = ?1")
	public List<AnnouncementUserReadDetails> getAnnouncementsForUser(User user);

	/**
	 * Get a collection of the {@link Announcement}s related to a {@link User}, meaning the collection of
	 * {@link Announcement}s marked as read by the {@link User}
	 *
	 * @param user The {@link User} to get read announcements for
	 * @return A collection of {@link AnnouncementUserJoin}s describing the link between announcement and user
	 */
	@Query("select j from AnnouncementUserJoin j where j.user = ?1")
	public List<AnnouncementUserJoin> getAnnouncementsReadByUser(User user);

	/**
	 * Gets a collection of {@link Announcement}s that have not been marked as read by the {@link User}.
	 *
	 * @param user The {@link User} for which we want to fetch all unread {@link Announcement}s
	 * @return List of {@link Announcement}s unread by the user
	 */
	@Query("select a from Announcement a where a not in "
			+ "(select j.announcement from AnnouncementUserJoin j where j.user = ?1)")
	public List<Announcement> getAnnouncementsUnreadByUser(User user);

	/**
	 * Get a specific {@link AnnouncementUserJoin} by {@link Announcement} and {@link User}
	 *
	 * @param announcement The {@link Announcement}
	 * @param user         The {@link Announcement} to get list of users that have read it
	 * @return {@link AnnouncementUserJoin} described by the announcement/user pair
	 */
	@Query("select j from AnnouncementUserJoin j where j.announcement = ?1 and j.user = ?2")
	public AnnouncementUserJoin getAnnouncementUserJoin(Announcement announcement, User user);

	/**
	 * Get a collection of the {@link User}s that have read an {@link Announcement}
	 *
	 * @param announcement The {@link Announcement} to get list of users that have read it
	 * @return A collection of {@link AnnouncementUserJoin}s describing the link between announcement and user
	 */
	@Query("select j from AnnouncementUserJoin j where j.announcement = ?1")
	public List<AnnouncementUserJoin> getUsersByAnnouncementRead(Announcement announcement);

	/**
	 * Get a collection of the {@link User}s that have not read an {@link Announcement}
	 *
	 * @param announcement The {@link Announcement} to get list of users that have not read it
	 * @return A collection of {@link AnnouncementUserJoin}s describing the link between announcement and user
	 */
	@Query("select u from User u where u not in "
			+ "(select j.user from AnnouncementUserJoin j where j.announcement = ?1)")
	public List<User> getUsersByAnnouncementUnread(Announcement announcement);

	/**
	 * Count how many {@link User}s have read a specific {@link Announcement}
	 *
	 * @param announcement The {@link Announcement} for which we want to count the number of users
	 * @return count of {@link User}s that have read the announcement
	 */
	@Query("select count (j.id) from AnnouncementUserJoin j where j.announcement = ?1")
	public Long countUsersForAnnouncement(Announcement announcement);
}
