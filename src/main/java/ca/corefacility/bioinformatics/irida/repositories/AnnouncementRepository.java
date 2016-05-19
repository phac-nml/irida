package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for managing {@link Announcement}s
 *
 */
public interface AnnouncementRepository extends PagingAndSortingRepository<Announcement, Long> {

    /**
     * Get a collection of all created {@link Announcement}s
     *
     * @return
     *      List of all {@link Announcement}s
     */
    @Query("select j from Announcement j")
    public List<Announcement> getAllAnnouncements();


    /**
     * Get all announcements created by a specific {@link User}
     * @param user
     *          The admin {@link User} that created the {@link Announcement}s
     * @return
     *      List of {@link Announcement}s created by the {@link User}
     */
    @Query("select j from Announcement j where j.user = ?1")
    public List<Announcement> getAnnouncementsByCreator(User user);
}
