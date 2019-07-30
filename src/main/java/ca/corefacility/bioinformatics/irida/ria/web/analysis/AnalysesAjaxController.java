package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.*;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;

/**
 * Controller to handle ajax requests for Analyses
 */
@RestController
@RequestMapping("/ajax/analyses")
public class AnalysesAjaxController {

	private AnalysisSubmissionService analysisSubmissionService;
	private AnalysisTypesService analysisTypesService;
	private IridaWorkflowsService iridaWorkflowsService;
	private MessageSource messageSource;
	private UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission;
	private IridaWorkflowsService workflowsService;

	@Autowired
	public AnalysesAjaxController(AnalysisSubmissionService analysisSubmissionService,
			AnalysisTypesService analysisTypesService, IridaWorkflowsService iridaWorkflowsService,
			MessageSource messageSource, UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission,
			IridaWorkflowsService workflowsService) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisTypesService = analysisTypesService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
		this.updateAnalysisSubmissionPermission = updateAnalysisSubmissionPermission;
		this.workflowsService = workflowsService;
	}

	@RequestMapping("/states")
	public List<AnalysisStateModel> getAnalysisStates(Locale locale) {
		List<AnalysisState> states = Arrays.asList(AnalysisState.values());
		return states.stream()
				.map(s -> new AnalysisStateModel(
						messageSource.getMessage("analysis.state." + s, new Object[] {}, locale), s.name()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list of localized names for all available {@link AnalysisType}s.
	 *
	 * @param locale the locale of the current user
	 * @return an internationalized list of analysis type names
	 */
	@RequestMapping("/types")
	public List<AnalysisTypeModel> getWorkflowTypes(Locale locale) {
		Set<AnalysisType> types = workflowsService.getRegisteredWorkflowTypes();
		return types.stream()
				.map(t -> new AnalysisTypeModel(
						messageSource.getMessage("workflow." + t.getType() + ".title", new Object[] {}, locale), t.getType()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list of analyses based on paging, sorting and filter requirements sent in {@link AnalysesListRequest}
	 *
	 * @param analysesListRequest description of the paging requirements.  Includes sorting, filtering, and paging
	 * @param type of the list required (whether administrator or user)
	 * @param locale of the current user
	 * @return the current contents of the table based on the state requested
	 * @throws IridaWorkflowNotFoundException thrown if the workflow cannot be found
	 */
	@RequestMapping("/list")
	public AnalysesListResponse getPagedAnalyses(@RequestBody AnalysesListRequest analysesListRequest,
			HttpServletRequest request, Locale locale)
			throws IridaWorkflowNotFoundException {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = (User) authentication.getPrincipal();

		/*
		Check to see if we are filtering by workflow type
		 */
		Set<UUID> workflowIds = null;
		if (!Strings.isNullOrEmpty(analysesListRequest.getFilters()
				.getType())) {
			AnalysisType workflowType = analysisTypesService.fromString(analysesListRequest.getFilters()
					.getType());
			Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByType(workflowType);
			workflowIds = workflows.stream()
					.map(IridaWorkflow::getWorkflowIdentifier)
					.collect(Collectors.toSet());
		}

		Page<AnalysisSubmission> page;
		PageRequest pageRequest = new PageRequest(analysesListRequest.getCurrent(), analysesListRequest.getPageSize(),
				analysesListRequest.getSort());

		/*
		If they are requesting to list all submissions make sure they are truly on the administrator page
		which would throw a true error and redirect the user else where.
		 */
		String referer = request.getHeader(HttpHeaders.REFERER);
		if (referer.endsWith("all") && user.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			// User is an admin and requesting the listing of all pages.
			page = analysisSubmissionService.listAllSubmissions(analysesListRequest.getSearch(), null,
					analysesListRequest.getFilters()
							.getState(), workflowIds, pageRequest);
		} else {
			page = analysisSubmissionService.listSubmissionsForUser(analysesListRequest.getSearch(), null,
					analysesListRequest.getFilters()
							.getState(), user, workflowIds, pageRequest);
		}

		List<AnalysisModel> analyses = page.getContent()
				.stream()
				.map(submission -> this.createAnalysisModel(submission, locale))
				.collect(Collectors.toList());

		return new AnalysesListResponse(analyses, page.getTotalElements());
	}

	/**
	 * Delete a specific {@link AnalysisSubmission}
	 *
	 * @param id for the {@link AnalysisSubmission} to delete
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public void deleteAnalysisSubmission(@RequestParam Long id) {
		final AnalysisSubmission deletedSubmission = analysisSubmissionService.read(id);
		analysisSubmissionService.delete(id);
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

