package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;

public interface AnalysisSubmissionRepositoryCustom {

	/**
	 * Get all {@link ca.corefacility.bioinformatics.irida.model.user.User} generated analysis output information.
	 *
	 * @param userId {@link ca.corefacility.bioinformatics.irida.model.user.User} id
	 * @return a list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(Long userId);
}
