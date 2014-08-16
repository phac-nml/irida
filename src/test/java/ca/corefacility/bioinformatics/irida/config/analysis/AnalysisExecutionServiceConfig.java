package ca.corefacility.bioinformatics.irida.config.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.ToolsClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;

import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.analysis.prepration.galaxy.phylogenomics.impl.GalaxyWorkflowPreparationServicePhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.service.analysis.submission.galaxy.phylogenomics.impl.AnalysisSubmissionServiceGalaxyPhylogenomicsPipeline;

/**
 * Test configuration for AnalysisExecutionService classes.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
@Conditional(NonWindowsPlatformCondition.class)
public class AnalysisExecutionServiceConfig {

	@Autowired
	private LocalGalaxy localGalaxy;
	
	@Bean
	public AnalysisSubmissionServiceGalaxyPhylogenomicsPipeline
		analysisExecutionServiceGalaxyPhylogenomicsPipeline(
				GalaxyHistoriesService galaxyHistoriesService, GalaxyWorkflowService galaxyWorkflowService) {
		
		GalaxyWorkflowPreparationServicePhylogenomicsPipeline galaxyWorkflowPreparationServicePhylogenomicsPipeline = 
				new GalaxyWorkflowPreparationServicePhylogenomicsPipeline(galaxyHistoriesService, galaxyWorkflowService);
		return new AnalysisSubmissionServiceGalaxyPhylogenomicsPipeline(galaxyWorkflowService,
				galaxyHistoriesService, galaxyWorkflowPreparationServicePhylogenomicsPipeline);
	}
	
	@Bean
	public GalaxyHistoriesService galaxyHistoriesService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		ToolsClient toolsClient = localGalaxy.getGalaxyInstanceAdmin().getToolsClient();
		return new GalaxyHistoriesService(historiesClient, toolsClient);
	}
	
	@Bean
	public GalaxyWorkflowService galaxyWorkflowService() {
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceAdmin().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceAdmin().getWorkflowsClient();
		
		return new GalaxyWorkflowService(historiesClient, workflowsClient,
						new StandardPasswordEncoder());
	}
}
