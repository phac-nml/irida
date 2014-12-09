package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowDefaultException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Class used to load up installed workflows in IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class IridaWorkflowsService {
	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsService.class);
	
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
	 */
	@Autowired
	public IridaWorkflowsService() {

		allRegisteredWorkflows = new HashMap<>();
		registeredWorkflowsForAnalysis = new HashMap<>();
		defaultWorkflowForAnalysis = new HashMap<>();
		workflowNamesMap = new HashMap<>();
	}

	/**
	 * Sets the given workflow as a default workflow for it's analysis type.
	 * 
	 * @param workflowId
	 *            The workflow id to set as default.
	 * @throws IridaWorkflowNotFoundException
	 *             If the given workflow cannot be found.
	 * @throws IridaWorkflowDefaultException
	 *             If the corresponding workflow type already has a default
	 *             workflow set.
	 */
	public void setDefaultWorkflow(UUID workflowId) throws IridaWorkflowNotFoundException,
			IridaWorkflowDefaultException {
		checkNotNull(workflowId, "workflowId is null");

		IridaWorkflow iridaWorkflow = getIridaWorkflow(workflowId);
		Class<? extends Analysis> analysisClass = iridaWorkflow.getWorkflowDescription().getAnalysisClass();
		if (defaultWorkflowForAnalysis.containsKey(analysisClass)) {
			throw new IridaWorkflowDefaultException("Cannot set workflow " + workflowId
					+ " as default, already exists default workflow for " + analysisClass);
		} else {
			defaultWorkflowForAnalysis.put(analysisClass, workflowId);
		}
	}

	/**
	 * Sets the given set of workflow ids as default workflows.
	 * 
	 * @param defaultWorkflows
	 *            The set of workflow ids to set as defaults.
	 * @throws IridaWorkflowNotFoundException
	 *             If one of the workflow ids has no corresponding workflow.
	 * @throws IridaWorkflowDefaultException
	 *             If there was an issue setting a default workflow.
	 */
	public void setDefaultWorkflows(Set<UUID> defaultWorkflows) throws IridaWorkflowNotFoundException,
			IridaWorkflowDefaultException {
		checkNotNull(defaultWorkflows, "iridaWorkflows is null");

		for (UUID workflowId : defaultWorkflows) {
			setDefaultWorkflow(workflowId);
		}
	}

	/**
	 * Registers the set of workflows with IRIDA.
	 * 
	 * @param iridaWorkflows
	 *            The set of workflows to register.
	 * @throws IridaWorkflowException
	 *             If there was an issue registering a workflow.
	 */
	public void registerWorkflows(Set<IridaWorkflow> iridaWorkflows) throws IridaWorkflowException {
		checkNotNull(iridaWorkflows, "iridaWorkflows is null");

		for (IridaWorkflow iridaWorkflow : iridaWorkflows) {
			registerWorkflow(iridaWorkflow);
		}
	}

	/**
	 * Registers the given workflow with this service.
	 * 
	 * @param iridaWorkflow
	 *            The workflow to register.
	 * @throws IridaWorkflowException
	 *             If there was an issue when registering the workflow.
	 */
	public void registerWorkflow(IridaWorkflow iridaWorkflow) throws IridaWorkflowException {
		checkNotNull(iridaWorkflow, "iridaWorkflow is null");

		Class<? extends Analysis> analysisClass = iridaWorkflow.getWorkflowDescription().getAnalysisClass();
		UUID workflowId = iridaWorkflow.getWorkflowDescription().getId();
		String workflowName = iridaWorkflow.getWorkflowDescription().getName();

		logger.debug("Registering workflow: " + iridaWorkflow);
		if (allRegisteredWorkflows.containsKey(workflowId)) {
			throw new IridaWorkflowException("Duplicate workflow " + workflowId);
		} else {
			allRegisteredWorkflows.put(workflowId, iridaWorkflow);
			addWorkflowForAnalysis(analysisClass, workflowId);
			addWorkflowNameToAnalysis(workflowName, analysisClass);
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
