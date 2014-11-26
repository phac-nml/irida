package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowIdentifier;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Class used to load up installed workflows in IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class IridaWorkflowsService {
	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsService.class);

	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	private static final String WORKFLOW_DIR = "workflows";

	/**
	 * Stores registered workflows in the format of { workflowName ->
	 * IridaWorkflow }
	 */
	private Map<IridaWorkflowIdentifier, IridaWorkflow> registeredWorkflows;

	/**
	 * Builds a new IridaWorkflowService for loading up installed workflows.
	 * 
	 * @param iridaWorkflowLoaderService
	 *            The service used to load up workflows.
	 * @throws IOException
	 *             If there was an issue reading a file.
	 * @throws IridaWorkflowLoadException
	 *             If there was an issue loading a workflow.
	 */
	@Autowired
	public IridaWorkflowsService(IridaWorkflowLoaderService iridaWorkflowLoaderService) throws IOException,
			IridaWorkflowLoadException {
		this.iridaWorkflowLoaderService = iridaWorkflowLoaderService;

		registeredWorkflows = new HashMap<>();

		registerWorkflows(AnalysisSubmission.class);
	}

	/**
	 * Registers workflows that are stored as resources belonging to the passed
	 * class.
	 * 
	 * @param analysisRootClass
	 *            The class defining where the workflow files are stored.
	 * @throws IOException
	 *             If there was a problem reading a workflow.
	 * @throws IridaWorkflowLoadException
	 *             If there was a problem loading a workflow.
	 */
	private void registerWorkflows(Class<?> analysisRootClass) throws IOException, IridaWorkflowLoadException {
		Path resourcePath = Paths.get(analysisRootClass.getResource(WORKFLOW_DIR).getFile());
		DirectoryStream<Path> stream = Files.newDirectoryStream(resourcePath);

		for (Path workflowDirectory : stream) {
			if (!Files.isDirectory(workflowDirectory)) {
				logger.warn("Workflow directory " + resourcePath + " contains a file " + workflowDirectory
						+ " that is not a proper workflow directory");
			} else {

				try {
					Set<IridaWorkflow> workflowVersions = iridaWorkflowLoaderService
							.loadAllWorkflowVersions(workflowDirectory);

					for (IridaWorkflow workflow : workflowVersions) {
						registeredWorkflows.put(workflow.getWorkflowIdentifier(), workflow);
					}
				} catch (Exception e) {
					throw new IridaWorkflowLoadException(
							"Could not load workflows from directory " + workflowDirectory, e);
				}
			}
		}
	}

	/**
	 * Returns a workflow with the given {@link IridaWorkflowIdentifier}.
	 * 
	 * @param workflowIdentifier
	 *            The identifier of the workflow to get.
	 * @return An IridaWorkflow with the given identifier.
	 * @throws IridaWorkflowNotFoundException
	 *             If no workflow with the given identifier was found.
	 */
	public IridaWorkflow loadIridaWorkflow(IridaWorkflowIdentifier workflowIdentifier)
			throws IridaWorkflowNotFoundException {
		checkNotNull(workflowIdentifier, "workflowIdentifier is null");

		if (registeredWorkflows.containsKey(workflowIdentifier)) {
			return registeredWorkflows.get(workflowIdentifier);
		} else {
			throw new IridaWorkflowNotFoundException(workflowIdentifier);
		}
	}

	/**
	 * Gets a Collection of all installed workflows.
	 * 
	 * @return A collection of all installed workflows.
	 */
	public Collection<IridaWorkflow> getInstalledWorkflows() {
		return registeredWorkflows.values();
	}
}
