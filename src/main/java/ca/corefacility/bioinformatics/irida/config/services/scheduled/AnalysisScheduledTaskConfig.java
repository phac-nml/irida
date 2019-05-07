package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyJobErrorsService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.JobErrorRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.CleanupAnalysisSubmissionConditionAge;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled task configuration for running analysis pipelines
 */
@Profile({ "prod", "analysis" })
@Configuration
public class AnalysisScheduledTaskConfig {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisScheduledTaskConfig.class);

	@Autowired
	private AnalysisExecutionService analysisExecutionService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private GalaxyJobErrorsService galaxyJobErrorsService;

	@Autowired
	private JobErrorRepository jobErrorRepository;

	@Autowired
	private EmailController emailController;

	/**
	 * Defines the time to clean up in number of days a submission must exist before it is cleaned up.
	 */
	@Value("${irida.analysis.cleanup.days}")
	private Double daysToCleanup;

	/**
	 * Rate in milliseconds of the analysis execution tasks.
	 */
	private static final long ANALYSIS_EXECUTION_TASK_RATE = 15000; // 15 seconds

	/**
	 * Rate in milliseconds of the cleanup task.
	 */
	private static final long CLEANUP_TASK_RATE = 60 * 60 * 1000; // 1 hour

	/**
	 * Cycle through any submissions and prepare them for execution.
	 */
	@Scheduled(initialDelay = 2000, fixedDelay = ANALYSIS_EXECUTION_TASK_RATE)
	public void prepareAnalyses() {
		analysisExecutionScheduledTask().prepareAnalyses();
	}

	/**
	 * Cycle through any outstanding submissions and execute them.
	 */
	@Scheduled(initialDelay = 3000, fixedDelay = ANALYSIS_EXECUTION_TASK_RATE)
	public void executeAnalyses() {
		analysisExecutionScheduledTask().executeAnalyses();
	}

	/**
	 * Cycle through any submissions running in Galaxy and monitor the status.
	 */
	@Scheduled(initialDelay = 4000, fixedDelay = ANALYSIS_EXECUTION_TASK_RATE)
	public void monitorRunningAnalyses() {
		analysisExecutionScheduledTask().monitorRunningAnalyses();
	}

	/**
	 * Cycle through any completed submissions and transfer the results.
	 */
	@Scheduled(initialDelay = 5000, fixedDelay = ANALYSIS_EXECUTION_TASK_RATE)
	public void transferAnalysesResults() {
		analysisExecutionScheduledTask().transferAnalysesResults();
	}

	/**
	 * Cycle through any transferred submissions and perform post-processing
	 */
	@Scheduled(initialDelay = 6000, fixedDelay = ANALYSIS_EXECUTION_TASK_RATE)
	public void postProcessResults() {
		analysisExecutionScheduledTask().postProcessResults();
	}

	/**
	 * Cycle through any completed or error submissions and clean up results from the execution manager.
	 */
	@Scheduled(initialDelay = 10000, fixedDelay = CLEANUP_TASK_RATE)
	public void cleanupAnalysisSubmissions() {
		analysisExecutionScheduledTask().cleanupAnalysisSubmissions();
	}

	/**
	 * Creates a new bean with a AnalysisExecutionScheduledTask for performing
	 * the analysis tasks.
	 *
	 * @return A AnalysisExecutionScheduledTask bean.
	 */
	@DependsOn("analysisSubmissionCleanupService")
	@Bean
	public AnalysisExecutionScheduledTask analysisExecutionScheduledTask() {
		return new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository, analysisExecutionService,
				cleanupAnalysisSubmissionCondition(), galaxyJobErrorsService, jobErrorRepository, emailController);
	}

	/**
	 * Builds a condition object defining the conditions under which an analysis
	 * submission should be cleaned up.
	 *
	 * @return A {@link CleanupAnalysisSubmissionConditionAge}.
	 */
	@Bean
	public CleanupAnalysisSubmissionCondition cleanupAnalysisSubmissionCondition() {
		if (daysToCleanup == null) {
			logger.info("No irida.analysis.cleanup.days set, defaulting to no cleanup");
			return CleanupAnalysisSubmissionCondition.NEVER_CLEANUP;
		} else {
			logger.info("Setting daysToCleanup to be irida.analysis.cleanup.time=" + daysToCleanup);

			// Converts fraction of day to a millisecond value
			long millisToCleanup = Math.round(daysToCleanup * TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
			return new CleanupAnalysisSubmissionConditionAge(Duration.ofMillis(millisToCleanup));
		}
	}
}
