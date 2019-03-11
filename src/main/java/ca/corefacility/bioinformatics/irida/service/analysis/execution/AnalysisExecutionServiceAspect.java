package ca.corefacility.bioinformatics.irida.service.analysis.execution;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceConfig;
import ca.corefacility.bioinformatics.irida.config.repository.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync;

/**
 * Switches an {@link AnalysisSubmission} to an error state on an exception when
 * being submitted for analysis.
 * 
 * The {@link Order} here needs to be a value between the order of
 * {@link AnalysisExecutionServiceGalaxyAsync} (
 * {@link AnalysisExecutionServiceConfig#ASYNC_ORDER}) and the order of the
 * Transaction Manager (
 * {@link IridaApiRepositoriesConfig#TRANSACTION_MANAGEMENT_ORDER}). This means
 * that the order of execution is
 * {@link AnalysisExecutionServiceGalaxyAsync}, then
 * {@link AnalysisExecutionServiceAspect}, then Transaction Manager.
 * 
 *
 */
@Aspect
@Order(AnalysisExecutionServiceAspect.ANALYSIS_EXECUTION_ASPECT_ORDER)
public class AnalysisExecutionServiceAspect {

	/**
	 * Defines the order for the {@link AnalysisExecutionServiceAspect}.
	 */
	public static final int ANALYSIS_EXECUTION_ASPECT_ORDER =
			IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER - 1;

	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceAspect.class);
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	private EmailController emailController;

	@Autowired
	public AnalysisExecutionServiceAspect(AnalysisSubmissionRepository analysisSubmissionRepository,
			EmailController emailController) {
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.emailController = emailController;
	}

	/**
	 * Aspect that matches any asynchronous calls for performing analysis
	 * submissions and switches the submission to an error state on an
	 * exception.
	 *
	 * @param analysisSubmission The submission that has failed.
	 * @param emailController    for sending error emails for {@link AnalysisSubmission}s
	 * @param exception          The exception that was thrown.
	 */
	@AfterThrowing(value = "execution(* ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync.*(ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission)) && args(analysisSubmission)", throwing = ("exception"))
	public void toErrorStateOnException(AnalysisSubmission analysisSubmission, EmailController emailController,
			Exception exception) {
		logger.error(
				"Error occured for submission: " + analysisSubmission + " changing to state " + AnalysisState.ERROR,
				exception);
		analysisSubmission.setAnalysisState(AnalysisState.ERROR);
		analysisSubmissionRepository.save(analysisSubmission);
		if (analysisSubmission.getEmailPipelineResult()) {
			emailController.sendPipelineStatusEmail(analysisSubmission);
		}
	}
	
	/**
	 * Aspect that matches any asynchronous calls for cleaning analysis
	 * submissions and switches to a cleaned error state. exception.
	 * 
	 * @param analysisSubmission
	 *            The submission that has failed to be cleaned.
	 * 
	 * @param exception
	 *            The exception that was thrown.
	 */
	@AfterThrowing(value = "execution(* ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyCleanupAsync.*(ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission)) && args(analysisSubmission)", throwing = ("exception"))
	public void toErrorStateOnCleaningException(AnalysisSubmission analysisSubmission, Exception exception) {
		logger.error("Error occured while cleaning submission: " + analysisSubmission + " changing to cleaned state "
				+ AnalysisCleanedState.CLEANING_ERROR, exception);
		analysisSubmission.setAnalysisCleanedState(AnalysisCleanedState.CLEANING_ERROR);
		analysisSubmissionRepository.save(analysisSubmission);
	}
}
