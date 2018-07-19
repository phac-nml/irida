package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;

@Repository
public class AnalysisSubmissionRepositoryImpl implements AnalysisSubmissionRepositoryCustom {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisSubmissionRepositoryImpl.class);

	private DataSource dataSource;

	@Autowired
	public AnalysisSubmissionRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(Long userId) {
		// @formatter:off
		String querySelectAnalysisOutputsForUser =
			"SELECT\n"
			+ "  s.id AS sampleId,\n"
			+ "  s.sampleName AS sampleName,\n"
			+ "  a.id AS analysisId,\n"
			+ "  aofmap.analysis_output_file_key AS analysisOutputFileKey,\n"
			+ "  aof.file_path AS filePath,\n"
			+ "  aof.id AS analysisOutputFileId,\n"
			+ "  a.analysis_type AS analysisType,\n"
			+ "  asub.workflow_id AS workflowId,\n"
			+ "  aof.created_date AS createdDate,\n"
			+ "  asub.name AS analysisSubmissionName,\n"
			+ "  asub.id AS analysisSubmissionId\n"
			+ "FROM analysis_output_file aof\n"
			+ "  INNER JOIN analysis_output_file_map aofmap ON aof.id = aofmap.analysisOutputFilesMap_id\n"
			+ "  INNER JOIN analysis a ON aofmap.analysis_id = a.id\n"
			+ "  INNER JOIN analysis_submission asub ON a.id = asub.analysis_id\n"
			+ "  INNER JOIN analysis_submission_sequencing_object o ON asub.id = o.analysis_submission_id\n"
			+ "  INNER JOIN sequencing_object seqobj ON o.sequencing_object_id = seqobj.id\n"
			+ "  INNER JOIN sample_sequencingobject sso on sso.sequencingobject_id = o.sequencing_object_id\n"
			+ "  INNER JOIN sample s ON sso.sample_id = s.id\n"
			+ "WHERE\n"
			+ "  asub.submitter = :userId";
		// @formatter:on
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userId", userId);
		logger.trace("Getting all automated analysis output file info for user id=" + userId);
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		return tmpl.query(querySelectAnalysisOutputsForUser, parameters,
				new BeanPropertyRowMapper(ProjectSampleAnalysisOutputInfo.class));
	}
}
