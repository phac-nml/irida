package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowDefaultException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Class used to load up installed workflows in IRIDA.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class IridaWorkflowsService {
	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowsService.class);

	/**
	 * Stores registered workflows within IRIDA.
	 */
	private Map<UUID, IridaWorkflow> allRegisteredWorkflows;

	/**
	 * Stores the id of a default workflow for an analysis type.
	 */
	private Map<AnalysisType, UUID> defaultWorkflowForAnalysis;

	/**
	 * Builds a new {@link IridaWorkflowService} for loading up installed
	 * workflows.
	 * 
	 * @param iridaWorkflows
	 *            A {@link IridaWorkflowSet} of {@link IridaWorkflow}s to use in IRIDA.
	 * @param defaultIridaWorkflows
	 *            A {@link IridaWorkflowIdSet} of {@link UUID}s to use as the default
	 *            workflows.
	 * @throws IridaWorkflowException
	 *             If there was an issue when attempting to register the
	 *             workflows.
	 */
	@Autowired
	public IridaWorkflowsService(IridaWorkflowSet iridaWorkflows, IridaWorkflowIdSet defaultIridaWorkflows)
			throws IridaWorkflowException {
		checkNotNull(iridaWorkflows, "iridaWorkflows is null");
		checkNotNull(defaultIridaWorkflows, "defaultWorkflows is null");

		allRegisteredWorkflows = new HashMap<>();
		defaultWorkflowForAnalysis = new HashMap<>();

		registerWorkflows(iridaWorkflows.getIridaWorkflows());
		setDefaultWorkflows(defaultIridaWorkflows.getIridaWorkflowIds());
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
		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription().getAnalysisType();
		if (defaultWorkflowForAnalysis.containsKey(analysisType)) {
			throw new IridaWorkflowDefaultException("Cannot set workflow " + workflowId
					+ " as default, already exists default workflow for \"" + analysisType + "\"");
		} else {
			defaultWorkflowForAnalysis.put(analysisType, workflowId);
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
		checkNotNull(iridaWorkflow.getWorkflowDescription().getAnalysisType(), "analysisType is null");

		UUID workflowId = iridaWorkflow.getWorkflowDescription().getId();

		logger.debug("Registering workflow: " + iridaWorkflow);
		if (allRegisteredWorkflows.containsKey(workflowId)) {
			throw new IridaWorkflowException("Duplicate workflow " + workflowId);
		} else {
			allRegisteredWorkflows.put(workflowId, iridaWorkflow);
		}
	}

	/**
	 * Gets the default workflow for a given type of analysis.
	 * 
	 * @param analysisType
	 *            The type of analysis to search for.
	 * @return An {@link IridaWorkflow} for this analysis type.
	 * @throws IridaWorkflowNotFoundException
	 */
	public IridaWorkflow getDefaultWorkflowByType(AnalysisType analysisType)
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
	 * Gets all of the default workflows for a given {@link Set} of
	 * {@link AnalysisType}s.
	 * 
	 * @param analysisTypes
	 *            A {@link Set} of {@link AnalysisType}s.
	 * @return A {@link Map} of {@link AnalysisType} to {@link IridaWorkflow}
	 *         all the passed analysis types.
	 * @throws IridaWorkflowNotFoundException
	 *             If one of the analysis types does not have any associated
	 *             workflows.
	 */
	public Map<AnalysisType, IridaWorkflow> getAllDefaultWorkflowsByType(Set<AnalysisType> analysisTypes)
			throws IridaWorkflowNotFoundException {
		checkNotNull(analysisTypes, "analysisTypes is null");

		Map<AnalysisType, IridaWorkflow> analysisTypeWorkflowsMap = Maps.newHashMap();
		for (AnalysisType analysisType : analysisTypes) {
			analysisTypeWorkflowsMap.put(analysisType, getDefaultWorkflowByType(analysisType));
		}

		return analysisTypeWorkflowsMap;
	}

	/**
	 * Gets all the workflows for a given {@link AnalysisType}.
	 * 
	 * @param analysisType
	 *            The {@link AnalysisType} to search for workflows.
	 * @return A {@link Set} of {@link IridaWorkflow}s for this analysis type.
	 * @throws IridaWorkflowNotFoundException
	 *             If not corresponding workflows could not be found.
	 */
	public Set<IridaWorkflow> getAllWorkflowsByType(AnalysisType analysisType) throws IridaWorkflowNotFoundException {
		checkNotNull(analysisType, "analysisType is null");

		Set<IridaWorkflow> workflowsByType = getRegisteredWorkflows()
				.stream()
				.filter((iridaWorkflow) -> analysisType
						.equals(iridaWorkflow.getWorkflowDescription().getAnalysisType())).collect(Collectors.toSet());

		if (workflowsByType.isEmpty()) {
			throw new IridaWorkflowNotFoundException("No registered workflows for type " + analysisType);
		} else {
			return workflowsByType;
		}
	}
	
	/**
	 * Gets a {@link Set} of all registered {@link AnalysisType} for all workflows.
	 * 
	 * @return A {@link Set} of all the types of all installed workflows.
	 */
	public Set<AnalysisType> getRegisteredWorkflowTypes() {
		Set<AnalysisType> types = new HashSet<>();

		for (IridaWorkflow workflow : getRegisteredWorkflows()) {
			types.add(workflow.getWorkflowDescription().getAnalysisType());
		}

		return types;
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
	 * Gets a {@link Set} of all installed workflows.
	 * 
	 * @return A {@link Set} of all installed workflows.
	 */
	public Set<IridaWorkflow> getRegisteredWorkflows() {
		return Sets.newHashSet(allRegisteredWorkflows.values());
	}
}
