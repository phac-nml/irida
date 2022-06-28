package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Custom repository methods for retrieving {@link MetadataEntry}s
 */
public interface MetadataEntryRepositoryCustom {
	/**
	 * Get all the {@link MetadataEntry} for a set of samples in a given project. This will return a Map of the Sample
	 * IDs associated with a Set of {@link MetadataEntry}
	 *
	 * @param project   the {@link Project} to get metadata for
	 * @param sampleIds the {@link Sample} ids to get metadata for
	 * @param fields    the fields to get metadata from in the project. This must not be empty.
	 * @return The Map of the Project's metadata
	 */
	Map<Long, Set<MetadataEntry>> getMetadataForProjectSamples(Project project, List<Long> sampleIds,
			List<MetadataTemplateField> fields);
}
