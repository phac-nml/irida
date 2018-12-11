package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;

/**
 * Impl of custom methods for {@link SampleRepository}.  This class can be used for speed improvements for sample
 * listing methods.
 */
public class SampleRepositoryImpl implements SampleRepositoryCustom {
	private final DataSource dataSource;

	@Autowired
	public SampleRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Sample> getSamplesForProjectShallow(Project project) {
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();

		//query to read samples for a project
		String queryString = "select s.id, s.createdDate, s.modifiedDate, s.description, s.sampleName, s.collectedBy, s.geographicLocationName, s.isolate, s.isolationSource, s.latitude, s.longitude, s.organism, s.strain, s.collectionDate, null as remote_status FROM sample s INNER JOIN project_sample p ON p.sample_id=s.id WHERE p.project_id=:project";

		parameters.addValue("project", project.getId());

		return tmpl.query(queryString, parameters, new BeanPropertyRowMapper(Sample.class));
	}
}
