package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;

/**
 * Custom repository for {@link Project}
 */
public interface ProjectRepositoryCustom {

	/**
	 * Get all {@link ProjectSampleAnalysisOutputInfo} for a {@link Project}.
	 * <p>
	 * Only non-collection analysis type outputs from shared or automated analyses are returned.
	 *
	 * @param projectId {@link Project} id
	 * @param userId    {@link User} id
	 * @param workflowIds Workflow UUIDs of workflow pipelines to get output files for
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllAnalysisOutputInfoForProject(Long projectId, Long userId, Set<UUID> workflowIds);
}
