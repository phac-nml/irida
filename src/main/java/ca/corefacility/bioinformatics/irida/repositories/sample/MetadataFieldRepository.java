package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.StaticMetadataTemplateField;
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

	/**
	 * Read a {@link StaticMetadataTemplateField} by its staticId
	 *
	 * @param staticId the static ID fo the field
	 * @return the read field
	 */
	@Query("from StaticMetadataTemplateField m where m.staticId = ?1")
	public StaticMetadataTemplateField findMetadataFieldByStaticId(String staticId);

	/**
	 * Get a list of all {@link StaticMetadataTemplateField}s
	 *
	 * @return the list of {@link StaticMetadataTemplateField}s
	 */
	@Query("from StaticMetadataTemplateField m where TYPE(m) = StaticMetadataTemplateField")
	public List<StaticMetadataTemplateField> findStaticMetadataFields();

	/**
	 * Get all {@link MetadataTemplateField}s in a given {@link MetadataTemplate}
	 *
	 * @param template the {@link MetadataTemplate} to get fields for
	 * @return a list of {@link MetadataTemplateField}
	 */
	@Query("SELECT t.fields FROM MetadataTemplate t WHERE t=?1")
	public List<MetadataTemplateField> getMetadataFieldsForTemplate(MetadataTemplate template);
}
