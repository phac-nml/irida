package ca.corefacility.bioinformatics.irida.config.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;

import ca.corefacility.bioinformatics.irida.model.workflow.manager.galaxy.ExecutionManagerGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.WorkspaceServicePhylogenomics;

/**
 * Configuration for an AnalysisExecutionService class.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile({ "dev", "prod", "it" })
public class AnalysisExecutionServiceConfig {
	
	@Autowired
	private ExecutionManagerGalaxy executionManager;
	
	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	
	@Autowired
	private AnalysisService analysisService;
	
	/**
	 * Builds a new AnalysisExecutionServicePhylogenomics which can be used for launching
	 *  phylogenomics analyses.
	 * @return  A AnalysisExecutionServicePhylogenomics for launching phylogenomics analyeses.
	 */
	@Lazy @Bean
	public AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics() {
		return new AnalysisExecutionServicePhylogenomics(analysisSubmissionRepository,
				analysisService, galaxyWorkflowService(), galaxyHistoriesService(),
				workspaceService());
	}

	@Lazy @Bean
	public WorkspaceServicePhylogenomics workspaceService() {
		return new WorkspaceServicePhylogenomics(galaxyHistoriesService(), galaxyWorkflowService());
	}

	@Lazy @Bean
	public GalaxyWorkflowService galaxyWorkflowService() {
		return new GalaxyWorkflowService(historiesClient(), workflowsClient(), passwordEncoder());
	}

	@Lazy @Bean
	public PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}

	@Lazy @Bean
	public WorkflowsClient workflowsClient() {
		return galaxyInstance().getWorkflowsClient();
	}

	@Lazy @Bean
	public GalaxyHistoriesService galaxyHistoriesService() {
		return new GalaxyHistoriesService(historiesClient(), toolsClient());
	}
	
	@Lazy @Bean
	public ToolsClient toolsClient() {
		return galaxyInstance().getToolsClient();
	}

	@Lazy @Bean
	public HistoriesClient historiesClient() {
		return galaxyInstance().getHistoriesClient();
	}
	
	@Lazy @Bean
	public GalaxyInstance galaxyInstance() {
		return GalaxyInstanceFactory.get(executionManager.getLocation().toString(),
				executionManager.getAPIKey());
	}
}
