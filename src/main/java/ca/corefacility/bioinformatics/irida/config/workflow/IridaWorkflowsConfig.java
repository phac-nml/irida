package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
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
@Profile({ "dev", "prod", "it" })
public class IridaWorkflowsConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsConfig.class);

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	/**
	 * Gets the {@link Path} for all IRIDA workflow types.
	 * 
	 * @return The {@link Path} for all IRIDA workflow types.
	 * @throws URISyntaxException
	 */
	@Bean
	public Path iridaWorkflowTypesPath() throws URISyntaxException {
		return Paths.get(AnalysisType.class.getResource("workflows").toURI());
	}

	/**
	 * Builds a set of workflows to load up into IRIDA.
	 * 
	 * @param iridaWorkflowTypesPath
	 *            The parent directory containing sub-directories for all IRIDA
	 *            workflow types.
	 * 
	 * @return A set of workflows to load into IRIDA.
	 * @throws IOException
	 *             If an I/O error occured.
	 * @throws IridaWorkflowLoadException
	 *             If there was an issue loading a specific workflow.
	 */
	@Bean
	public IridaWorkflowSet iridaWorkflows(Path iridaWorkflowTypesPath) throws IOException, IridaWorkflowLoadException {
		Set<IridaWorkflow> iridaWorkflowsSet = Sets.newHashSet();

		DirectoryStream<Path> workflowTypesStream = Files.newDirectoryStream(iridaWorkflowTypesPath);

		for (Path workflowTypePath : workflowTypesStream) {
			if (!Files.isDirectory(workflowTypePath)) {
				logger.warn("Workflow type directory " + iridaWorkflowTypesPath + " contains a file "
						+ workflowTypePath + " that is not a proper workflow directory.");
			} else {
				iridaWorkflowsSet.addAll(iridaWorkflowLoaderService.loadAllWorkflowImplementations(workflowTypePath));
			}
		}

		return new IridaWorkflowSet(iridaWorkflowsSet);
	}

	/**
	 * A set of workflow ids to use as defaults.
	 * 
	 * @return A set of workflow ids to use as defaults.
	 */
	@Bean
	public IridaWorkflowIdSet defaultIridaWorkflows() {
		return new IridaWorkflowIdSet(Sets.newHashSet());
	}
}
