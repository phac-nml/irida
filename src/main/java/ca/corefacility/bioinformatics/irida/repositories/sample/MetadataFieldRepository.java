package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataField;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing and reading {@link MetadataField}s
 */
public interface MetadataFieldRepository extends IridaJpaRepository<MetadataField, Long> {

	/**
	 * Get a {@link MetadataField} based on its {@link String} label.
	 *
	 * @param label
	 * 		the {@link String} field label
	 *
	 * @return {@link MetadataField}
	 */
	@Query("from MetadataField m where m.label = ?1")
	public MetadataField findMetadataFieldByLabel(String label);

	@Query("FROM MetadataField m where m.label LIKE %:query%")
	public List<MetadataField> findAllMetadataFieldsByLabelQuery(@Param("query") String query);
}
