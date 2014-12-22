package ca.corefacility.bioinformatics.irida.service.analysis.execution;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceConfig;
import ca.corefacility.bioinformatics.irida.config.repository.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;

/**
 * Switches an {@link AnalysisSubmission} to an error state on an exception when
 * being submitted for analysis.
 * 
 * The {@link Order} here needs to be a value between the order of
 * {@link AnalysisExecutionServiceGalaxyAsync} (
 * {@link AnalysisExecutionServiceConfig.ASYNC_ORDER}) and the order of the
 * Transaction Manager (
 * {@link IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER}). This means
 * that the order of execution is
 * {@link AnalysisExecutionServiceGalaxyAsync} ->
 * {@link AnalysisExecutionServiceAspect} -> Transaction Manager.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Aspect
@Order(AnalysisExecutionServiceAspect.ANALYSIS_EXECUTION_ASPECT_ORDER)
public class AnalysisExecutionServiceAspect {

	/**
	 * Defines the order for the {@link AnalysisExecutionServiceAspect}.
	 */
	public static final int ANALYSIS_EXECUTION_ASPECT_ORDER = IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER - 1;

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
	 * 
	 * @param execption
	 *            The exception that was thrown.
	 */
	@AfterThrowing(value = "execution(* ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync.*(ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission)) && args(analysisSubmission)", throwing = ("exception"))
	public void toErrorStateOnException(AnalysisSubmission analysisSubmission, Exception exception) {
		logger.error("Error occured for submission: " + analysisSubmission + " changing to state "
				+ AnalysisState.ERROR, exception);
		analysisSubmission.setAnalysisState(AnalysisState.ERROR);
		analysisSubmissionRepository.save(analysisSubmission);
	}
}
