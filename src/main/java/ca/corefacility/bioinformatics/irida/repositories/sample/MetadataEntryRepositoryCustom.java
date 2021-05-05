package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

public interface MetadataEntryRepositoryCustom {

	Map<Long, Set<MetadataEntry>> getMetadataForProject(Project project);
}
