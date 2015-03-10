package ca.corefacility.bioinformatics.irida.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadProjectPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.ReadReferenceFilePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.UpdateReferenceFilePermission;

/**
 * Interface for interactions with {@link ReferenceFile}.
 * 
 */
public interface ReferenceFileService extends CRUDService<Long, ReferenceFile> {

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, '" + ReadReferenceFilePermission.PERMISSION_PROVIDED
			+ "')")
	public ReferenceFile read(Long id) throws EntityNotFoundException;

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

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, '" + UpdateReferenceFilePermission.PERMISSION_PROVIDED
			+ "')")
	public ReferenceFile update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException;

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, '" + UpdateReferenceFilePermission.PERMISSION_PROVIDED
			+ "')")
	public void delete(Long id);
}
