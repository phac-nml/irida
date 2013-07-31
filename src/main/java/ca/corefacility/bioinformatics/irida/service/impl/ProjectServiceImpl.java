package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.*;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A specialized service layer for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectServiceImpl extends CRUDServiceImpl<Long, Project> implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private ProjectRepository projectRepository;
    private CRUDRepository<Long, Sample> sampleRepository;
    private UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              CRUDRepository<Long, Sample> sampleRepository,
                              UserRepository userRepository, Validator validator) {
        super(projectRepository, validator, Project.class);
        this.projectRepository = projectRepository;
        this.sampleRepository = sampleRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Project create(Project p) {
    	Project project = super.create(p);
    	UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	User user = userRepository.getUserByUsername(userDetails.getUsername());
    	addUserToProject(project, user, null);
    	return project;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Join<Project, User> addUserToProject(Project project, User user, Role role) {
        return projectRepository.addUserToProject(project, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUserFromProject(Project project, User user) {
        projectRepository.removeUserFromProject(project, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectSampleJoin addSampleToProject(Project project, Sample sample) {
        logger.trace("Adding sample to project.");
        // the sample hasn't been persisted before, persist it before calling the relationshipRepository.
        if (sample.getId() == null) {
            logger.trace("Going to validate and persist sample prior to creating relationship.");
            // validate the sample, then persist it:
            Set<ConstraintViolation<Sample>> constraintViolations = validator.validate(sample);
            if (constraintViolations.isEmpty()) {
                sample = sampleRepository.create(sample);
            } else {
                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
            }
        }
        
        return projectRepository.addSampleToProject(project, sample);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSampleFromProject(Project project, Sample sample) {
        projectRepository.removeSampleFromProject(project, sample);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
	@Override
    public void removeSequenceFileFromProject(Project project, SequenceFile sf) {
        projectRepository.removeFileFromProject(project, sf);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Join<Project, User>> getProjectsForUser(User user) {
        return new ArrayList<Join<Project, User>>(projectRepository.getProjectsForUser(user));
    }
}
