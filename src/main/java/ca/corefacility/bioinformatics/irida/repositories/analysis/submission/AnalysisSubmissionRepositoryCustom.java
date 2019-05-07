package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;

/**
 * Interface for methods using native SQL queries to get {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} info for {@link ca.corefacility.bioinformatics.irida.model.project.Project} and {@link ca.corefacility.bioinformatics.irida.model.user.User}
 */
public interface AnalysisSubmissionRepositoryCustom {

	/**
	 * Get all {@link ProjectSampleAnalysisOutputInfo} shared with a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @param workflowIds Workflow UUIDs of workflow pipelines to get output files for
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllAnalysisOutputInfoSharedWithProject(Long projectId, Set<UUID> workflowIds);

	/**
	 * Get all automated {@link ProjectSampleAnalysisOutputInfo} for a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @param workflowIds Workflow UUIDs of workflow pipelines to get output files for
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllAutomatedAnalysisOutputInfoForAProject(Long projectId,
			Set<UUID> workflowIds);

	/**
	 * Get all {@link ca.corefacility.bioinformatics.irida.model.user.User} generated analysis output information.
	 *
	 * @param userId {@link ca.corefacility.bioinformatics.irida.model.user.User} id
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(Long userId);
}
