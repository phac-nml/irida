package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowToolRepository;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.ToolShedRepositoriesClient;
import com.github.jmchilton.blend4j.galaxy.beans.InstalledRepository;
import com.github.jmchilton.blend4j.galaxy.beans.InstalledRepository.InstallationStatus;
import com.github.jmchilton.blend4j.galaxy.beans.RepositoryInstall;

/**
 * Class used configure workflows in Galaxy for integration testing.
 * 
 *
 */
@Configuration
@Profile("test")
public class IridaWorkflowsGalaxyIntegrationTestConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsGalaxyIntegrationTestConfig.class);

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	private UUID snvPhylWorkflowId = UUID.fromString("c0e4f8c5-8a47-4a22-a42c-5256fd30526b");

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

		IridaWorkflow snvPhylWorkflow = iridaWorkflowsService.getIridaWorkflow(snvPhylWorkflowId);

		return snvPhylWorkflow;
	}
}
