package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation of the custom methods for retrieving {@link MetadataEntry}
 */
public class MetadataEntryRepositoryImpl implements MetadataEntryRepositoryCustom {
	private final DataSource dataSource;
	private final EntityManager entityManager;

	@Autowired
	public MetadataEntryRepositoryImpl(DataSource dataSource, EntityManager entityManager) {
		this.dataSource = dataSource;
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<Long, Set<MetadataEntry>> getMetadataForProject(Project project,
			List<MetadataTemplateField> requestedFields) {
		checkArgument(!requestedFields.isEmpty(), "requestedFields must not be empty");

		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("project", project.getId());

		//get the metadata fields available in the project
		String queryString = "SELECT DISTINCT f.* FROM project_sample p INNER JOIN metadata_entry s ON p.sample_id=s.sample_id INNER JOIN metadata_field f ON s.field_id=f.id WHERE p.project_id=:project";
		Query nativeQuery = entityManager.createNativeQuery(queryString, MetadataTemplateField.class);
		nativeQuery.setParameter("project", project.getId());
		List<MetadataTemplateField> fields = nativeQuery.getResultList();

		//Collect the fields into a map from ID to field for use later
		Map<Long, MetadataTemplateField> fieldMap = fields.stream()
				.collect(Collectors.toMap(MetadataTemplateField::getId, field -> field));

		//get all the sample IDs in the project for mapping
		String sampleIdQueryString = "SELECT DISTINCT p.sample_id FROM project_sample p WHERE p.project_id=:project";
		List<Long> allSampleIds = tmpl.query(sampleIdQueryString, parameters,
				(rs, rowNum) -> rs.getLong("p.sample_id"));

		//query for all metadata entries used in the project
		String entityQueryString = "select e.id, e.type, e.value, e.field_id, e.sample_id from metadata_entry e INNER JOIN project_sample s ON s.sample_id=e.sample_id WHERE s.project_id=:project AND e.field_id IN (:fieldIds)";
		List<Long> requestedFieldIds = requestedFields.stream()
				.map(MetadataTemplateField::getId)
				.collect(Collectors.toList());
		parameters.addValue("fieldIds", requestedFieldIds);

		//map the results into a SampleMetadataEntry
		List<SampleMetadataEntry> sampleEntryCollection = tmpl.query(entityQueryString, parameters, (rs, rowNum) -> {
			//get the request columns
			String type = rs.getString("e.type");
			String value = rs.getString("e.value");
			long entryId = rs.getLong("e.id");
			long fieldId = rs.getLong("e.field_id");
			long sampleId = rs.getLong("e.sample_id");

			//get the field associated with this entry
			MetadataTemplateField metadataTemplateField = fieldMap.get(fieldId);

			// build a MetadataEntry for the object
			MetadataEntry entry = new MetadataEntry(value, type, metadataTemplateField);
			entry.setId(entryId);

			return new SampleMetadataEntry(sampleId, entry);
		});

		//build a map of sample ID some empty sets for us to add the metadata
		Map<Long, Set<MetadataEntry>> sampleMetadata = allSampleIds.stream()
				.collect(Collectors.toMap(s -> s, s -> new HashSet<>()));

		//for each sample id, add the associated metadata
		for (SampleMetadataEntry entries : sampleEntryCollection) {
			sampleMetadata.get(entries.getSampleId())
					.add(entries.getEntry());
		}

		return sampleMetadata;

	}

	/**
	 * A convenience class for collecting the results of the above metadata query.  It will be used after to convert into the sample/metadata map.
	 */
	private class SampleMetadataEntry {
		Long sampleId;
		MetadataEntry entry;

		SampleMetadataEntry(Long sampleId, MetadataEntry entry) {
			this.sampleId = sampleId;
			this.entry = entry;
		}

		public Long getSampleId() {
			return sampleId;
		}

		public MetadataEntry getEntry() {
			return entry;
		}
	}
}
