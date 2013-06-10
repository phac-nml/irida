/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.*;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A specialized service layer for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectServiceImpl extends CRUDServiceImpl<Identifier, Project> implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private RelationshipRepository relationshipRepository;
    private ProjectRepository projectRepository;
    private CRUDRepository<Identifier, Sample> sampleRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, RelationshipRepository relationshipRepository,
                              CRUDRepository<Identifier, Sample> sampleRepository, Validator validator) {
        super(projectRepository, validator, Project.class);
        this.projectRepository = projectRepository;
        this.relationshipRepository = relationshipRepository;
        this.sampleRepository = sampleRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship addUserToProject(Project project, User user, Role role) {
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
    public Relationship addSampleToProject(Project project, Sample sample) {
        logger.trace("Adding sample to project.");
        // the sample hasn't been persisted before, persist it before calling the relationshipRepository.
        if (sample.getIdentifier() == null) {
            logger.trace("Going to validate and persist sample prior to creating relationship.");
            // validate the sample, then persist it:
            Set<ConstraintViolation<Sample>> constraintViolations = validator.validate(sample);
            if (constraintViolations.isEmpty()) {
                sample = sampleRepository.create(sample);
            } else {
                throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
            }
        }
        return relationshipRepository.create(project, sample);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSampleFromProject(Project project, Sample sample) {
        relationshipRepository.delete(project, sample);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSequenceFileFromProject(Project project, SequenceFile sf) {
        relationshipRepository.delete(project, sf);
    }

    @Override
    public Collection<Project> getProjectsForUser(User user) {
        return projectRepository.getProjectsForUser(user);
    }
}
