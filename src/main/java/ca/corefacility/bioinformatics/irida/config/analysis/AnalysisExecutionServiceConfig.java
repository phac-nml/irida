package ca.corefacility.bioinformatics.irida.config.analysis;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import ca.corefacility.bioinformatics.irida.model.workflow.manager.galaxy.ExecutionManagerGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibraryBuilder;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxy;
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
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
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
	private SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository;

	@Autowired
	private SequenceFileRepository sequenceFileRepository;
	
	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;
	
	@Lazy
	@Bean
	public AnalysisExecutionService analysisExecutionServiceSimplified() {
		return new AnalysisExecutionServiceGalaxy(analysisSubmissionService, galaxyHistoriesService(),
				analysisExecutionServiceGalaxyAsyncSimplified());
	}

	@Lazy
	@Bean
	public AnalysisExecutionServiceGalaxyAsync analysisExecutionServiceGalaxyAsyncSimplified() {
		return new AnalysisExecutionServiceGalaxyAsync(analysisSubmissionService, analysisService,
				galaxyWorkflowService(), analysisWorkspaceService(), iridaWorkflowsService);
	}

	@Lazy
	@Bean
	public AnalysisWorkspaceServiceGalaxy analysisWorkspaceService() {
		return new AnalysisWorkspaceServiceGalaxy(galaxyHistoriesService(), galaxyWorkflowService(),
				sampleSequenceFileJoinRepository, sequenceFileRepository, galaxyLibraryBuilder(), iridaWorkflowsService);
	}

	/**
	 * @return A GalaxyWorkflowService for interacting with Galaxy workflows.
	 */
	@Lazy
	@Bean
	public GalaxyWorkflowService galaxyWorkflowService() {
		return new GalaxyWorkflowService(historiesClient(), workflowsClient(), workflowChecksumEncoder(),
				StandardCharsets.UTF_8);
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
	 * @return A PasswordEncoder for generating or validating workflow
	 *         checksums.
	 */
	@Lazy
	@Bean
	public PasswordEncoder workflowChecksumEncoder() {
		return new StandardPasswordEncoder();
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
		return new GalaxyLibrariesService(librariesClient());
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
