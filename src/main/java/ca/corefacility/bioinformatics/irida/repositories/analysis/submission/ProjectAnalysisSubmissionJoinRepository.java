package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * repository for storing and reading {@link ProjectAnalysisSubmissionJoin}s
 */
public interface ProjectAnalysisSubmissionJoinRepository
		extends IridaJpaRepository<ProjectAnalysisSubmissionJoin, Long> {

	/**
	 * Get all {@link Project}s a given {@link AnalysisSubmission} is shared
	 * with
	 * 
	 * @param submission
	 *            the {@link AnalysisSubmission}
	 * @return a list of {@link ProjectAnalysisSubmissionJoin}s
	 */
	@Query("FROM ProjectAnalysisSubmissionJoin j WHERE j.analysisSubmission=?1")
	public List<ProjectAnalysisSubmissionJoin> getProjectsForSubmission(AnalysisSubmission submission);

	/**
	 * Gets all the {@link ProjectAnalysisSubmissionJoin}s for a given {@link Project}.
	 * 
	 * @param project
	 *            The {@link Project}.
	 * @return A list of {@link ProjectAnalysisSubmissionJoin}s
	 */
	@Query("FROM ProjectAnalysisSubmissionJoin j WHERE j.project=?1")
	public List<ProjectAnalysisSubmissionJoin> getSubmissionsForProject(Project project);

	/**
	 * Read a {@link ProjectAnalysisSubmissionJoin} object for a given
	 * {@link AnalysisSubmission} and {@link Project}
	 * 
	 * @param submission
	 *            the {@link AnalysisSubmission}
	 * @param project
	 *            the {@link Project}
	 * @return a {@link ProjectAnalysisSubmissionJoin} describing the
	 *         relationship
	 */
	@Query("FROM ProjectAnalysisSubmissionJoin j WHERE j.analysisSubmission=?1 AND j.project=?2")
	public ProjectAnalysisSubmissionJoin getProjectSubmissionShare(AnalysisSubmission submission, Project project);
}
