package ca.corefacility.bioinformatics.irida.config.analysis;

import java.nio.charset.StandardCharsets;
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
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;
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
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisCollectionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisProvenanceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
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
	
	/**
	 * Timeout in seconds to stop polling a Galaxy library.
	 */
	private static final int LIBRARY_TIMEOUT = 5 * 60;
	
	/**
	 * Polling time in seconds to poll a Galaxy library to check if
	 * datasets have been properly uploaded.
	 */
	private static final int LIBRARY_POLLING_TIME = 5;

	@Lazy
	@Bean
	public AnalysisExecutionService analysisExecutionService() {
		return new AnalysisExecutionServiceGalaxy(analysisSubmissionService, galaxyHistoriesService(),
				analysisExecutionServiceGalaxyAsync());
	}

	@Lazy
	@Bean
	public AnalysisExecutionServiceGalaxyAsync analysisExecutionServiceGalaxyAsync() {
		return new AnalysisExecutionServiceGalaxyAsync(analysisSubmissionService, analysisService,
				galaxyWorkflowService(), analysisWorkspaceService(), iridaWorkflowsService);
	}

	@Lazy
	@Bean
	public AnalysisWorkspaceServiceGalaxy analysisWorkspaceService() {
		return new AnalysisWorkspaceServiceGalaxy(galaxyHistoriesService(), galaxyWorkflowService(),
				sequenceFileService, sequenceFilePairService, galaxyLibraryBuilder(),
				iridaWorkflowsService,
				analysisCollectionServiceGalaxy(), analysisProvenanceServiceGalaxy(), analysisParameterServiceGalaxy);
	}
	
	@Lazy
	@Bean
	public AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy() {
		return new AnalysisCollectionServiceGalaxy(galaxyHistoriesService());
	}

	@Lazy
	@Bean
	public AnalysisProvenanceServiceGalaxy analysisProvenanceServiceGalaxy() {
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getToolsClient();
		return new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService(), toolsClient);
	}

	@Lazy
	@Bean
	public GalaxyHistoriesService galaxyHistoriesService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getToolsClient();
		return new GalaxyHistoriesService(historiesClient, toolsClient, galaxyLibrariesService());
	}

	@Lazy
	@Bean
	public GalaxyLibrariesService galaxyLibrariesService() {
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getLibrariesClient();
		return new GalaxyLibrariesService(librariesClient, LIBRARY_POLLING_TIME, LIBRARY_TIMEOUT);
	}

	@Lazy
	@Bean
	public GalaxyLibraryBuilder galaxyLibraryBuilder() {
		LibrariesClient librariesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getLibrariesClient();
		return new GalaxyLibraryBuilder(librariesClient, galaxyRoleSearch(), localGalaxy.getGalaxyURL());
	}

	@Lazy
	@Bean
	public GalaxyRoleSearch galaxyRoleSearch() {
		RolesClient rolesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getRolesClient();
		return new GalaxyRoleSearch(rolesClient, localGalaxy.getGalaxyURL());
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
	public GalaxyWorkflowService galaxyWorkflowService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getWorkflowsClient();

		return new GalaxyWorkflowService(historiesClient, workflowsClient, StandardCharsets.UTF_8);
	}

	@Lazy
	@Bean
	public DatabaseSetupGalaxyITService analysisExecutionGalaxyITService() {
		return new DatabaseSetupGalaxyITService(referenceFileRepository, sequenceFileService,
				sampleService, analysisExecutionService(),
				analysisSubmissionService, analysisSubmissionRepository, sequenceFilePairRepository);
	}
}
