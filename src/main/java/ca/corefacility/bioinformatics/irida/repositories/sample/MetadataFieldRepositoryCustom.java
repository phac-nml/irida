package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

import java.util.List;

/**
 * Custom repository methods for getting {@link MetadataTemplateField}s
 */
public interface MetadataFieldRepositoryCustom {
	/**
	 * Get all MetadataTemplateField associated with a {@link Project}
	 *
	 * @param p The project to get fields for
	 * @return a list of fields
	 */
	public List<MetadataTemplateField> getMetadataFieldsForProject(Project p);
}
