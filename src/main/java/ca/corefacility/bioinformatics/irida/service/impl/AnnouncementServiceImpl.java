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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import javax.validation.Validator;
import java.util.List;

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
     *  {@inheritDoc}
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('AS_USER')")
    public Join<Announcement, User> markAnnouncementAsReadByUser(Announcement announcement, User user) {
        try {
            AnnouncementUserJoin join = announcementUserJoinRepository.save(new AnnouncementUserJoin(announcement, user));
            return join;
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
    @PreAuthorize("hasRole('AS_USER')")
    public void markAnnouncementAsUnreadByUser(Announcement announcement, User user) throws EntityNotFoundException {
        announcementUserJoinRepository.delete(new AnnouncementUserJoin(announcement, user));
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('AS_ADMIN')")
    public List<Join<Announcement,User>> getConfirmedReadUsersforAnnouncement(Announcement announcement) {
        return announcementUserJoinRepository.getUsersByAnnouncementRead(announcement);
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('AS_ADMIN')")
    public List<User> getUnreadUsersForAnnouncement(Announcement announcement) {
        return announcementUserJoinRepository.getUsersByAnnouncementUnread(announcement);
    }

}
