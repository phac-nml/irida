package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * Custom repository methods for getting {@link MetadataTemplateField}s
 */
public class MetadataFieldRepositoryImpl implements MetadataFieldRepositoryCustom {

	private final EntityManager entityManager;
	private DataSource dataSource;

	@Autowired
	public MetadataFieldRepositoryImpl(EntityManager entityManager, DataSource dataSource) {
		this.entityManager = entityManager;
		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MetadataTemplateField> getMetadataFieldsForProject(Project p) {

		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();

		//get the metadata fields available in the project
		String fieldIdQueryString = "SELECT DISTINCT e.field_id FROM metadata_entry e INNER JOIN project_sample p ON e.sample_id=p.sample_id WHERE p.project_id=:project";
		parameters.addValue("project", p.getId());
		List<Long> fieldIds = tmpl.queryForList(fieldIdQueryString, parameters, Long.class);

		String queryString = "SELECT * from metadata_field f WHERE f.id IN :fields";
		Query nativeQuery = entityManager.createNativeQuery(queryString, MetadataTemplateField.class);
		nativeQuery.setParameter("fields", fieldIds);

		return nativeQuery.getResultList();
	}
}
