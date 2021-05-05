package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

public class MetadataEntryRepositoryImpl implements MetadataEntryRepositoryCustom {
	private final DataSource dataSource;
	private final EntityManager entityManager;

	@Autowired
	public MetadataEntryRepositoryImpl(DataSource dataSource, EntityManager entityManager) {
		this.dataSource = dataSource;
		this.entityManager = entityManager;
	}

	public Map<Long, Set<MetadataEntry>> getMetadataForProject(Project project) {

		String queryString = "SELECT DISTINCT f.* FROM project_sample p INNER JOIN metadata_entry s ON p.sample_id=s.sample_id INNER JOIN metadata_field f ON s.field_id=f.id WHERE p.project_id=:project";
		Query nativeQuery = entityManager.createNativeQuery(queryString, MetadataTemplateField.class);

		nativeQuery.setParameter("project", project.getId());

		List<MetadataTemplateField> fields = nativeQuery.getResultList();

		Map<Long, MetadataTemplateField> fieldMap = fields.stream()
				.collect(Collectors.toMap(MetadataTemplateField::getId, field -> field));

		String entityQueryString = "select e.id, e.type, e.value, e.field_id, e.sample_id from metadata_entry e INNER JOIN project_sample s ON s.sample_id=e.sample_id WHERE s.project_id=:project";
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("project", project.getId());

		List<SampleMetadataEntry> sampleEntryCollection = tmpl.query(entityQueryString, parameters, (rs, rowNum) -> {
			String type = rs.getString("e.type");
			String value = rs.getString("e.value");
			long entryId = rs.getLong("e.id");
			long fieldId = rs.getLong("e.field_id");
			long sampleId = rs.getLong("e.sample_id");

			MetadataTemplateField metadataTemplateField = fieldMap.get(fieldId);

			MetadataEntry entry = new MetadataEntry(value, type, metadataTemplateField);
			entry.setId(entryId);

			return new SampleMetadataEntry(sampleId, entry);
		});

		Set<Long> sampleIdSet = sampleEntryCollection.stream()
				.map(SampleMetadataEntry::getSample)
				.collect(Collectors.toSet());
		Map<Long, Set<MetadataEntry>> sampleMetadata = new HashMap<>();
		for (Long s : sampleIdSet) {
			sampleMetadata.put(s, new HashSet<>());
		}

		for (SampleMetadataEntry entries : sampleEntryCollection) {
			sampleMetadata.get(entries.getSample())
					.add(entries.getEntry());
		}

		return sampleMetadata;

	}

	private class SampleMetadataEntry {
		Long sample;
		MetadataEntry entry;

		SampleMetadataEntry(Long sample, MetadataEntry entry) {
			this.sample = sample;
			this.entry = entry;
		}

		public Long getSample() {
			return sample;
		}

		public MetadataEntry getEntry() {
			return entry;
		}
	}
}
