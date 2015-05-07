package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadProjectPermission;

/**
 * Interface for interactions with {@link ReferenceFile}.
 * 
 */
public interface ReferenceFileService extends CRUDService<Long, ReferenceFile> {
	/**
	 * Get the collection of {@link ReferenceFile} attached to the specified
	 * {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to get {@link ReferenceFile}s for.
	 * @return the collection of {@link ReferenceFile} attached to the
	 *         {@link Project}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, '" + ReadProjectPermission.PERMISSION_PROVIDED
			+ "')")
	public List<Join<Project, ReferenceFile>> getReferenceFilesForProject(Project project);
}
