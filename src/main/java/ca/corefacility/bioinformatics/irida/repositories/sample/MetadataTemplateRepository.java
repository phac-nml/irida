package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing and reading {@link MetadataTemplate}s
 */
public interface MetadataTemplateRepository extends IridaJpaRepository<MetadataTemplate, Long> {

	/**
	 * Get all the {@link MetadataTemplate}s for a given {@link Project}
	 *
	 * @param project the project to get templates for
	 * @return a list of {@link MetadataTemplate}
	 */
	@Query("FROM MetadataTemplate m WHERE m.project=?1")
	public List<MetadataTemplate> getMetadataTemplatesForProject(Project project);
}
