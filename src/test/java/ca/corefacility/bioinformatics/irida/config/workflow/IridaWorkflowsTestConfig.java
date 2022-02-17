package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.config.AnalysisTypeSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Class used to load up test workflows.
 * 
 *
 */
@TestConfiguration
public class IridaWorkflowsTestConfig {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	private UUID testAnalysisDefaultId = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private UUID phylogenomicsPipelineDefaultId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");
	private UUID assemblyAnnotationPipelineDefaultId = UUID.fromString("8c438951-484a-48da-be2b-93b7d29aa2a3");

	@Bean
	public IridaWorkflowSet iridaWorkflows() throws IOException, IridaWorkflowLoadException, URISyntaxException {
		Path testAnalysisPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis").toURI());
		Path phylogenomicsAnalysisPath = Paths
				.get(Analysis.class.getResource("workflows/AnalysisPhylogenomicsPipeline").toURI());
		Path assemblyAnnotationPath = Paths
				.get(Analysis.class.getResource("workflows/AnalysisAssemblyAnnotation").toURI());

		Set<IridaWorkflow> workflowsSet = iridaWorkflowLoaderService.loadAllWorkflowImplementations(testAnalysisPath);
		workflowsSet.addAll(iridaWorkflowLoaderService.loadAllWorkflowImplementations(phylogenomicsAnalysisPath));
		workflowsSet.addAll(iridaWorkflowLoaderService.loadAllWorkflowImplementations(assemblyAnnotationPath));

		return new IridaWorkflowSet(workflowsSet);
	}

	@Bean
	public IridaWorkflowIdSet defaultIridaWorkflows() {
		return new IridaWorkflowIdSet(Sets.newHashSet(testAnalysisDefaultId, phylogenomicsPipelineDefaultId,
				assemblyAnnotationPipelineDefaultId));
	}

	@Bean
	public AnalysisTypeSet disabledAnalysisTypes() {
		return new AnalysisTypeSet();
	}

	@Bean
	public IridaWorkflowsService iridaWorkflowsService(IridaWorkflowSet iridaWorkflows,
			IridaWorkflowIdSet defaultIridaWorkflows, AnalysisTypeSet disabledAnalysisTypes)
			throws IridaWorkflowException {
		return new IridaWorkflowsService(iridaWorkflows, defaultIridaWorkflows, disabledAnalysisTypes);
	}
}
