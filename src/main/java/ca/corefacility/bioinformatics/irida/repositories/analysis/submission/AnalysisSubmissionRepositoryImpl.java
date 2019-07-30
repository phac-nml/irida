package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;

/**
 * Implementation of {@link AnalysisSubmissionRepositoryCustom} with methods using native SQL queries to get {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} info for {@link ca.corefacility.bioinformatics.irida.model.project.Project} and {@link ca.corefacility.bioinformatics.irida.model.user.User}
 */
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
	public List<ProjectSampleAnalysisOutputInfo> getAllAnalysisOutputInfoSharedWithProject(Long projectId,
			Set<UUID> workflowIds) {
		// @formatter:off
		final String query =
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
			+ "  asub.id AS analysisSubmissionId,\n"
			+ "  u.id AS userId,\n"
			+ "  u.firstName AS userFirstName,\n"
			+ "  u.lastName AS userLastName\n"
			+ "FROM analysis_output_file aof\n"
			+ "  INNER JOIN analysis_output_file_map aofmap ON aof.id = aofmap.analysisOutputFilesMap_id\n"
			+ "  INNER JOIN analysis a ON aofmap.analysis_id = a.id\n"
			+ "  INNER JOIN analysis_submission asub ON a.id = asub.analysis_id\n"
			+ "  INNER JOIN analysis_submission_sequencing_object o ON asub.id = o.analysis_submission_id\n"
			+ "  INNER JOIN sample_sequencingobject sso ON sso.sequencingobject_id = o.sequencing_object_id\n"
			+ "  INNER JOIN sample s ON sso.sample_id = s.id\n"
			+ "  INNER JOIN project_sample psample ON s.id = psample.sample_id\n"
			+ "  INNER JOIN user u ON asub.submitter = u.id\n"
			+ "  INNER JOIN project_analysis_submission pasub ON asub.id = pasub.analysis_submission_id\n"
			+ "WHERE\n"
			+ "  psample.project_id = :projectId\n"
			+ "  AND asub.workflow_id IN (:workflowIds)\n";
		// @formatter:on
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		// need to explicitly convert UUIDs to String
		final List<String> workflowUUIDStrings = workflowIds.stream()
				.map(UUID::toString)
				.collect(Collectors.toList());
		parameters.addValue("projectId", projectId);
		parameters.addValue("workflowIds", workflowUUIDStrings);
		logger.trace("Getting all shared analysis output file info for project id=" + projectId);
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		return tmpl.query(query, parameters, new BeanPropertyRowMapper(ProjectSampleAnalysisOutputInfo.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ProjectSampleAnalysisOutputInfo> getAllAutomatedAnalysisOutputInfoForAProject(Long projectId,
			Set<UUID> workflowIds) {
		// @formatter:off
		final String query =
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
			+ "  asub.id AS analysisSubmissionId,\n"
			+ "  u.id AS userId,\n"
			+ "  u.firstName AS userFirstName,\n"
			+ "  u.lastName AS userLastName\n"
			+ "FROM analysis_output_file aof\n"
			+ "  INNER JOIN analysis_output_file_map aofmap ON aof.id = aofmap.analysisOutputFilesMap_id\n"
			+ "  INNER JOIN analysis a ON aofmap.analysis_id = a.id\n"
			+ "  INNER JOIN analysis_submission asub ON a.id = asub.analysis_id\n"
			+ "  INNER JOIN analysis_submission_sequencing_object o ON asub.id = o.analysis_submission_id\n"
			+ "  INNER JOIN sample_sequencingobject sso ON sso.sequencingobject_id = o.sequencing_object_id\n"
			+ "  INNER JOIN sample s ON sso.sample_id = s.id\n"
			+ "  INNER JOIN project_sample psample ON s.id = psample.sample_id\n"
			+ "  INNER JOIN user u ON asub.submitter = u.id\n"
			+ "WHERE\n"
			+ "  psample.project_id = :projectId\n"
			+ "  AND asub.workflow_id IN (:workflowIds)\n"
			+ "  AND asub.automated=1";
		// @formatter:on
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("projectId", projectId);
		final List<String> workflowUUIDStrings = workflowIds.stream()
				.map(UUID::toString)
				.collect(Collectors.toList());
		parameters.addValue("workflowIds", workflowUUIDStrings);
		logger.trace("Getting all automated analysis output file info for project id=" + projectId);
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		return tmpl.query(query, parameters, new BeanPropertyRowMapper(ProjectSampleAnalysisOutputInfo.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(Long userId) {
		// @formatter:off
		String query =
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
			+ "  INNER JOIN sample_sequencingobject sso ON sso.sequencingobject_id = o.sequencing_object_id\n"
			+ "  INNER JOIN sample s ON sso.sample_id = s.id\n"
			+ "WHERE\n"
			+ "  asub.submitter = :userId";
		// @formatter:on
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userId", userId);
		logger.trace("Getting all automated analysis output file info for user id=" + userId);
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		return tmpl.query(query, parameters, new BeanPropertyRowMapper(ProjectSampleAnalysisOutputInfo.class));
	}
}
