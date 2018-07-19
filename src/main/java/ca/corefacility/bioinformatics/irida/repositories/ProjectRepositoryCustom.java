package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;

/**
 * Custom repository for {@link Project}
 */
public interface ProjectRepositoryCustom {

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
}
