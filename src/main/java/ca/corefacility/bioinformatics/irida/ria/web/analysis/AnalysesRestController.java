package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysesListRequest;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysesListResponse;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisModel;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

@RestController
@RequestMapping("/ajax/analyses")
public class AnalysesRestController {

	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService iridaWorkflowsService;
	private MessageSource messageSource;
	private UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission;

	@Autowired
	public AnalysesRestController(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, MessageSource messageSource,
			UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
		this.updateAnalysisSubmissionPermission = updateAnalysisSubmissionPermission;
	}

	@RequestMapping("/list")
	public AnalysesListResponse getPagedAnalyses(@RequestBody AnalysesListRequest analysesListRequest,
			@RequestParam(required = false, defaultValue = "user") String type, Locale locale) {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = (User) authentication.getPrincipal();

		/*
		If they are requesting to list all submissions make sure they are truly on the administrator page
		which would throw a true error and redirect the user else where.
		 */
		if (type.equals("all") && !user.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			type = "user";
		}

		Page<AnalysisSubmission> page;
		PageRequest pageRequest = new PageRequest(analysesListRequest.getCurrent(),
				analysesListRequest.getPageSize(), analysesListRequest.getSort());
		page = analysisSubmissionService.listAllSubmissions(null, null, null, null, pageRequest);

		List<AnalysisModel> analyses = page.getContent()
				.stream()
				.map(submission -> this.createAnalysisModel(submission, locale))
				.collect(Collectors.toList());

		return new AnalysesListResponse(analyses, page.getTotalElements());
	}

	private AnalysisModel createAnalysisModel(AnalysisSubmission submission, Locale locale) {
		float percentComplete = 0;
		AnalysisState analysisState = submission.getAnalysisState();
		JobError error = null;
		if (analysisState.equals(AnalysisState.ERROR)) {
			try {
				error = analysisSubmissionService.getFirstJobError(submission.getId());
			} catch (ExecutionManagerException e) {
				// Leave error set to null for now.
			}
		} else {
			try {
				percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(submission.getId());
			} catch (ExecutionManagerException e) {
				// Leave the percentage set to 0.
			}
		}

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflowOrUnknown(submission);
		String workflowType = iridaWorkflow.getWorkflowDescription()
				.getAnalysisType()
				.getType();
		String state = messageSource.getMessage("analysis.state." + analysisState.toString(), null, locale);
		String workflow = messageSource.getMessage("workflow." + workflowType + ".title", null, workflowType, locale);
		Long duration = 0L;
		if (analysisState.equals(AnalysisState.COMPLETED)) {
			duration = getDurationInMilliseconds(submission.getCreatedDate(), submission.getAnalysis()
					.getCreatedDate());
		}

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		boolean updatePermission = this.updateAnalysisSubmissionPermission.isAllowed(authentication, submission);

		return new AnalysisModel(submission, state, duration, workflow, percentComplete, updatePermission);
	}

	private JobError getFirstJobError(AnalysisSubmission submission) throws ExecutionManagerException {
		return analysisSubmissionService.getFirstJobError(submission.getId());
	}

	/**
	 * Get the milliseconds between two {@link Date}s
	 *
	 * @param start {@link Date}
	 * @param end   {@link Date}
	 * @return {@link Long} milliseconds
	 */
	private Long getDurationInMilliseconds(Date start, Date end) {
		Instant startInstant = start.toInstant();
		Instant endInstant = end.toInstant();
		Duration duration = Duration.between(startInstant, endInstant)
				.abs();
		return duration.toMillis();
	}
}

