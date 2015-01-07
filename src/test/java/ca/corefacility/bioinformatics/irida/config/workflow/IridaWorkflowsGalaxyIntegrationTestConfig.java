package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Class used configure workflows in Galaxy for integration testing.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
public class IridaWorkflowsGalaxyIntegrationTestConfig {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;
	
	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	private UUID snvPhylWorkflowId = UUID.fromString("ccca532d-b0be-4f2c-bd6d-9886aa722571");

	/**
	 * Registers a production SNVPhyl workflow for testing.
	 * @return  A production {@link IridaWorkflow} for testing.
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws IridaWorkflowException
	 */
	@Bean
	public IridaWorkflow snvPhylWorkflow() throws IOException, URISyntaxException, IridaWorkflowException {
		Path snvPhylProductionPath = Paths.get(AnalysisType.class.getResource("workflows/SNVPhyl").toURI());

		Set<IridaWorkflow> snvPhylWorkflows = iridaWorkflowLoaderService.loadAllWorkflowImplementations(snvPhylProductionPath);
		iridaWorkflowsService.registerWorkflows(snvPhylWorkflows);
		
		return iridaWorkflowsService.getIridaWorkflow(snvPhylWorkflowId);
	}
}
