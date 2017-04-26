package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing and reading {@link MetadataTemplateField}s
 */
public interface MetadataFieldRepository extends IridaJpaRepository<MetadataTemplateField, Long> {

	/**
	 * Get a {@link MetadataTemplateField} based on its {@link String} label.
	 *
	 * @param label
	 * 		the {@link String} field label
	 *
	 * @return {@link MetadataTemplateField}
	 */
	@Query("from MetadataTemplateField m where m.label = ?1")
	public MetadataTemplateField findMetadataFieldByLabel(String label);

	@Query("FROM MetadataTemplateField m where m.label LIKE %:query%")
	public List<MetadataTemplateField> findAllMetadataFieldsByLabelQuery(@Param("query") String query);
}
