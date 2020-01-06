package ca.corefacility.bioinformatics.irida.ria.web.analysis.auditing;

/*
 * This class is used for auditing analysis submissions using
 * AuditReader and EntityManagerFactory.
 */

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.utilities.DateUtilities;

public class AnalysisAuditing {
	private EntityManagerFactory entityManagerFactory;

	public AnalysisAuditing(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory=entityManagerFactory;
	}

	/**
	 * Gets the running time of an analysis
	 *
	 * @param submission {@link AnalysisSubmission} The submission {@link AnalysisSubmission}
	 * @return {@link Long} Running time of the analysis
	 */
	public Long getAnalysisRunningTime(AnalysisSubmission submission) {
		AuditReader audit = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
		ArrayList<AnalysisSubmission> uniqueAuditedSubmissions = new ArrayList<>();
		if (audit != null ) {
			// Gets a list of the analysis submission revisions for the submission
			List<?> auditResultSet = audit.createQuery()
					.forRevisionsOfEntity(AnalysisSubmission.class, true, false)
					.add(AuditEntity.id()
							.eq(submission.getId()))
					.getResultList();

			ArrayList<String> auditedStates = new ArrayList<>();

			// Get a unique list of the audited submissions based on the state
			for (Object auditResult : auditResultSet) {
				AnalysisSubmission auditedSubmission = (AnalysisSubmission) auditResult;
				if (auditedSubmission != null && !auditedStates.contains(auditedSubmission.getAnalysisState()
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
		return 0L;
	}

	/**
	 * Gets the state of analysis prior to error
	 *
	 * @param submissionId {@link Long} identifier for an {@link AnalysisSubmission}
	 * @return {@link String} State of analysis prior to error
	 */
	public AnalysisState getPreviousStateBeforeError(Long submissionId) {
		AuditReader audit = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
		AnalysisSubmission previousRevision = null;

		if (audit != null) {
			// Get revisions from the analysis submission audit table for the submission
			List<?> auditResultSet = audit.createQuery()
					.forRevisionsOfEntity(AnalysisSubmission.class, true, false)
					.add(AuditEntity.id()
							.eq(submissionId))
					.getResultList();

			// Go through the revisions and find the first one with an error. The revision
			// prior is set to the previousRevision
			for (Object auditResult : auditResultSet) {
				AnalysisSubmission auditedSubmission = (AnalysisSubmission) auditResult;
				if (auditedSubmission != null && auditedSubmission.getAnalysisState() == AnalysisState.ERROR) {
					break;
				}
				previousRevision = auditedSubmission;
			}
		}
		if (previousRevision == null) {
			return null;
		}

		return previousRevision.getAnalysisState();
	}
}
