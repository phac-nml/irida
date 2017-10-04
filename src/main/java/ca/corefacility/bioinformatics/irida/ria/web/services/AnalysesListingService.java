package ca.corefacility.bioinformatics.irida.ria.web.services;

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
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AnalysesListingService {
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService iridaWorkflowsService;
	private MessageSource messageSource;

	@Autowired
	public AnalysesListingService(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
	}

	public DataTablesResponse getPagedSubmissions(DataTablesParams params, Locale locale)
			throws IridaWorkflowNotFoundException, ExecutionManagerException {
		// Lets set up the filters
		Map<String, String> searchMap = params.getSearchMap();
		AnalysisState state = searchMap.containsKey("analysis.state") ? AnalysisState.valueOf(searchMap.get("analysis.state")) : null;
		String name = searchMap.getOrDefault("name", null);
		Set<UUID> workflowIds = null;
		if (searchMap.containsKey("workflow")) {
			AnalysisType workflowType = AnalysisType.fromString(searchMap.get("workflow"));
			Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByType(workflowType);
			workflowIds = workflows.stream().map(IridaWorkflow::getWorkflowIdentifier).collect(Collectors.toSet());
		}

		Specification<AnalysisSubmission> specification = AnalysisSubmissionSpecification
				.filterAnalyses(params.getSearchValue(), name, state, null, workflowIds, null);

		Page<AnalysisSubmission> page = analysisSubmissionService
				.search(specification, new PageRequest(params.getCurrentPage(), params.getLength(), params.getSort()));

		List<DataTablesResponseModel> data = new ArrayList<>();
		for (AnalysisSubmission submission : page.getContent()) {
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

		return new DTAnalysis(id, name, submitter, percentComplete, createdDate, workflow, state, duration);
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
