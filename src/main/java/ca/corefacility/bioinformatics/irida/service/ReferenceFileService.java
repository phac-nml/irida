package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;

/**
 * Interface for interactions with {@link ReferenceFile}.
 * 
 */
public interface
ReferenceFileService extends CRUDService<Long, ReferenceFile> {
	/**
	 * Get the collection of {@link ReferenceFile} attached to the specified
	 * {@link Project}.
	 * 
	 * @param project
	 *            the {@link Project} to get {@link ReferenceFile}s for.
	 * @return the collection of {@link ReferenceFile} attached to the
	 *         {@link Project}.
	 */
	List<Join<Project, ReferenceFile>> getReferenceFilesForProject(Project project);
}
