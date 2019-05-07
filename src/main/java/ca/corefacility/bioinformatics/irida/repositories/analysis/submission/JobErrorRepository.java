package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import java.util.List;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for managing {@link JobError} objects
 */
public interface JobErrorRepository extends IridaJpaRepository<JobError, Long> {

	/**
	 * Find first {@link JobError} for a given {@link AnalysisSubmission}
	 *
	 * @param analysisSubmission Galaxy {@link AnalysisSubmission}
	 * @return a {@link JobError} associated with an {@link AnalysisSubmission}
	 */
	JobError findFirstByAnalysisSubmission(AnalysisSubmission analysisSubmission);

	/**
	 * Find all {@link JobError} objects for a given {@link AnalysisSubmission}
	 *
	 * @param analysisSubmission Galaxy {@link AnalysisSubmission}
	 * @return all {@link JobError} objects associated with an {@link AnalysisSubmission}
	 */
	List<JobError> findAllByAnalysisSubmission(AnalysisSubmission analysisSubmission);

	/**
	 * Find all {@link JobError} objects for an {@link ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow}
	 *
	 * @param iridaWorkflowId UUID of an {@link ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow}
	 * @return all {@link JobError} objects associated with an {@link ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow}
	 */
	List<JobError> findAllByAnalysisSubmission_WorkflowId(UUID iridaWorkflowId);
}
