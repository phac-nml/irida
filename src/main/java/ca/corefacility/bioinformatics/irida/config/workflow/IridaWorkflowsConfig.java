package ca.corefacility.bioinformatics.irida.config.workflow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.enums.config.AnalysisTypeSet;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Class used to load up test workflows.
 * 
 *
 */
@Configuration
public class IridaWorkflowsConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsConfig.class);

	private static final String IRIDA_DEFAULT_WORKFLOW_PREFIX = "irida.workflow.default";
	private static final String IRIDA_DISABLED_TYPES = "irida.workflow.types.disabled";

	@Autowired
	private Environment environment;

	/**
	 * Gets the {@link Path} for all IRIDA workflow types.
	 * 
	 * @return The {@link Path} for all IRIDA workflow types.
	 * @throws URISyntaxException
	 *             if the path is not a valid uri.
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
				iridaWorkflowsSet.addAll(iridaWorkflowLoaderService().loadAllWorkflowImplementations(workflowTypePath));
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
		Set<UUID> defaultWorkflowIds = Sets.newHashSet();

		for (AnalysisType analysisType : AnalysisType.values()) {
			String analysisDefaultProperyName = IRIDA_DEFAULT_WORKFLOW_PREFIX + "." + analysisType;

			logger.trace("Getting default workflow id from property '" + analysisDefaultProperyName + "'");
			String analysisDefaultId = environment.getProperty(analysisDefaultProperyName);
			if (analysisDefaultId == null) {
				logger.warn("No default workflow id associated with property '" + analysisDefaultProperyName + "'");
			} else {
				try {
					UUID id = UUID.fromString(analysisDefaultId);
					logger.debug("Adding default workflow " + analysisDefaultProperyName + "=" + analysisDefaultId);
					defaultWorkflowIds.add(id);
				} catch (IllegalArgumentException e) {
					logger.error("Default workflow id for " + analysisDefaultProperyName + "=" + analysisDefaultId
							+ " is not a valid workflow id");
				}
			}
		}

		return new IridaWorkflowIdSet(defaultWorkflowIds);
	}

	/**
	 * Sets up an {@link Unmarshaller} for workflow objects.
	 * 
	 * @return An {@link Unmarshaller} for workflow objects.
	 */
	@Bean
	public Unmarshaller workflowDescriptionUnmarshaller() {
		Jaxb2Marshaller jaxb2marshaller = new Jaxb2Marshaller();
		jaxb2marshaller.setPackagesToScan(new String[] { "ca.corefacility.bioinformatics.irida.model.workflow" });
		return jaxb2marshaller;
	}

	/**
	 * Constructs a service for loading up workflows for IRIDA.
	 * 
	 * @return A service for loading workflows for IRIDA.
	 */
	@Bean
	public IridaWorkflowLoaderService iridaWorkflowLoaderService() {
		return new IridaWorkflowLoaderService(workflowDescriptionUnmarshaller());
	}

	/**
	 * Builds a {@link AnalysisTypeSet} of {@link AnalysisType}s which are to be disabled from
	 * the UI.
	 * 
	 * @return A {@link AnalysisTypeSet} of {@link AnalysisType}s which are to be disabled from the UI.
	 */
	@Bean
	public AnalysisTypeSet disabledAnalysisTypes() {
		String[] disabledWorkflowTypes = environment.getProperty(IRIDA_DISABLED_TYPES, String[].class);
		return new AnalysisTypeSet(Sets.newHashSet(disabledWorkflowTypes).stream().map(t -> AnalysisType.fromString(t))
				.collect(Collectors.toSet()));
	}

	/**
	 * Builds a new {@link IridaWorkflowsService}.
	 * 
	 * @param iridaWorkflows        The set of IridaWorkflows to use.
	 * @param defaultIridaWorkflows The set of ids for default workflows to use.
	 * @param disabledAnalysisTypes The set of disabled {@link AnalysisType}s.
	 * @return A new {@link IridaWorkflowsService}.
	 * @throws IridaWorkflowException If there was an error loading a workflow.
	 */
	@Bean
	public IridaWorkflowsService iridaWorkflowsService(IridaWorkflowSet iridaWorkflows,
			IridaWorkflowIdSet defaultIridaWorkflows, AnalysisTypeSet disabledAnalysisTypes)
			throws IridaWorkflowException {
		return new IridaWorkflowsService(iridaWorkflows, defaultIridaWorkflows, disabledAnalysisTypes);
	}
}
