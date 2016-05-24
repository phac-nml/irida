package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.User;

import java.util.List;

/**
 *
 */
public interface AnnouncementService extends CRUDService<Long, Announcement> {

    /**
     *      Mark an {@link Announcement} object as read by a {@link User}
     * @param announcement
     *
     * @return An {@link AnnouncementUserJoin} object representing the relationship between
     *          the announcement and the user
     */
    public Join<Announcement, User> markAnnouncementAsReadByUser(Announcement announcement);

    /**
     *      Mark an {@link Announcement} as unread by a {@link User}
     * @param announcement
     * @param user
     */
    public void markAnnouncementAsUnreadByUser(Announcement announcement, User user);

    /**
     *      Get all of the {@link Join}s describing users that have comfirmed they've
     *      read a particular {@link Announcement}
     * @param announcement
     *          The {@link Announcement} for which we want to load users that have read it
     * @return List of {@link User}s that have read the announcement
     */
    public List<Join<Announcement,User>> getConfirmedReadUsersforAnnouncement(Announcement announcement);

    /**
     *      Get a list of all of the {@link User}s that have not confirmed they've read
     *      the {@link Announcement}
     * @param announcement
     * @return List of {@link User}s that haven't confirmed they've read the announcement
     */
    public List<User> getUnreadUsersForAnnouncement(Announcement announcement);

}
