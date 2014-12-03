package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.integration.TestAnalysis;

/**
 * Class used to load up test workflows.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Configuration
@Profile("test")
public class IridaWorkflowsServiceTestConfig {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	@Bean
	public IridaWorkflowsService iridaWorkflowsService() throws IOException, IridaWorkflowLoadException {
		IridaWorkflowsService workflowsService = new IridaWorkflowsService(iridaWorkflowLoaderService);
		workflowsService.registerAnalysis(TestAnalysis.class, UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6"));

		return workflowsService;
	}
}
