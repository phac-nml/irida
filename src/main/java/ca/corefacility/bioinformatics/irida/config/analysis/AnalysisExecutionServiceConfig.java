package ca.corefacility.bioinformatics.irida.config.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;

import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyCleanupAsync;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisCollectionServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisParameterServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisProvenanceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxy;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Configuration for an AnalysisExecutionService class.
 * 
 *
 */
@Configuration
@EnableAsync(order = AnalysisExecutionServiceConfig.ASYNC_ORDER)
@Profile({ "dev", "prod", "it", "analysis", "ncbi", "processing", "sync", "web" })
public class AnalysisExecutionServiceConfig {

	/**
	 * The order for asynchronous tasks. In particular, defines the order for
	 * methods in {@link AnalysisExecutionServiceGalaxyAsync}.
	 */
	public static final int ASYNC_ORDER = AnalysisExecutionServiceAspect.ANALYSIS_EXECUTION_ASPECT_ORDER - 1;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisService analysisService;

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
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleService;
	
	@Autowired
	private ToolsClient toolsClient;
	
	@Autowired
	private JobsClient jobsClient;
	
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
				galaxyWorkflowService, analysisWorkspaceService(), iridaWorkflowsService, analysisSubmissionSampleService);
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
				galaxyLibrariesService, iridaWorkflowsService, analysisCollectionServiceGalaxy(),
				analysisProvenanceService(), analysisParameterServiceGalaxy,
				sequencingObjectService);
	}

	@Lazy
	@Bean
	public AnalysisProvenanceServiceGalaxy analysisProvenanceService() {
		return new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService, toolsClient, jobsClient);
	}
	
	@Lazy
	@Bean
	public AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy() {
		return new AnalysisCollectionServiceGalaxy(galaxyHistoriesService);
	}
}
