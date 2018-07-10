package ca.corefacility.bioinformatics.irida.repositories;

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

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;

/**
 * Custom repository implementation for {@link Project}
 */
@Repository
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
	private static final Logger logger = LoggerFactory.getLogger(ProjectRepositoryImpl.class);

	private DataSource dataSource;

	@Autowired
	public ProjectRepositoryImpl(DataSource dataSource) {

		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ProjectSampleAnalysisOutputInfo> getAllAnalysisOutputInfoForProject(Long projectId, Long userId, Set<UUID> workflowIds) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("projectId", projectId);
		parameters.addValue("userId", userId);
		final List<String> workflowUUIDStrings = workflowIds.stream()
				.map(UUID::toString)
				.collect(Collectors.toList());
		parameters.addValue("workflowIds", workflowUUIDStrings);
		// @formatter:off
		String query =
				"SELECT " +
				"  s.id AS sampleId, " +
				"  s.sampleName AS sampleName, " +
				"  a.id AS analysisId, " +
				"  aofmap.analysis_output_file_key AS analysisOutputFileKey, " +
				"  aof.file_path AS filePath, " +
				"  aof.id AS analysisOutputFileId, " +
				"  a.analysis_type AS analysisType, " +
				"  asub.workflow_id AS workflowId, " +
				"  aof.created_date AS createdDate, " +
				"  asub.name AS analysisSubmissionName, " +
				"  asub.id AS analysisSubmissionId, " +
				"  u.id AS userId, " +
				"  u.firstName AS userFirstName, " +
				"  u.lastName AS userLastName " +
				"FROM analysis_output_file aof "+
				"  INNER JOIN analysis_output_file_map aofmap ON aof.id = aofmap.analysisOutputFilesMap_id" +
				"  INNER JOIN analysis a ON aofmap.analysis_id = a.id" +
				"  INNER JOIN analysis_submission asub ON a.id = asub.analysis_id" +
				"  INNER JOIN analysis_submission_sequencing_object o ON asub.id = o.analysis_submission_id" +
				"  INNER JOIN sequencing_object seqobj ON o.sequencing_object_id = seqobj.id" +
				"  INNER JOIN sample_sequencingobject sso on sso.sequencingobject_id = o.sequencing_object_id" +
				"  INNER JOIN sample s ON sso.sample_id = s.id" +
				"  INNER JOIN project_sample psample ON s.id = psample.sample_id" +
				"  INNER JOIN project p ON psample.project_id = p.id" +
				"  INNER JOIN user u ON asub.submitter = u.id" +
				"  LEFT JOIN project_analysis_submission pasub ON asub.id = pasub.analysis_submission_id "+
				"WHERE" +
				// project id parameter goes here
				"  p.id = :projectId"  +
				// non-collection type analysis type
				"  AND asub.workflow_id IN (:workflowIds)" +
				// shared to the project by a user
				"  AND (pasub.project_id = p.id " +
				// or automated SISTR or assembly
				"       OR (seqobj.sistr_typing = asub.id OR seqobj.automated_assembly = asub.id)" +
				// or user's own analyses
				"       OR u.id = :userId)";
		// @formatter:on
		logger.trace("Getting all shared or automated analysis output file info for project id=" + projectId + " and user id=" + userId);
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(dataSource);
		return tmpl.query(query, parameters,
				new BeanPropertyRowMapper(ProjectSampleAnalysisOutputInfo.class));
	}

}
