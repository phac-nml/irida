package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Class used configure workflows in Galaxy for integration testing.
 * 
 *
 */
@TestConfiguration
public class IridaWorkflowsGalaxyIntegrationTestConfig {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	@Value("${irida.workflow.default.PHYLOGENOMICS}")
	private String snvPhylWorkflowId;

	/**
	 * Registers a production SNVPhyl workflow for testing.
	 * 
	 * @return A production {@link IridaWorkflow} for testing.
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws IridaWorkflowException
	 */
	@Lazy
	@Bean
	public IridaWorkflow snvPhylWorkflow() throws IOException, URISyntaxException, IridaWorkflowException {
		Path snvPhylProductionPath = Paths.get(AnalysisType.class.getResource("workflows/SNVPhyl").toURI());

		Set<IridaWorkflow> snvPhylWorkflows = iridaWorkflowLoaderService
				.loadAllWorkflowImplementations(snvPhylProductionPath);
		iridaWorkflowsService.registerWorkflows(snvPhylWorkflows);

		IridaWorkflow snvPhylWorkflow = iridaWorkflowsService.getIridaWorkflow(UUID.fromString(snvPhylWorkflowId));

		return snvPhylWorkflow;
	}
}
