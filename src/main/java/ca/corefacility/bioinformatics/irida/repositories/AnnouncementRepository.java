package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for managing {@link Announcement}s
 */
public interface AnnouncementRepository extends IridaJpaRepository<Announcement, Long> {

	/**
	 * Get all announcements created by a specific {@link User}
	 * 
	 * @param user The admin {@link User} that created the {@link Announcement}s
	 * @return List of {@link Announcement}s created by the {@link User}
	 */
	@Query("select j from Announcement j where j.user = ?1")
	public List<Announcement> getAnnouncementsByCreator(User user);
}
