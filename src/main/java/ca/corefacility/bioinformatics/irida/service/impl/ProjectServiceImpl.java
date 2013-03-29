/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import javax.validation.Validator;

/**
 * A specialized service layer for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectServiceImpl extends CRUDServiceImpl<Identifier, Project> implements ProjectService {

    private CRUDRepository<Identifier, User> userRepository;

    public ProjectServiceImpl(CRUDRepository<Identifier, Project> projectRepository, CRUDRepository<Identifier, User> userRepository, Validator validator) {
        super(projectRepository, validator);
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUserToProject(Project project, User user, Role role) {
        // first, add the user to the project:
        project.addUserToProject(user, role);
        repository.update(project);

        // then add the project to the user:
        user.addProject(project, role);
        userRepository.update(user);
    }
}
