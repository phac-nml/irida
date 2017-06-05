package ca.corefacility.bioinformatics.irida.config.services;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;
import ca.corefacility.bioinformatics.irida.service.ProjectEventEmailScheduledTask;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.export.ExportUploadService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.CleanupAnalysisSubmissionConditionAge;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectSynchronizationService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Config for only activating scheduled tasks in certain profiles.
 * 
 *
 */
@Profile({ "prod" })
@Configuration
@EnableScheduling
public class IridaScheduledTasksConfig implements SchedulingConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(IridaScheduledTasksConfig.class);

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	
	@Autowired
	private AnalysisExecutionService analysisExecutionService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ExportUploadService uploadService;

	@Autowired 	
	private ProjectEventEmailScheduledTask eventEmailTask;
	
	@Autowired
	private ProjectSynchronizationService projectSyncService;
	
	@Value("${irida.scheduled.threads}")
	private int threadCount = 2;

	/**
	 * Rate in milliseconds of the analysis execution tasks.
	 */
	private static final long ANALYSIS_EXECUTION_TASK_RATE = 15000; // 15 seconds
	
	// rate in MS of the upload task rate
	private static final long UPLOAD_EXECUTION_TASK_RATE = 60000; // 60 seconds
	
	// rate in MS of the upload status checking
	private static final long UPLOAD_STATUS_TASK_RATE = 300000; // 5 minutes
	
	// rate in MS of the upload status checking
	private static final long PROJECT_SYNC_RATE = 300000; // 5 minutes
	
	/**
	 * Rate in milliseconds of the cleanup task.
	 */
	private static final long CLEANUP_TASK_RATE = 60*60*1000; // 1 hour
	
	/**
	 * Defines the time to clean up in number of days a submission must exist before it is cleaned up.
	 */
	@Value("${irida.analysis.cleanup.days}")
	private Double daysToCleanup;

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
	 * Cycle through any completed or error submissions and clean up results from the execution manager.
	 */
	@Scheduled(initialDelay = 10000, fixedDelay = CLEANUP_TASK_RATE)
	public void cleanupAnalysisSubmissions() {
		analysisExecutionScheduledTask().cleanupAnalysisSubmissions();
	}

	/**
	 * Launch the NCBI uploader
	 */
	@Scheduled(initialDelay = 1000, fixedDelay = UPLOAD_EXECUTION_TASK_RATE)
	public void ncbiUpload() {
		uploadService.launchUpload();
	}

	/**
	 * Launch the NCBI status checking
	 */
	@Scheduled(initialDelay = UPLOAD_STATUS_TASK_RATE, fixedDelay = UPLOAD_STATUS_TASK_RATE)
	public void ncbiUploadStatus() {
		uploadService.updateRunningUploads();
	}

	/**
	 * Check for any new events for users who are subscribed to projects and
	 * email them
	 */
	@Scheduled(cron = "${irida.scheduled.subscription.cron}")
	public void emailProjectEvents() {
		eventEmailTask.emailUserTasks();
	}
	
	/**
	 * Find projects which must be synchronized from remote sites
	 */
	@Scheduled(initialDelay = PROJECT_SYNC_RATE, fixedDelay = PROJECT_SYNC_RATE)
	public void syncProject(){
		projectSyncService.findMarkedProjectsToSync();
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
				cleanupAnalysisSubmissionCondition());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	/**
	 * Builds a new Executor for scheduled tasks.
	 * 
	 * @return A new Executor for scheduled tasks.
	 */
	private Executor taskExecutor() {
		ScheduledExecutorService delegateExecutor = Executors.newScheduledThreadPool(threadCount);

		SecurityContext schedulerContext = createSchedulerSecurityContext();
		return new DelegatingSecurityContextScheduledExecutorService(delegateExecutor, schedulerContext);
	}

	/**
	 * Creates a security context object for the scheduled tasks.
	 * 
	 * @return A {@link SecurityContext} for the scheduled tasks.
	 */
	private SecurityContext createSchedulerSecurityContext() {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		Authentication anonymousToken = new AnonymousAuthenticationToken(
				"nobody", "nobody", ImmutableList.of(Role.ROLE_ANONYMOUS));
		
		Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);
		User admin = userService.getUserByUsername("admin");
		SecurityContextHolder.getContext().setAuthentication(oldAuthentication);

		Authentication adminAuthentication = new PreAuthenticatedAuthenticationToken(
				admin, null, Lists.newArrayList(Role.ROLE_ADMIN));

		context.setAuthentication(adminAuthentication);

		return context;
	}
}