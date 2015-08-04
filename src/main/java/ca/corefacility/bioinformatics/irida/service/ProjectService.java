package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A specialized service layer for projects.
 *
 */
public interface ProjectService extends CRUDService<Long, Project> {

	/**
	 * Add the specified {@link User} to the {@link Project} with a {@link Role} . If the {@link User} is a manager for
	 * the {@link Project}, then the {@link User} should be added to the {@link Project} with the 'ROLE_MANAGER' {@link
	 * Role}.
	 *
	 * @param project
	 * 		the {@link Project} to add the user to.
	 * @param user
	 * 		the user to add to the {@link Project}.
	 * @param role
	 * 		the role that the user plays on the {@link Project}.
	 *
	 * @return a reference to the relationship resource created between the two entities.
	 */
	public Join<Project, User> addUserToProject(Project project, User user, ProjectRole role);

	/**
	 * Remove the specified {@link User} from the {@link Project}.
	 *
	 * @param project
	 * 		the {@link Project} to remove the {@link User} from.
	 * @param user
	 * 		the {@link User} to be removed from the {@link Project}.
	 *
	 * @throws ProjectWithoutOwnerException
	 * 		if removing this user would leave the project without an owner
	 */
	public void removeUserFromProject(Project project, User user) throws ProjectWithoutOwnerException;

	/**
	 * Update a {@link User}'s {@link ProjectRole} on a {@link Project}
	 *
	 * @param project
	 * 		The project to update
	 * @param user
	 * 		The user to update
	 * @param projectRole
	 * 		The role to set
	 *
	 * @return The newly updated role object
	 * @throws ProjectWithoutOwnerException
	 * 		If the role change would leave the project without an owner
	 */
	public Join<Project, User> updateUserProjectRole(Project project, User user, ProjectRole projectRole)
			throws ProjectWithoutOwnerException;

	/**
	 * Add the specified {@link Sample} to the {@link Project}.
	 *
	 * @param project
	 * 		the {@link Project} to add the {@link Sample} to.
	 * @param sample
	 * 		the {@link Sample} to add to the {@link Project}. If the {@link Sample} has not previously been persisted, the
	 * 		service will persist the {@link Sample}.
	 *
	 * @return a reference to the relationship resource created between the two entities.
	 */
	public Join<Project, Sample> addSampleToProject(Project project, Sample sample);
	
	
	/**
	 * Move a {@link Sample} from one {@link Project} to another
	 * 
	 * @param source
	 *            the source {@link Project}
	 * @param destination
	 *            Destination {@link Project}
	 * @param sample
	 *            {@link Sample} to be moved
	 * @return Newly created {@link ProjectSampleJoin}
	 */
	public ProjectSampleJoin moveSampleBetweenProjects(Project source, Project destination, Sample sample);

	/**
	 * Remove the specified {@link Sample} from the {@link Project}. The {@link Sample} will also be deleted from the
	 * system because {@link Sample}s cannot exist outside of a {@link Project}.
	 *
	 * @param project
	 * 		the {@link Project} to remove the {@link Sample} from.
	 * @param sample
	 * 		the {@link Sample} to remove.
	 */
	public void removeSampleFromProject(Project project, Sample sample);
	
	/**
	 * Remove a collection of {@link Sample}s from a {@link Project}
	 * 
	 * @param project
	 *            the {@link Project} to remove the {@link Sample}s from.
	 * @param samples
	 *            the {@link Sample}s to remove.
	 */
	public void removeSamplesFromProject(Project project, Iterable<Sample> samples);

	/**
	 * Get all {@link Project}s associated with a particular {@link User}.
	 *
	 * @param user
	 * 		the user to get projects for.
	 *
	 * @return the projects associated with the user.
	 */
	public List<Join<Project, User>> getProjectsForUser(User user);

	/**
	 * Search {@link ProjectUserJoin}s with a given specification and paging parameters
	 *
	 * @param specification
	 * 		The specification to search with
	 * @param page
	 * 		The search page number
	 * @param size
	 * 		The search page size
	 * @param order
	 * 		The search order
	 * @param sortProperties
	 * 		The page sort properties
	 *
	 * @return The matching ProjectUserJoins
	 */
	public Page<ProjectUserJoin> searchProjectUsers(Specification<ProjectUserJoin> specification, int page, int size,
			Direction order, String... sortProperties);

	/**
	 * Check if a {@link User} has a given {@link ProjectRole} on a {@link Project}
	 *
	 * @param user
	 * 		The user to test
	 * @param project
	 * 		The project to test
	 * @param projectRole
	 * 		The project role to test
	 *
	 * @return true/false whether the user has the given role
	 */
	public boolean userHasProjectRole(User user, Project project, ProjectRole projectRole);

	/**
	 * Add a related {@link Project} to the given {@link Project}
	 *
	 * @param subject
	 * 		The parent project
	 * @param relatedProject
	 * 		The project to be added to the parent
	 *
	 * @return a {@link RelatedProjectJoin} describing the relationship
	 */
	public RelatedProjectJoin addRelatedProject(Project subject, Project relatedProject);

	/**
	 * Get all {@link RelatedProjectJoin}s for a given {@link Project}
	 *
	 * @param project
	 * 		The parent project
	 *
	 * @return A list of {@link RelatedProjectJoin}
	 */
	public List<RelatedProjectJoin> getRelatedProjects(Project project);

	/**
	 * Get all {@link RelatedProjectJoin}s where the given Project is the relatedProject property.
	 *
	 * @param project
	 * 		The child project
	 *
	 * @return A list of {@link RelatedProjectJoin}
	 */
	public List<RelatedProjectJoin> getReverseRelatedProjects(Project project);

	/**
	 * Remove a {@link RelatedProjectJoin}
	 *
	 * @param relatedProject
	 * 		The {@link RelatedProjectJoin} to remove
	 */
	public void removeRelatedProject(RelatedProjectJoin relatedProject);

	/**
	 * Remove a {@link RelatedProjectJoin} for the given project and related project
	 *
	 * @param subject
	 * 		the owning project
	 * @param relatedProject
	 * 		The related project
	 */
	public void removeRelatedProject(Project subject, Project relatedProject);

	/**
	 * Get the projects that a given sample is on
	 *
	 * @param sample
	 * 		The sample to get projects for
	 *
	 * @return All the projects a sample exists in
	 */
	public List<Join<Project, Sample>> getProjectsForSample(Sample sample);

	/**
	 * Add a {@link ReferenceFile} to a {@link Project}.
	 *
	 * @param project
	 * 		the {@link Project} to add the {@link ReferenceFile} to.
	 * @param referenceFile
	 * 		the {@link ReferenceFile}.
	 *
	 * @return a {@link Join} representing the relationship between the {@link Project} and {@link ReferenceFile}.
	 */
	public Join<Project, ReferenceFile> addReferenceFileToProject(Project project, ReferenceFile referenceFile);

	/**
	 * Remove a {@link ReferenceFile} from a {@link Project}
	 *
	 * @param project
	 *            the {@link Project} to remove the reference file from.
	 * @param referenceFile
	 *            the {@link ReferenceFile} to remove.
	 */
	public void removeReferenceFileFromProject(Project project, ReferenceFile referenceFile);
}
