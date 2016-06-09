package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.AnnouncementRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.announcement.AnnouncementUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Service for managing @{link Announcements}
 *
 */
@Service
public class AnnouncementServiceImpl extends CRUDServiceImpl<Long, Announcement> implements AnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    private AnnouncementRepository announcementRepository;
    private AnnouncementUserJoinRepository announcementUserJoinRepository;
    private UserRepository userRepository;

    @Autowired
    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository,
                                   AnnouncementUserJoinRepository announcementUserJoinRepository,
                                   UserRepository userRepository,
                                   Validator validator) {
        super(announcementRepository, validator, Announcement.class);
        this.announcementRepository = announcementRepository;
        this.announcementUserJoinRepository = announcementUserJoinRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Announcement create(Announcement announcement) {
        announcementRepository.save(announcement);
        return announcement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(final Long id) {
        super.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public Announcement read(Long id) {
        return super.read(id);
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public Join<Announcement, User> markAnnouncementAsReadByUser(Announcement announcement, User user) throws EntityExistsException {
        try {
            final AnnouncementUserJoin auj = new AnnouncementUserJoin(announcement, user);
            return announcementUserJoinRepository.save(auj);
        }
        catch (DataIntegrityViolationException e) {
            throw new EntityExistsException("The user [" + user.getId() + "] has already marked announcement ["
                    + announcement.getId() + "] as read");
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public void markAnnouncementAsUnreadByUser(Announcement announcement, User user) throws EntityNotFoundException {

        try {
            final AnnouncementUserJoin join = announcementUserJoinRepository.getAnnouncementUserJoin(announcement, user);
            Long id = join.getId();
            announcementUserJoinRepository.delete(id);
        } catch (NullPointerException e) {
            throw new EntityNotFoundException("The user [" + user.getId() + "] has not yet read announcement ["
                    + announcement.getId() + "]: cannot mark as unread.");
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Join<Announcement,User>> getReadUsersForAnnouncement(Announcement announcement) throws EntityNotFoundException {
        return announcementUserJoinRepository.getUsersByAnnouncementRead(announcement);
    }


    /**
     *  {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getUnreadUsersForAnnouncement(Announcement announcement) throws EntityNotFoundException {
        return announcementUserJoinRepository.getUsersByAnnouncementUnread(announcement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public List<Join<Announcement, User>> getReadAnnouncementsForUser(User user) {
        return announcementUserJoinRepository.getAnnouncementsReadByUser(user);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public List<Announcement> getUnreadAnnouncementsForUser(User user) {
        return announcementUserJoinRepository.getAnnouncementsUnreadByUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public List<Announcement> getAllAnnouncements() {
        return Lists.newArrayList(announcementRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Announcement> getAnnouncementsCreatedByUser(User user) {
        return announcementRepository.getAnnouncementsByCreator(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Long countReadsForOneAnnouncement(Announcement announcement) {
        return announcementUserJoinRepository.countUsersForAnnouncement(announcement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<Announcement, Long> countReadsForAllAnnouncements() {
        Map<Announcement, Long> counts = new HashMap<>();
        List<Announcement> announcements = getAllAnnouncements();

        for (Announcement a: announcements) {
            counts.put(a, countReadsForOneAnnouncement(a));
        }

        return counts;
    }

}
