package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Class used to load up installed workflows in IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
//@Service
public class IridaWorkflowsService {
	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsService.class);

	private static final String WORKFLOWS_DIR = "workflows";

	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	/**
	 * Stores registered workflows within IRIDA.
	 */
	private Map<UUID, IridaWorkflow> allRegisteredWorkflows;

	/**
	 * Stores registered workflows for a particular analysis.
	 */
	private Map<Class<? extends Analysis>, Set<UUID>> registeredWorkflowsForAnalysis;

	/**
	 * Stores the id of a default workflow for an analysis.
	 */
	private Map<Class<? extends Analysis>, UUID> defaultWorkflowForAnalysis;

	/**
	 * Stores map of workflow names to ids.
	 */
	private Map<String, Class<? extends Analysis>> workflowNamesMap;

	/**
	 * Builds a new {@link IridaWorkflowService} for loading up installed
	 * workflows.
	 * 
	 * @param iridaWorkflowLoaderService
	 *            The service used to load up workflows.
	 */
	@Autowired
	public IridaWorkflowsService(IridaWorkflowLoaderService iridaWorkflowLoaderService) {
		this.iridaWorkflowLoaderService = iridaWorkflowLoaderService;

		allRegisteredWorkflows = new HashMap<>();
		registeredWorkflowsForAnalysis = new HashMap<>();
		defaultWorkflowForAnalysis = new HashMap<>();
		workflowNamesMap = new HashMap<>();
	}

	/**
	 * Registers workflows that are stored as resources belonging to the passed
	 * Analysis class.
	 * 
	 * @param analysisClass
	 *            The class defining the type of analysis.
	 * @param defaultWorkflowId
	 *            The default id of the workflow for this analysis.
	 * @throws IOException
	 *             If there was a problem reading a workflow.
	 * @throws IridaWorkflowLoadException
	 *             If there was a problem loading a workflow.
	 */
	public void registerAnalysis(Class<? extends Analysis> analysisClass, UUID defaultWorkflowId) throws IOException,
			IridaWorkflowLoadException {
		checkNotNull(analysisClass, "analysisClass is null");
		checkNotNull(defaultWorkflowId, "defaultWorkflowId is null");

		logger.debug("Registering Analysis: " + analysisClass);
		String analysisName = analysisClass.getSimpleName();
		Path workflowsResourcePath = Paths.get(analysisClass.getResource(WORKFLOWS_DIR).getFile());
		Path workflowPath = workflowsResourcePath.resolve(analysisName);

		if (!Files.isDirectory(workflowPath)) {
			throw new IridaWorkflowLoadException("Missing directory " + workflowPath + " for class " + analysisClass);
		} else {
			try {
				Set<IridaWorkflow> workflowVersions = iridaWorkflowLoaderService
						.loadAllWorkflowImplementations(workflowPath);

				for (IridaWorkflow workflow : workflowVersions) {
					if (allRegisteredWorkflows.containsKey(workflow.getWorkflowIdentifier())) {
						throw new IridaWorkflowLoadException("Duplicate workflow " + workflow.getWorkflowIdentifier());
					} else {
						allRegisteredWorkflows.put(workflow.getWorkflowIdentifier(), workflow);
						addWorkflowForAnalysis(analysisClass, workflow.getWorkflowIdentifier());
						addWorkflowNameToAnalysis(workflow.getWorkflowDescription().getName(), analysisClass);
					}
				}
			} catch (Exception e) {
				throw new IridaWorkflowLoadException("Could not load workflows from directory " + workflowPath, e);
			}

			if (!registeredWorkflowsForAnalysis.get(analysisClass).contains(defaultWorkflowId)) {
				throw new IridaWorkflowLoadException("No workflow with id " + defaultWorkflowId + " vailable for "
						+ analysisClass);
			} else {
				defaultWorkflowForAnalysis.put(analysisClass, defaultWorkflowId);
			}
		}
	}

	private void addWorkflowNameToAnalysis(String workflowName, Class<? extends Analysis> analysisType) {
		if (!workflowNamesMap.containsKey(workflowName)) {
			workflowNamesMap.put(workflowName, analysisType);
		}
	}

	private void addWorkflowForAnalysis(Class<? extends Analysis> analysisType, UUID id) {
		if (!registeredWorkflowsForAnalysis.containsKey(analysisType)) {
			registeredWorkflowsForAnalysis.put(analysisType, new HashSet<>());
		}

		registeredWorkflowsForAnalysis.get(analysisType).add(id);
	}

	/**
	 * Gets the default workflow for a given type of analysis.
	 * 
	 * @param analysisType
	 *            The type of analysis to search for.
	 * @return An {@link IridaWorkflow} for this analysis type.
	 * @throws IridaWorkflowNotFoundException
	 */
	public IridaWorkflow getDefaultWorkflow(Class<? extends Analysis> analysisType)
			throws IridaWorkflowNotFoundException {
		checkNotNull(analysisType, "analysisType is null");

		if (!defaultWorkflowForAnalysis.containsKey(analysisType)) {
			throw new IridaWorkflowNotFoundException(analysisType);
		} else {
			UUID id = defaultWorkflowForAnalysis.get(analysisType);
			return allRegisteredWorkflows.get(id);
		}
	}

	/**
	 * Gets the default workflow for a workflow with the given name.
	 * 
	 * @param workflowName
	 *            The name of the workflow to search.
	 * @return A default implementing workflow with this name.
	 * @throws IridaWorkflowNotFoundException
	 *             If no corresponding workflow was found.
	 */
	public IridaWorkflow getDefaultWorkflow(String workflowName) throws IridaWorkflowNotFoundException {
		checkNotNull(workflowName, "workflowName is null");

		if (!workflowNamesMap.containsKey(workflowName)) {
			throw new IridaWorkflowNotFoundException(workflowName);
		} else {
			Class<? extends Analysis> analysisType = workflowNamesMap.get(workflowName);
			return getDefaultWorkflow(analysisType);
		}
	}

	/**
	 * Gets all the workflows for a given workflow name.
	 * 
	 * @param workflowName
	 *            The name of workflow to search.
	 * @return A Set of {@link IridaWorkflow} for this workflow name.
	 * @throws IridaWorkflowNotFoundException
	 *             If not corresponding workflows could be found.
	 */
	public Set<IridaWorkflow> getAllWorkflowsByName(String workflowName) throws IridaWorkflowNotFoundException {
		checkNotNull(workflowName);

		if (!workflowNamesMap.containsKey(workflowName)) {
			throw new IridaWorkflowNotFoundException(workflowName);
		} else {
			Class<? extends Analysis> analysisType = workflowNamesMap.get(workflowName);
			return getAllWorkflowsByClass(analysisType);
		}
	}

	/**
	 * Gets all the workflows for a given analysis type.
	 * 
	 * @param analysisType
	 *            The type of analysis to search for workflows.
	 * @return A Set of {@link IridaWorkflow} for this analysis type.
	 * @throws IridaWorkflowNotFoundException
	 *             If not corresponding workflows could be found.
	 */
	public Set<IridaWorkflow> getAllWorkflowsByClass(Class<? extends Analysis> analysisType)
			throws IridaWorkflowNotFoundException {
		checkNotNull(analysisType, "analysisType is null");

		Set<IridaWorkflow> workflowsSet = new HashSet<>();

		if (!registeredWorkflowsForAnalysis.containsKey(analysisType)) {
			throw new IridaWorkflowNotFoundException(analysisType);
		} else {
			for (UUID id : registeredWorkflowsForAnalysis.get(analysisType)) {
				workflowsSet.add(allRegisteredWorkflows.get(id));
			}

			return workflowsSet;
		}
	}

	/**
	 * Gets a list of all the names of all installed workflows.
	 * 
	 * @return A list of all the names of all installed workflows.
	 */
	public Set<String> getAllWorkflowNames() {
		Set<String> names = new HashSet<>();

		for (IridaWorkflow workflow : getInstalledWorkflows()) {
			names.add(workflow.getWorkflowDescription().getName());
		}

		return names;
	}

	/**
	 * Returns a workflow with the given id.
	 * 
	 * @param workflowId
	 *            The identifier of the workflow to get.
	 * @return An {@link IridaWorkflow} with the given identifier.
	 * @throws IridaWorkflowNotFoundException
	 *             If no workflow with the given identifier was found.
	 */
	public IridaWorkflow getIridaWorkflow(UUID workflowId) throws IridaWorkflowNotFoundException {
		checkNotNull(workflowId, "workflowId is null");

		if (allRegisteredWorkflows.containsKey(workflowId)) {
			return allRegisteredWorkflows.get(workflowId);
		} else {
			throw new IridaWorkflowNotFoundException(workflowId);
		}
	}

	/**
	 * Gets a Collection of all installed workflows.
	 * 
	 * @return A collection of all installed workflows.
	 */
	public Collection<IridaWorkflow> getInstalledWorkflows() {
		return allRegisteredWorkflows.values();
	}
}
