package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Custom repository methods for retrieving {@link MetadataEntry}s
 */
public interface MetadataEntryRepositoryCustom {

	/**
	 * Get all the {@link MetadataEntry} for a given project.  This will return a Map of the Sample IDs associated with a Set of {@link MetadataEntry}
	 *
	 * @param project the {@link Project} to get metadata for
	 * @return The Map of the Project's metadata
	 */
	Map<Long, Set<MetadataEntry>> getMetadataForProject(Project project);
}
