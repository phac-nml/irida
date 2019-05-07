package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowDefaultException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotDisplayableException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.config.AnalysisTypeSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowOutput;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Class used to load up installed workflows in IRIDA.
 * 
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
	
	private AnalysisTypeSet disabledAnalysisTypes;
	
	/**
	 * Builds a new {@link IridaWorkflowsService} for loading up installed
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
	public IridaWorkflowsService(IridaWorkflowSet iridaWorkflows, IridaWorkflowIdSet defaultIridaWorkflows)
			throws IridaWorkflowException {
		this(iridaWorkflows, defaultIridaWorkflows, new AnalysisTypeSet());
	}

	/**
	 * Builds a new {@link IridaWorkflowsService} for loading up installed
	 * workflows.
	 * 
	 * @param iridaWorkflows        A {@link IridaWorkflowSet} of
	 *                              {@link IridaWorkflow}s to use in IRIDA.
	 * @param defaultIridaWorkflows A {@link IridaWorkflowIdSet} of {@link UUID}s to
	 *                              use as the default workflows.
	 * @param disabledAnalysisTypes A {@link Set} of disabled {@link AnalysisType}s.
	 * @throws IridaWorkflowException If there was an issue when attempting to
	 *                                register the workflows.
	 */
	@Autowired
	public IridaWorkflowsService(IridaWorkflowSet iridaWorkflows, IridaWorkflowIdSet defaultIridaWorkflows, AnalysisTypeSet disabledAnalysisTypes)
			throws IridaWorkflowException {
		checkNotNull(iridaWorkflows, "iridaWorkflows is null");
		checkNotNull(defaultIridaWorkflows, "defaultWorkflows is null");

		allRegisteredWorkflows = new HashMap<>();
		defaultWorkflowForAnalysis = new HashMap<>();

		registerWorkflows(iridaWorkflows.getIridaWorkflows());
		setDefaultWorkflows(defaultIridaWorkflows.getIridaWorkflowIds());

		this.disabledAnalysisTypes = disabledAnalysisTypes;
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
		checkNotNull(iridaWorkflow.getWorkflowDescription().getAnalysisType().getType(), "analysisType name is null");

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
	 *             if the workflow could not be found for the specified analysis
	 *             type.
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
	 * Gets a {@link Set} of disabled {@link AnalysisType}s.
	 * 
	 * @return A {@link Set} of disabled {@link AnalysisType}s.
	 */
	public Set<AnalysisType> getDisplayableWorkflowTypes() {
		return Sets.difference(getRegisteredWorkflowTypes(), disabledAnalysisTypes.getAnalysisTypes());
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
	 * Returns a workflow with the given id that is displayable.
	 * 
	 * @param workflowId The identifier of the workflow to get.
	 * @return An {@link IridaWorkflow} with the given identifier that is
	 *         displayable.
	 * @throws IridaWorkflowNotDisplayableException If no workflow with the given
	 *                                              identifier is not displayable.
	 * @throws IridaWorkflowNotFoundException       If the workflow was not found.
	 */
	public IridaWorkflow getDisplayableIridaWorkflow(UUID workflowId)
			throws IridaWorkflowNotDisplayableException, IridaWorkflowNotFoundException {
		IridaWorkflow workflow = getIridaWorkflow(workflowId);

		if (getDisplayableWorkflowTypes().contains(workflow.getWorkflowDescription().getAnalysisType())) {
			return workflow;
		} else {
			throw new IridaWorkflowNotDisplayableException(workflowId);
		}
	}

	/**
	 * Get list of workflow output names.
	 * @param workflowId Workflow UUID.
	 * @return List of workflow output names.
	 * @throws IridaWorkflowNotFoundException if no workflow with the given UUID found.
	 */
	public List<String> getOutputNames(UUID workflowId) throws IridaWorkflowNotFoundException {
		final IridaWorkflow iridaWorkflow = getIridaWorkflow(workflowId);
		return iridaWorkflow.getWorkflowDescription()
				.getOutputs()
				.stream()
				.map(IridaWorkflowOutput::getName)
				.collect(Collectors.toList());
	}

	/**
	 * Gets a {@link Set} of all installed workflows.
	 * 
	 * @return A {@link Set} of all installed workflows.
	 */
	public Set<IridaWorkflow> getRegisteredWorkflows() {
		return Sets.newHashSet(allRegisteredWorkflows.values());
	}

	/**
	 * Get all registered single-sample workflow UUIDs for retrieving {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} info with a 1-to-1 mapping to a {@link Sample}
	 * <p>
	 * Since all automated analyses and many other pipelines at this time produce results that map 1-to-1 to a
	 * {@link Sample} and it is trivial to download multi-sample, collection-type
	 * {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile}s (they are zipped and
	 * contain all output for all Sample inputs), we want to enable easy retrieval of results for single-sample
	 * pipelines for batch download.
	 *
	 * @return UUIDs for single-sample workflows
	 */
	public Set<UUID> getSingleSampleWorkflows() {
		return getRegisteredWorkflows()
				.stream()
				.filter(workflow -> workflow.getWorkflowDescription()
						.getInputs()
						.requiresSingleSample())
				.map(IridaWorkflow::getWorkflowIdentifier)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Returns a workflow associated with the given {@link UUID}, attempting to fill
	 * in as many details as possible if the workflow can't be found.
	 * 
	 * @param iridaWorkflowId The {@link UUID} object to search for a workflow.
	 * @return An {@link IridaWorkflow} with the given submission, or an 'unknown'
	 *         workflow object if the associated workflow is not found.
	 */
	public IridaWorkflow getIridaWorkflowOrUnknown(UUID iridaWorkflowId) {
		checkNotNull(iridaWorkflowId, "iridaWorkflowId is null");

		try {
			return getIridaWorkflow(iridaWorkflowId);
		} catch (IridaWorkflowNotFoundException e) {
			logger.warn(
					"Could not find workflow for [" + iridaWorkflowId + "], defaulting to 'unknown' for many details");

			return createUnknownWorkflow(iridaWorkflowId, BuiltInAnalysisTypes.UNKNOWN);
		}
	}

	/**
	 * Returns a workflow associated with the given {@link AnalysisSubmission},
	 * attempting to fill in as many details as possible if the workflow can't be
	 * found.
	 * 
	 * @param analysisSubmission The {@link AnalysisSubmission} object to search for
	 *                           a workflow.
	 * @return An {@link IridaWorkflow} with the given submission, or an 'unknown'
	 *         workflow object if the associated workflow is not found.
	 */
	public IridaWorkflow getIridaWorkflowOrUnknown(AnalysisSubmission analysisSubmission) {
		checkNotNull(analysisSubmission, "analysisSubmission is null");
		checkNotNull(analysisSubmission.getWorkflowId(), "analysisSubmission workflowId is null");

		try {
			return getIridaWorkflow(analysisSubmission.getWorkflowId());
		} catch (IridaWorkflowNotFoundException e) {
			logger.warn("Could not find workflow for [" + analysisSubmission.getWorkflowId()
					+ "], defaulting to 'unknown' for many details");

			AnalysisType type;
			if (AnalysisState.COMPLETED.equals(analysisSubmission.getAnalysisState())
					&& analysisSubmission.getAnalysis() != null) {
				type = analysisSubmission.getAnalysis().getAnalysisType();
			} else {
				type = BuiltInAnalysisTypes.UNKNOWN;
			}

			return createUnknownWorkflow(analysisSubmission.getWorkflowId(), type);
		}
	}

	private IridaWorkflow createUnknownWorkflow(UUID workflowId, AnalysisType analysisType) {
		return new IridaWorkflow(
				new IridaWorkflowDescription(workflowId, "unknown", "unknown", analysisType, new IridaWorkflowInput(),
						Lists.newLinkedList(), Lists.newLinkedList(), Lists.newLinkedList()),
				new IridaWorkflowStructure(null));
	}
}
