/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.*;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import javax.validation.Validator;
import java.util.Collection;

/**
 * A specialized service layer for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectServiceImpl extends CRUDServiceImpl<Identifier, Project> implements ProjectService {

    private CRUDRepository<UserIdentifier, User> userRepository;
    private CRUDRepository<Identifier, Sample> sampleRepository;
    private ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, CRUDRepository<UserIdentifier, User> userRepository, CRUDRepository<Identifier, Sample> sampleRepository, Validator validator) {
        super(projectRepository, validator, Project.class);
        this.projectRepository = projectRepository;
        this.sampleRepository = sampleRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUserToProject(Project project, User user, Role role) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship addSampleToProject(Project project, Sample sample) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSampleFromProject(Project project, Sample sample) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship addSequenceFileToProject(Project project, SequenceFile sf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Project> getProjectsForUser(User user) {
        return projectRepository.getProjectsForUser(user);
    }
}
