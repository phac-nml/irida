package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Interface for storing and retrieving {@link MetadataRestriction}
 */
public interface MetadataRestrictionRepository extends IridaJpaRepository<MetadataRestriction, Long> {

	/**
	 * Get the {@link MetadataRestriction} for a given {@link Project} and {@link MetadataTemplateField}.  If no restriction is defined, this will return null.
	 *
	 * @param project the {@link Project} to get the restriction for.
	 * @param field   the {@link MetadataTemplateField} to get a restriction for.
	 * @return The requested {@link MetadataRestriction}
	 */
	@Query("FROM MetadataRestriction r WHERE r.project=?1 AND r.field=?2")
	public MetadataRestriction getRestrictionForFieldAndProject(Project project, MetadataTemplateField field);

	/**
	 * List all {@link MetadataRestriction} for a given {@link Project}
	 *
	 * @param project the {@link Project} to get restrictions for
	 * @return a list of {@link MetadataRestriction}
	 */
	@Query("FROM MetadataRestriction r WHERE r.project=?1")
	public List<MetadataRestriction> getRestrictionForProject(Project project);
}
