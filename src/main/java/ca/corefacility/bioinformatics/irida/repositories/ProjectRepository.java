package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Collection;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;

/**
 * Specialized repository for {@link Project}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {

    /**
     * Get all {@link Project}s associated with a particular {@link User}.
     *
     * @param user the user to get projects for.
     * @return A collection of {@link ProjectUserJoin}s describing the projects associated with the user.
     */
    public Collection<ProjectUserJoin> getProjectsForUser(User user);
	
	/**
	 * Get all {@link Project}s associated with a particular {@link User} where that user has a {@link ProjectRole}.PROJECT_OWNER role on the project.
     * @param user the user to get projects for.
	 * @param role The user's role on the project
     * @return A collection of {@link ProjectUserJoin}s describing the projects associated with the user.
	 */
	public Collection<ProjectUserJoin> getProjectsForUserWithRole(User user,ProjectRole role);

    /**
     * Add a user to a project
     *
     * @param project The project to add the user to
     * @param user    The user to add
     * @return A {@link ProjectUserJoin} object describing the project/user link
     */
    public ProjectUserJoin addUserToProject(Project project, User user, ProjectRole projectRole);

    /**
     * Remove a {@link User} from a {@link Project}.
     *
     * @param project the {@link Project} to remove the {@link User} from.
     * @param user    the {@link User} to remove from the {@link Project}.
     */
    public void removeUserFromProject(Project project, User user);
    
    /**
     * Add a {@link Sample} to a {@link Project}.
     * @param project The {@link Project} to add a {@link Sample} to
     * @param sample The {@link Sample} to add to a {@link Project}
     * @return A {@link ProjectSampleJoin} describing the project/sample link.
     */
    public ProjectSampleJoin addSampleToProject(Project project, Sample sample);
    
    /**
     * Get a collection of the {@link Project}s related to a {@link Sample}
     * @param sample The {@link Sample} to get the projects for
     * @return A collection of {@link ProjectSampleJoin}s describing the project/sample link
     */
    public Collection<ProjectSampleJoin> getProjectForSample(Sample sample);
    
    /**
     * Remove a {@link Sample} from a {@link Project}
     * @param project The {@link Project} to remove from
     * @param sample The {@link Sample} to remove
     */
    public void removeSampleFromProject(Project project, Sample sample) throws EntityNotFoundException;
	
	/**
	 * Check if a {@link User} has a given {@link ProjectRole} on a {@link Project}
	 * @param user The user to test
	 * @param project The project to test
	 * @param role The project role to test
	 * @return true/false whether the user has the given role
	 */
	public boolean userHasProjectRole(User user, Project project, ProjectRole role);
}
