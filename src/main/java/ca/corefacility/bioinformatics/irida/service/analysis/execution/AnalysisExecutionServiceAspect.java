package ca.corefacility.bioinformatics.irida.service.analysis.execution;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;

/**
 * Switches an {@link AnalysisSubmission} to an error state on an execption when being submitted for analysis.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
public class AnalysisExecutionServiceAspect {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceAspect.class);
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	public AnalysisExecutionServiceAspect(AnalysisSubmissionRepository analysisSubmissionRepository) {
		this.analysisSubmissionRepository = analysisSubmissionRepository;
	}

	/**
	 * Aspect that matches any asynchronous calls for performing analysis
	 * submissions and switches the submission to an error state on an
	 * exception.
	 * 
	 * @param analysisSubmission
	 *            The submission that has failed.
	 */
	@AfterThrowing("execution(* ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsyncSimplified.*(ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission)) && args(analysisSubmission)")
	public void toErrorStateOnException(AnalysisSubmission analysisSubmission) {
		logger.debug("Error occured for submission: " + analysisSubmission + " changing to state "
				+ AnalysisState.ERROR);
		analysisSubmission.setAnalysisState(AnalysisState.ERROR);
		analysisSubmissionRepository.save(analysisSubmission);
	}
}
