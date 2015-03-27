package ca.corefacility.bioinformatics.irida.config.analysis;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
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

import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.google.common.collect.Lists;

/**
 * Test configuration for {@link AnalysisExecutionService} classes.
 * 
 *
 */
@Configuration
@Profile("test")
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
	private SequenceFileService sequenceFileService;

	@Autowired
	private SequenceFilePairService sequenceFilePairService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;
	
	@Autowired
	private SequenceFilePairRepository sequenceFilePairRepository;
	
	@Autowired
	private AnalysisParameterServiceGalaxy analysisParameterServiceGalaxy;
	
	@Autowired
	private GalaxyHistoriesService galaxyHistoriesService;
	
	@Autowired
	private GalaxyLibrariesService galaxyLibrariesService;
	
	@Autowired
	private GalaxyLibraryBuilder galaxyLibraryBuilder;
	
	@Autowired
	private GalaxyWorkflowService galaxyWorkflowService;

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
				galaxyWorkflowService, analysisWorkspaceService(), iridaWorkflowsService);
	}
	
	@Lazy
	@Bean
	public AnalysisExecutionServiceGalaxyCleanupAsync analysisExecutionServiceGalaxyCleanupAsync() {
		return new AnalysisExecutionServiceGalaxyCleanupAsync(analysisSubmissionService,
				galaxyWorkflowService, galaxyHistoriesService, galaxyLibrariesService);
	}

	@Lazy
	@Bean
	public AnalysisWorkspaceServiceGalaxy analysisWorkspaceService() {
		return new AnalysisWorkspaceServiceGalaxy(galaxyHistoriesService, galaxyWorkflowService,
				sequenceFileService, sequenceFilePairService, galaxyLibraryBuilder,
				iridaWorkflowsService,
				analysisCollectionServiceGalaxy(), analysisProvenanceServiceGalaxy(), analysisParameterServiceGalaxy);
	}
	
	@Lazy
	@Bean
	public AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy() {
		return new AnalysisCollectionServiceGalaxy(galaxyHistoriesService);
	}

	@Lazy
	@Bean
	public AnalysisProvenanceServiceGalaxy analysisProvenanceServiceGalaxy() {
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getToolsClient();
		return new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService, toolsClient);
	}

	/**
	 * Builds a new Executor for analysis tasks.
	 * 
	 * @return A new Executor for analysis tasks.
	 */
	@Profile({"test", "it"})
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
		return new DatabaseSetupGalaxyITService(referenceFileRepository, sequenceFileService,
				sampleService, analysisExecutionService(),
				analysisSubmissionService, analysisSubmissionRepository, sequenceFilePairRepository);
	}
}
