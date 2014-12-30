package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;

import com.google.common.collect.Sets;

/**
 * Class used to load up test workflows.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
public class IridaWorkflowsTestConfig {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	private UUID testAnalysisDefaultId = UUID
			.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private UUID phylogenomicsPipelineDefaultId = UUID
			.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");

	@Bean
	public IridaWorkflowSet iridaWorkflows() throws IOException,
			IridaWorkflowLoadException {
		Set<IridaWorkflow> workflowsSet = iridaWorkflowLoaderService
				.loadWorkflowsForClass(TestAnalysis.class);
		workflowsSet.addAll(iridaWorkflowLoaderService
				.loadWorkflowsForClass(AnalysisPhylogenomicsPipeline.class));

		return new IridaWorkflowSet(workflowsSet);
	}

	@Bean
	public IridaWorkflowIdSet defaultIridaWorkflows() {
		return new IridaWorkflowIdSet(Sets.newHashSet(testAnalysisDefaultId,
				phylogenomicsPipelineDefaultId));
	}
}
