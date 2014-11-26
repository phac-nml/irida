package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowIdentifier;

/**
 * Class used to load up installed workflows in IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class IridaWorkflowsService {
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;
	private String workflowResourceLocation;

	/**
	 * Stores registered workflows in the format of { workflowName ->
	 * IridaWorkflow }
	 */
	private Map<IridaWorkflowIdentifier, IridaWorkflow> registeredWorkflows;

	/**
	 * Builds a new IridaWorkflowService for loading up installed workflows.
	 * 
	 * @param workflowResourceLocation
	 *            Resource location for workflow files.
	 * @param iridaWorkflowLoaderService
	 *            The service used to load up workflows.
	 * @throws IOException
	 *             If there was an issue reading a file.
	 * @throws IridaWorkflowLoadException
	 *             If there was an issue loading a workflow.
	 */
	@Autowired
	public IridaWorkflowsService(String workflowResourceLocation, IridaWorkflowLoaderService iridaWorkflowLoaderService)
			throws IOException, IridaWorkflowLoadException {
		this.iridaWorkflowLoaderService = iridaWorkflowLoaderService;
		this.workflowResourceLocation = workflowResourceLocation;

		registeredWorkflows = new HashMap<>();

		registerWorkflows();
	}

	private void registerWorkflows() throws IOException, IridaWorkflowLoadException {
		Path resourcePath = Paths.get(IridaWorkflowsService.class.getResource(workflowResourceLocation).getFile());
		DirectoryStream<Path> stream = Files.newDirectoryStream(resourcePath);

		for (Path workflowDirectory : stream) {
			String name = workflowDirectory.toFile().getName();
			IridaWorkflowIdentifier workflowIdentifier = new IridaWorkflowIdentifier(name, "test");

			try {
				IridaWorkflow workflow = iridaWorkflowLoaderService.loadIridaWorkflow(workflowDirectory);
				registeredWorkflows.put(workflowIdentifier, workflow);
			} catch (Exception e) {
				throw new IridaWorkflowLoadException("Could not load workflow " + workflowIdentifier
						+ " from directory " + workflowDirectory);
			}
		}
	}

	/**
	 * Returns a workflow with the given name.
	 * 
	 * @param workflowName
	 *            The name of the workflow to get.
	 * @return An IridaWorkflow with the given name.
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
}
