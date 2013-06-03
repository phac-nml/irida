package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.*;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * A specialized service layer for projects.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Transactional
public interface ProjectService extends CRUDService<Identifier, Project> {

    /**
     * Add the specified user to the {@link Project} with a role. If the user is a
     * manager for the {@link Project}, then the user should be added to the {@link Project}
     * with the 'ROLE_MANAGER' role.
     *
     * @param project the {@link Project} to add the user to.
     * @param user    the user to add to the {@link Project}.
     * @param role    the role that the user plays on the {@link Project}.
     */
    public void addUserToProject(Project project, User user, Role role);

    /**
     * Add the specified {@link Sample} to the {@link Project}.
     *
     * @param project the {@link Project} to add the {@link Sample} to.
     * @param sample  the {@link Sample} to add to the {@link Project}. If the {@link Sample} has not
     *                previously been persisted, the service will persist the {@link Sample}.
     * @return a reference to the relationship resource created between the two entities.
     */
    public Relationship addSampleToProject(Project project, Sample sample);

    /**
     * Remove the specified {@link Sample} from the {@link Project}. The {@link Sample} will also be deleted from the
     * system because {@link Sample}s cannot exist outside of a {@link Project}.
     *
     * @param project the {@link Project} to remove the {@link Sample} from.
     * @param sample  the {@link Sample} to remove.
     */
    public void removeSampleFromProject(Project project, Sample sample);

    /**
     * Add the specified {@link SequenceFile} to the {@link Project}.
     *
     * @param project the {@link Project} to add the {@link SequenceFile} to.
     * @param sf      the {@link SequenceFile} to add to the {@link Project}. If the {@link SequenceFile} has not
     *                previously been persisted, then the service will persist the {@link SequenceFile}.
     * @return a reference to the relationship resource created between the two entities.
     */
    public Relationship addSequenceFileToProject(Project project, SequenceFile sf);

    /**
     * Remove the specified {@link SequenceFile} from the {@link Project}. If the {@link SequenceFile} is not associated
     * with any other {@link Project}s, then as a side-effect, the {@link SequenceFile} will be deleted from the system.
     *
     * @param project the {@link Project} from which to remove the file.
     * @param sf      the {@link SequenceFile} to remove.
     */
    public void removeSequenceFileFromProject(Project project, SequenceFile sf);

    /**
     * Get all {@link Project}s associated with a particular {@link User}.
     *
     * @param user the user to get projects for.
     * @return the projects associated with the user.
     */
    public Collection<Project> getProjectsForUser(User user);
}
