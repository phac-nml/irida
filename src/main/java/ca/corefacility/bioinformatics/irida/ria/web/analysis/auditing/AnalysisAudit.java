package ca.corefacility.bioinformatics.irida.ria.web.analysis.auditing;

/*
 * This class is used for auditing analysis submissions
 */

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.ria.web.utilities.DateUtilities;

@Component
public class AnalysisAudit {

	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	public AnalysisAudit(AnalysisSubmissionRepository analysisSubmissionRepository) {
		this.analysisSubmissionRepository = analysisSubmissionRepository;
	}
	/**
	 * Gets the running time of an analysis
	 *
	 * @param submission {@link AnalysisSubmission} The submission {@link AnalysisSubmission}
	 * @return {@link Long} Running time of the analysis
	 */
	public Long getAnalysisRunningTime(AnalysisSubmission submission) {
		ArrayList<AnalysisSubmission> uniqueAuditedSubmissions = new ArrayList<>();

		// Gets a list of the analysis submission revisions for the submission
		Revisions<Integer, AnalysisSubmission> revisions = analysisSubmissionRepository.findRevisions(submission.getId());

		ArrayList<String> auditedStates = new ArrayList<>();

		// Get a unique list of the audited submissions based on the state
		for (Revision<Integer, AnalysisSubmission> rev : revisions) {
			AnalysisSubmission auditedSubmission = rev.getEntity();
			if (!auditedStates.contains(auditedSubmission.getAnalysisState()
					.toString())) {
				auditedStates.add(auditedSubmission.getAnalysisState()
						.toString());
				uniqueAuditedSubmissions.add(auditedSubmission);
			}
		}
		// Get the run time of the analysis from creation till completion/error
		return DateUtilities.getDurationInMilliseconds(submission.getCreatedDate(),
				uniqueAuditedSubmissions.get(uniqueAuditedSubmissions.size() - 1)
						.getModifiedDate());
	}

	/**
	 * Gets the state of analysis prior to error
	 *
	 * @param submissionId {@link Long} identifier for an {@link AnalysisSubmission}
	 * @return {@link String} State of analysis prior to error
	 */
	public AnalysisState getPreviousStateBeforeError(Long submissionId) {

		AnalysisSubmission previousRevision = null;

		// Get revisions from the analysis submission audit table for the submission
		Revisions<Integer, AnalysisSubmission> revisions = analysisSubmissionRepository.findRevisions(submissionId);
		// Go through the revisions and find the first one with an error. The revision
		// prior is set to the previousRevision
		for (Revision<Integer, AnalysisSubmission> rev : revisions) {
			AnalysisSubmission auditedSubmission = rev.getEntity();
			if (auditedSubmission.getAnalysisState() == AnalysisState.ERROR) {
				break;
			}
			previousRevision = auditedSubmission;
		}

		if (previousRevision == null) {
			return null;
		}

		return previousRevision.getAnalysisState();
	}
}
