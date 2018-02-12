package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * A repository for storing and reading {@link MetadataTemplateField}s
 */
public interface MetadataFieldRepository
		extends IridaJpaRepository<MetadataTemplateField, Long>, MetadataFieldRepositoryCustom {

	/**
	 * Get a {@link MetadataTemplateField} based on its {@link String} label.
	 *
	 * @param label the {@link String} field label
	 * @return {@link MetadataTemplateField}
	 */
	@Query("from MetadataTemplateField m where m.label = ?1")
	public MetadataTemplateField findMetadataFieldByLabel(String label);

	/**
	 * Get a {@link List} of {@link MetadataTemplateField} with a label that partially matches the query
	 *
	 * @param query The {@link String} to test the {@link MetadataTemplateField}'s label against.
	 * @return {@link List} of {@link MetadataTemplateField} with labels that match the query.
	 */
	@Query("FROM MetadataTemplateField m where m.label LIKE %:query%")
	public List<MetadataTemplateField> findAllMetadataFieldsByLabelQuery(@Param("query") String query);
}
