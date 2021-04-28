package ca.corefacility.bioinformatics.irida.config.analysis;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.config.services.IridaPluginConfig;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.results.impl.AnalysisSubmissionSampleProcessorImpl;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.plugins.IridaPlugin;
import ca.corefacility.bioinformatics.irida.plugins.IridaPluginException;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
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
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
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
	
	private static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionServiceConfig.class);

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
	private ToolsClient toolsClient;
	
	@Autowired
	private JobsClient jobsClient;
	
	@Autowired
	private IridaPluginConfig.IridaPluginList pipelinePlugins;

	@Autowired
	private MetadataTemplateService metadataTemplateService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private List<AnalysisSampleUpdater> defaultAnalysisSampleUpdaters;

	@Autowired
	private IridaFileStorageUtility iridaFileStorageUtility;


	private List<AnalysisSampleUpdater> loadPluginAnalysisSampleUpdaters() {
		List<AnalysisSampleUpdater> pluginUpdaters = Lists.newLinkedList();

		for (IridaPlugin plugin : pipelinePlugins.getPlugins()) {
			try {
				Optional<AnalysisSampleUpdater> analysisSampleUpdaterOption = plugin.getUpdater(metadataTemplateService,
						sampleService, iridaWorkflowsService);
				if (analysisSampleUpdaterOption.isPresent()) {
					pluginUpdaters.add(analysisSampleUpdaterOption.get());
				}
			} catch (IridaPluginException e) {
				logger.error("Could not load AnalysisSampleUpdater for plugin " + plugin + ", skipping", e);
			}
		}

		return pluginUpdaters;
	}

	@Bean
	public AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor() {
		List<AnalysisSampleUpdater> analysisSampleUpdaters = Lists.newLinkedList();
		analysisSampleUpdaters.addAll(defaultAnalysisSampleUpdaters);
		analysisSampleUpdaters.addAll(loadPluginAnalysisSampleUpdaters());

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
				galaxyWorkflowService, analysisWorkspaceService(), iridaWorkflowsService, analysisSubmissionSampleProcessor());
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
				sequencingObjectService, iridaFileStorageUtility);
	}

	@Lazy
	@Bean
	public AnalysisProvenanceServiceGalaxy analysisProvenanceService() {
		return new AnalysisProvenanceServiceGalaxy(galaxyHistoriesService, toolsClient, jobsClient);
	}
	
	@Lazy
	@Bean
	public AnalysisCollectionServiceGalaxy analysisCollectionServiceGalaxy() {
		return new AnalysisCollectionServiceGalaxy(galaxyHistoriesService, iridaFileStorageUtility);
	}
}
