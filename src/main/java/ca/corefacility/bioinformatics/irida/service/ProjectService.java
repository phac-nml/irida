package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * A specialized service layer for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface ProjectService extends CRUDService<Long, Project> {

    /**
     * Add the specified {@link User} to the {@link Project} with a {@link Role}. If the {@link User} is a
     * manager for the {@link Project}, then the {@link User} should be added to the {@link Project}
     * with the 'ROLE_MANAGER' {@link Role}.
     *
     * @param project the {@link Project} to add the user to.
     * @param user    the user to add to the {@link Project}.
     * @param role    the role that the user plays on the {@link Project}.
     * @return a reference to the relationship resource created between the two entities.
     */
    public Join<Project, User> addUserToProject(Project project, User user, ProjectRole role);

    /**
     * Remove the specified {@link User} from the {@link Project}.
     *
     * @param project the {@link Project} to remove the {@link User} from.
     * @param user    the {@link User} to be removed from the {@link Project}.
     */
    public void removeUserFromProject(Project project, User user);

    /**
     * Add the specified {@link Sample} to the {@link Project}.
     *
     * @param project the {@link Project} to add the {@link Sample} to.
     * @param sample  the {@link Sample} to add to the {@link Project}. If the {@link Sample} has not
     *                previously been persisted, the service will persist the {@link Sample}.
     * @return a reference to the relationship resource created between the two entities.
     */
    public Join<Project, Sample> addSampleToProject(Project project, Sample sample);

    /**
     * Remove the specified {@link Sample} from the {@link Project}. The {@link Sample} will also be deleted from the
     * system because {@link Sample}s cannot exist outside of a {@link Project}.
     *
     * @param project the {@link Project} to remove the {@link Sample} from.
     * @param sample  the {@link Sample} to remove.
     */
    public void removeSampleFromProject(Project project, Sample sample);

    /**
     * Get all {@link Project}s associated with a particular {@link User}.
     *
     * @param user the user to get projects for.
     * @return the projects associated with the user.
     */
    public List<Join<Project, User>> getProjectsForUser(User user);
}
