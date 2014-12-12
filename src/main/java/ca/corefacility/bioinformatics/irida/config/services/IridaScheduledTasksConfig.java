package ca.corefacility.bioinformatics.irida.config.services;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Config for only activating scheduled tasks in certain profiles.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Profile({ "prod" })
@Configuration
@EnableScheduling
public class IridaScheduledTasksConfig implements SchedulingConfigurer {

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisExecutionServiceSimplified analysisExecutionServiceSimplified;

	@Autowired
	private UserService userService;

	/**
	 * Cycle through any outstanding submissions and execute them.
	 */
	@Scheduled(initialDelay = 5000, fixedRate = 15000)
	public void executeAnalyses() {
		analysisExecutionScheduledTask().executeAnalyses();
	}

	/**
	 * Cycle through any completed submissions and transfer the results.
	 */
	@Scheduled(initialDelay = 5000, fixedRate = 15000)
	public void transferAnalysesResults() {
		analysisExecutionScheduledTask().transferAnalysesResults();
	}

	/**
	 * Creates a new bean with a AnalysisExecutionScheduledTask for performing
	 * the analysis tasks.
	 * 
	 * @return A AnalysisExecutionScheduledTask bean.
	 */
	@Bean
	public AnalysisExecutionScheduledTask analysisExecutionScheduledTask() {
		return new AnalysisExecutionScheduledTaskImpl(analysisSubmissionService, analysisSubmissionRepository,
				analysisExecutionServiceSimplified);
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
		ScheduledExecutorService delegateExecutor = Executors.newSingleThreadScheduledExecutor();
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

		Authentication anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));

		Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);
		User admin = userService.getUserByUsername("admin");
		SecurityContextHolder.getContext().setAuthentication(oldAuthentication);

		Authentication adminAuthentication = new PreAuthenticatedAuthenticationToken(admin, null,
				Lists.newArrayList(Role.ROLE_ADMIN));

		context.setAuthentication(adminAuthentication);

		return context;
	}
}
