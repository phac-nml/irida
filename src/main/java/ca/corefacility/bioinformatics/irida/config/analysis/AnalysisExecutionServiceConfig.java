package ca.corefacility.bioinformatics.irida.config.analysis;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import ca.corefacility.bioinformatics.irida.model.workflow.manager.galaxy.ExecutionManagerGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisCollectionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisProvenanceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;

/**
 * Configuration for an AnalysisExecutionService class.
 * 
 *
 */
@Configuration
@EnableAsync(order = AnalysisExecutionServiceConfig.ASYNC_ORDER)
@Profile({ "dev", "prod", "it" })
public class AnalysisExecutionServiceConfig {

	/**
	 * The order for asynchronous tasks. In particular, defines the order for
	 * methods in {@link AnalysisExecutionServiceGalaxyAsync}.
	 */
	public static final int ASYNC_ORDER = AnalysisExecutionServiceAspect.ANALYSIS_EXECUTION_ASPECT_ORDER - 1;

	@Autowired
	private ExecutionManagerGalaxy executionManager;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private SequenceFileService sequenceFileService;

	@Autowired
	private SequenceFilePairService sequenceFilePairService;

	@Autowired
	private SequenceFileRepository sequenceFileRepository;
	
	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;
	
	@Autowired
	private AnalysisParameterServiceGalaxy analysisParameterServiceGalaxy;
	
	/**
	 * Timeout in seconds to stop polling a Galaxy library.
	 */
	@Value("${galaxy.library.upload.timeout}")
	private int libraryTimeout;
	
	/**
	 * Polling time in seconds to poll a Galaxy library to check if
	 * datasets have been properly uploaded.
	 */
	@Value("${galaxy.library.upload.polling.time}")
	private int pollingTime;
	
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
				sequenceFileService, sequenceFilePairService, galaxyLibraryBuilder(), iridaWorkflowsService,
				analysisCollectionServiceGalaxy(), analysisProvenanceService(), analysisParameterServiceGalaxy);
	}

	@Lazy
	@Bean
	public AnalysisProvenanceServiceGalaxy analysisProvenanceService() {
		return new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService(), toolsClient());
	}
	
	@Lazy
	@Bean
	public AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy() {
		return new AnalysisCollectionServiceGalaxy(galaxyHistoriesService());
	}

	/**
	 * @return A GalaxyWorkflowService for interacting with Galaxy workflows.
	 */
	@Lazy
	@Bean
	public GalaxyWorkflowService galaxyWorkflowService() {
		return new GalaxyWorkflowService(historiesClient(), workflowsClient(), StandardCharsets.UTF_8);
	}

	/**
	 * @return A GalaxyLibraryBuilder for building libraries.
	 */
	@Lazy
	@Bean
	public GalaxyLibraryBuilder galaxyLibraryBuilder() {
		return new GalaxyLibraryBuilder(librariesClient(), galaxyRoleSearch(), executionManager.getLocation());
	}

	/**
	 * @return A GalaxyRoleSearch for searching through Galaxy roles.
	 */
	@Lazy
	@Bean
	public GalaxyRoleSearch galaxyRoleSearch() {
		return new GalaxyRoleSearch(rolesClient(), executionManager.getLocation());
	}

	/**
	 * @return A RolesClient for dealing with roles in Galaxy.
	 */
	@Lazy
	@Bean
	public RolesClient rolesClient() {
		return galaxyInstance().getRolesClient();
	}

	/**
	 * @return A WorkflowsClient for interacting with Galaxy.
	 */
	@Lazy
	@Bean
	public WorkflowsClient workflowsClient() {
		return galaxyInstance().getWorkflowsClient();
	}

	/**
	 * @return A LibrariesClient for interacting with Galaxy.
	 */
	@Lazy
	@Bean
	public LibrariesClient librariesClient() {
		return galaxyInstance().getLibrariesClient();
	}

	/**
	 * @return A GalaxyHistoriesService for interacting with Galaxy histories.
	 */
	@Lazy
	@Bean
	public GalaxyHistoriesService galaxyHistoriesService() {
		return new GalaxyHistoriesService(historiesClient(), toolsClient(), galaxyLibrariesService());
	}

	/**
	 * @return A GalaxyHistoriesService for interacting with Galaxy histories.
	 */
	@Lazy
	@Bean
	public GalaxyLibrariesService galaxyLibrariesService() {
		return new GalaxyLibrariesService(librariesClient(), pollingTime, libraryTimeout);
	}

	/**
	 * @return A ToolsClient for interacting with Galaxy tools.
	 */
	@Lazy
	@Bean
	public ToolsClient toolsClient() {
		return galaxyInstance().getToolsClient();
	}

	/**
	 * @return A HistoriesClient for interacting with Galaxy histories.
	 */
	@Lazy
	@Bean
	public HistoriesClient historiesClient() {
		return galaxyInstance().getHistoriesClient();
	}

	/**
	 * @return An instance of a connection to Galaxy.
	 */
	@Lazy
	@Bean
	public GalaxyInstance galaxyInstance() {
		return GalaxyInstanceFactory.get(executionManager.getLocation().toString(), executionManager.getAPIKey());
	}
}
