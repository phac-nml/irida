package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnalysisSubmissionSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTAnalysis;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

@Component
public class AnalysesListingService {
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService iridaWorkflowsService;
	private UpdateAnalysisSubmissionPermission updateAnalysisPermission;
	private MessageSource messageSource;

	@Autowired
	public AnalysesListingService(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, UpdateAnalysisSubmissionPermission updateAnalysisPermission,
			MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.updateAnalysisPermission = updateAnalysisPermission;
		this.messageSource = messageSource;
	}

	public DataTablesResponse getPagedSubmissions(DataTablesParams params, Locale locale)
			throws IridaWorkflowNotFoundException, ExecutionManagerException {
		/*
		Check the DataTableParams to see if any search conditions are present
		 */
		Map<String, String> searchMap = params.getSearchMap();
		AnalysisState state = searchMap.containsKey("analysisState") ? AnalysisState.valueOf(searchMap.get("analysisState")) : null;
		String name = searchMap.getOrDefault("name", null);
		/*
		Workflow Ids are a special consideration.
		The actual ids need to be look up based on the name passed.
		 */
		Set<UUID> workflowIds = null;
		if (searchMap.containsKey("workflow")) {
			AnalysisType workflowType = AnalysisType.fromString(searchMap.get("workflow"));
			Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByType(workflowType);
			workflowIds = workflows.stream().map(IridaWorkflow::getWorkflowIdentifier).collect(Collectors.toSet());
		}

		/*
		Generate the specification.
		Note no user or project are required since this is a global analyses search
		performed by an administrator.
		 */
		Specification<AnalysisSubmission> specification = AnalysisSubmissionSpecification
				.filterAnalyses(params.getSearchValue(), name, state, null, workflowIds, null);

		Page<AnalysisSubmission> page = analysisSubmissionService
				.search(specification, new PageRequest(params.getCurrentPage(), params.getLength(), params.getSort()));

		/*
		IRIDA DataTables response expects and object that implements the DataTablesResponseModel interface.
		 */
		List<DataTablesResponseModel> data = new ArrayList<>();
		for (AnalysisSubmission submission : page.getContent()) {
			// Each AnalysisSubmission needs to be converted into a DTAnalysis.
			data.add(createDataTablesAnalysis(submission, locale));
		}

		return new DataTablesResponse(params, page, data);
	}

	private DTAnalysis createDataTablesAnalysis(AnalysisSubmission submission, Locale locale)
			throws IridaWorkflowNotFoundException, ExecutionManagerException {
		Long id = submission.getId();
		String name = submission.getName();
		String submitter = submission.getSubmitter().getLabel();
		Date createdDate = submission.getCreatedDate();
		float percentComplete = 0;
		if (!submission.getAnalysisState().equals(AnalysisState.ERROR)) {
			percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(submission.getId());
		}

		String workflowType = iridaWorkflowsService.getIridaWorkflow(submission.getWorkflowId()).getWorkflowDescription()
				.getAnalysisType().toString();
		String workflow = messageSource.getMessage("workflow." + workflowType + ".title", null, locale);
		String state = messageSource
				.getMessage("analysis.state." + submission.getAnalysisState().toString(), null, locale);
		Long duration = getAnalysisDuration(submission);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean updatePermission = updateAnalysisPermission.isAllowed(authentication, submission);

		return new DTAnalysis(id, name, submitter, percentComplete, createdDate, workflow, state, duration,
				updatePermission);
	}

	private Long getAnalysisDuration(AnalysisSubmission submission) {
		Analysis analysis = submission.getAnalysis();
		Long duration = 0L;
		Instant createInstant = submission.getCreatedDate().toInstant();
		// get duration
		if (submission.getAnalysisState().equals(AnalysisState.COMPLETED)) {
			Instant finishedInstant = analysis.getCreatedDate().toInstant();
			Duration between = Duration.between(finishedInstant, createInstant);
			duration = between.toMillis();
		}

		return duration;
	}
}
