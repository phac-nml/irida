package ca.corefacility.bioinformatics.irida.config.analysis;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.results.impl.AnalysisSubmissionSampleProcessorImpl;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyCleanupAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisCollectionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisProvenanceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Test configuration for {@link AnalysisExecutionService} classes.
 * 
 *
 */
@TestConfiguration
@EnableAsync(order = AnalysisExecutionServiceConfig.ASYNC_ORDER)
@Conditional(NonWindowsPlatformCondition.class)
public class AnalysisExecutionServiceTestConfig {

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private ReferenceFileRepository referenceFileRepository;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	@Autowired
	private AnalysisParameterServiceGalaxy analysisParameterServiceGalaxy;

	@Autowired
	private GalaxyHistoriesService galaxyHistoriesService;

	@Autowired
	private GalaxyLibrariesService galaxyLibrariesService;

	@Autowired
	private GalaxyWorkflowService galaxyWorkflowService;

	@Autowired
	private SequencingObjectService sequencingObjectService;

	@Autowired
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;

	@Autowired
	private SampleRepository sampleRepository;

	@Bean
	public AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor() {
		List<AnalysisSampleUpdater> analysisSampleUpdaters = Lists.newLinkedList();

		return new AnalysisSubmissionSampleProcessorImpl(sampleRepository, analysisSampleUpdaters);
	}

	@Lazy
	@Bean
	public AnalysisExecutionService analysisExecutionService() {
		return new AnalysisExecutionServiceGalaxy(analysisSubmissionService, galaxyHistoriesService,
				analysisExecutionServiceGalaxyAsync(), analysisExecutionServiceGalaxyCleanupAsync());
	}

	@Lazy
	@Bean
	public AnalysisExecutionServiceGalaxyAsync analysisExecutionServiceGalaxyAsync() {
		return new AnalysisExecutionServiceGalaxyAsync(analysisSubmissionService, analysisService,
				galaxyWorkflowService, analysisWorkspaceService(), iridaWorkflowsService,
				analysisSubmissionSampleProcessor);
	}

	@Lazy
	@Bean
	public AnalysisExecutionServiceGalaxyCleanupAsync analysisExecutionServiceGalaxyCleanupAsync() {
		return new AnalysisExecutionServiceGalaxyCleanupAsync(analysisSubmissionService, galaxyWorkflowService,
				galaxyHistoriesService, galaxyLibrariesService);
	}

	@Lazy
	@Bean
	public AnalysisWorkspaceServiceGalaxy analysisWorkspaceService() {
		return new AnalysisWorkspaceServiceGalaxy(galaxyHistoriesService, galaxyWorkflowService, galaxyLibrariesService,
				iridaWorkflowsService, analysisCollectionServiceGalaxy(), analysisProvenanceServiceGalaxy(),
				analysisParameterServiceGalaxy, sequencingObjectService);
	}

	@Lazy
	@Bean
	public AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy() {
		return new AnalysisCollectionServiceGalaxy(galaxyHistoriesService);
	}

	@Lazy
	@Bean
	public AnalysisProvenanceServiceGalaxy analysisProvenanceServiceGalaxy() {
		final ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();
		final JobsClient jobsClient = localGalaxy.getGalaxyInstanceAdmin().getJobsClient();
		return new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService, toolsClient, jobsClient);
	}

	/**
	 * Builds a new Executor for analysis tasks.
	 * 
	 * @return A new Executor for analysis tasks.
	 */
	@Bean
	public Executor analysisTaskExecutor() {
		ScheduledExecutorService delegateExecutor = Executors.newSingleThreadScheduledExecutor();
		SecurityContext schedulerContext = createSchedulerSecurityContext();
		return new DelegatingSecurityContextScheduledExecutorService(delegateExecutor, schedulerContext);
	}

	/**
	 * Creates a security context object for the analysis tasks.
	 * 
	 * @return A {@link SecurityContext} for the analysis tasks.
	 */
	private SecurityContext createSchedulerSecurityContext() {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		User admin = new User();
		admin.setUsername("aaron");
		Authentication adminAuthentication = new PreAuthenticatedAuthenticationToken(admin, null,
				Lists.newArrayList(Role.ROLE_ADMIN));

		context.setAuthentication(adminAuthentication);

		return context;
	}

	@Lazy
	@Bean
	public DatabaseSetupGalaxyITService analysisExecutionGalaxyITService() {
		return new DatabaseSetupGalaxyITService(referenceFileRepository, sampleService, analysisExecutionService(),
				analysisSubmissionService, analysisSubmissionRepository, sequencingObjectService);
	}
}
