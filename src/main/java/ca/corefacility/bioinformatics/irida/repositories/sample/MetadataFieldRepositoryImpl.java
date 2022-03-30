package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
	@Override
	public List<MetadataTemplateField> getMetadataFieldsForProject(Project p) {
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();

		/*
		 * The below code for loading the metadata fields could be simplified
		 * into a single query, but the previous 3-join query was taking 10s of
		 * seconds to load for large projects. The 2 query process below for
		 * loading the fields is providing significantly better performance than
		 * loading fields in a single 3-join query.
		 */

		// First load all the field IDs related to this project
		String fieldIdQueryString = "SELECT DISTINCT e.field_id FROM metadata_entry e INNER JOIN project_sample p ON e.sample_id=p.sample_id WHERE p.project_id=:project";
		parameters.addValue("project", p.getId());
		List<Long> fieldIds = tmpl.queryForList(fieldIdQueryString, parameters, Long.class);

		List<MetadataTemplateField> resultList;
		if (!fieldIds.isEmpty()) {
			// next load all the full fields with those ids
			String queryString = "SELECT f from MetadataTemplateField f WHERE f.id IN :fields";
			TypedQuery<MetadataTemplateField> nativeQuery = entityManager.createQuery(queryString,
					MetadataTemplateField.class);
			nativeQuery.setParameter("fields", fieldIds);
			resultList = nativeQuery.getResultList();
		} else {
			resultList = new ArrayList<>();
		}

		return resultList;
	}
}
