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
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.RelationshipSesameRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import javax.validation.Validator;
import java.util.Collection;
import java.util.List;

/**
 * A specialized service layer for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectServiceImpl extends CRUDServiceImpl<Identifier, Project> implements ProjectService {


    private RelationshipRepository relationshipRepository;
    private ProjectRepository projectRepository;
    

    public ProjectServiceImpl(ProjectRepository projectRepository, RelationshipRepository relationshipRepository, Validator validator) {
        super(projectRepository, validator, Project.class);
        this.projectRepository = projectRepository;
        this.relationshipRepository = relationshipRepository;
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
    public Relationship addSampleToProject(Project project, Sample sample) {
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
    public Relationship addSequenceFileToProject(Project project, SequenceFile sf) {
        return relationshipRepository.create(project, sf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSequenceFileFromProject(Project project, SequenceFile sf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Project> getProjectsForUser(User user) {
        return projectRepository.getProjectsForUser(user);
    }
}
