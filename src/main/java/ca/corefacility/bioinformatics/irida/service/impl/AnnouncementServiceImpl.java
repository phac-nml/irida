package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.repositories.AnnouncementRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.announcement.AnnouncementUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Validator;

/**
 *  Service for managing @{link Announcements}
 *
 */
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
}
