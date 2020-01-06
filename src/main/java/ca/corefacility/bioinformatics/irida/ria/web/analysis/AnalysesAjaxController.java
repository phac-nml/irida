package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.auditing.AnalysisAuditing;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysesListRequest;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisModel;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisStateModel;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisTypeModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.TableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.utilities.DateUtilities;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Controller to handle ajax requests for Analyses
 */
@RestController
@RequestMapping("/ajax/analyses")
public class AnalysesAjaxController {
	private AnalysisSubmissionService analysisSubmissionService;
	private AnalysisTypesService analysisTypesService;
	private ProjectService projectService;
	private IridaWorkflowsService iridaWorkflowsService;
	private MessageSource messageSource;
	private UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission;
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	public AnalysesAjaxController(AnalysisSubmissionService analysisSubmissionService,
			AnalysisTypesService analysisTypesService, ProjectService projectService,
			IridaWorkflowsService iridaWorkflowsService, MessageSource messageSource,
			UpdateAnalysisSubmissionPermission updateAnalysisSubmissionPermission, EntityManagerFactory entityManagerFactory) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisTypesService = analysisTypesService;
		this.projectService = projectService;
		this.iridaWorkflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
		this.updateAnalysisSubmissionPermission = updateAnalysisSubmissionPermission;
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * Get a internationalized list of all Analysis pipeline states.
	 *
	 * @param locale {@link Locale} Users locale
	 * @return {@link List} of {@link AnalysisState}
	 */
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
		Set<AnalysisType> types = iridaWorkflowsService.getRegisteredWorkflowTypes();
		return types.stream()
				.map(t -> new AnalysisTypeModel(
						messageSource.getMessage("workflow." + t.getType() + ".title", new Object[] {}, locale),
						t.getType()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list of analyses based on paging, sorting and filter requirements sent in {@link AnalysesListRequest}
	 *
	 * @param analysesListRequest description of the paging requirements.  Includes sorting, filtering, and paging
	 * @param admin               {@link Boolean} whether this is from the admin page.
	 * @param projectId           {@link Long} if the request is from a specific project
	 * @param locale              of the current user
	 * @return the current contents of the table based on the state requested
	 * @throws IridaWorkflowNotFoundException thrown if the workflow cannot be found
	 */
	@RequestMapping("/list")
	public TableResponse getPagedAnalyses(@RequestBody AnalysesListRequest analysesListRequest,
			@RequestParam(required = false, defaultValue = "false") Boolean admin,
			@RequestParam(required = false) Long projectId, Locale locale) throws IridaWorkflowNotFoundException {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = (User) authentication.getPrincipal();

		/*
		Check to see if we are filtering by workflow type
		 */
		Set<UUID> workflowIds = new HashSet<>();
		if (analysesListRequest.getFilters()
				.getType()
				.size() > 0) {
			List<String> workflowTypesFilter = analysesListRequest.getFilters()
					.getType();
			for (String type : workflowTypesFilter) {
				AnalysisType workflowType = analysisTypesService.fromString(type);
				Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByType(workflowType);
				workflowIds.addAll(workflows.stream()
						.map(IridaWorkflow::getWorkflowIdentifier)
						.collect(Collectors.toSet()));
			}
		}

		/*
		Check to see if the filter includes the state.  This can be any number of states.
		 */
		Set<AnalysisState> stateFilters = new HashSet<>();
		if (analysesListRequest.getFilters()
				.getState()
				.size() > 0) {
			List<AnalysisState> states = analysesListRequest.getFilters()
					.getState();
			stateFilters.addAll(states);
		}

		Page<AnalysisSubmission> page;
		PageRequest pageRequest = PageRequest.of(analysesListRequest.getCurrent(), analysesListRequest.getPageSize(),
				analysesListRequest.getSort());

		if (projectId != null) {
			Project project = projectService.read(projectId);
			page = analysisSubmissionService.listSubmissionsForProject(analysesListRequest.getSearch(), null,
					stateFilters, workflowIds, project, pageRequest);
		} else if (admin && user.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			// User is an admin and requesting the listing of all pages.
			page = analysisSubmissionService.listAllSubmissions(analysesListRequest.getSearch(), null, stateFilters,
					workflowIds, pageRequest);
		} else {
			page = analysisSubmissionService.listSubmissionsForUser(analysesListRequest.getSearch(), null, stateFilters,
					user, workflowIds, pageRequest);
		}

		/*
		UI cannot consume it as-is.  Format into something the UI will like use the the AnalysisModel
		 */
		List<TableModel> analyses = page.getContent()
				.stream()
				.map(submission -> this.createAnalysisModel(submission, locale))
				.collect(Collectors.toList());

		return new TableResponse(analyses, page.getTotalElements());
	}

	/**
	 * Delete a specific {@link AnalysisSubmission}
	 *
	 * @param ids      for all {@link AnalysisSubmission}'s to delete
	 * @param response {@link HttpServletResponse}
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public void deleteAnalysisSubmissions(@RequestParam List<Long> ids, HttpServletResponse response) {
		try {
			analysisSubmissionService.deleteMultiple(ids);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Format an {@link AnalysisSubmission} into something a form that the UI can consume.
	 *
	 * @param submission {@link AnalysisSubmission}
	 * @param locale     {@link Locale}
	 * @return {@link AnalysisModel}
	 */
	private AnalysisModel createAnalysisModel(AnalysisSubmission submission, Locale locale) {
		AnalysisState analysisState = submission.getAnalysisState();
		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflowOrUnknown(submission);
		String workflowType = iridaWorkflow.getWorkflowDescription()
				.getAnalysisType()
				.getType();
		String stateString = messageSource.getMessage("analysis.state." + analysisState.toString(), null, locale);
		AnalysisStateModel state = new AnalysisStateModel(stateString, analysisState.toString());
		String workflow = messageSource.getMessage("workflow." + workflowType + ".title", null, workflowType, locale);

		AnalysisAuditing analysisAudit = new AnalysisAuditing(entityManagerFactory);
		Long duration = analysisAudit.getAnalysisRunningTime(submission);

		/*
		Check to see if the user has authority to update (delete) this particular submission.
		 */
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		boolean updatePermission = this.updateAnalysisSubmissionPermission.isAllowed(authentication, submission);

		return new AnalysisModel(submission, state, duration, workflow, updatePermission);
	}

	/**
	 * Download the output files of an {@link AnalysisSubmission}
	 *
	 * @param id       {@link Long} identifier for an {@link AnalysisSubmission}
	 * @param response downloaded output files
	 */
	@RequestMapping("/download/{id}")
	public void downloadAnalysis(@PathVariable Long id, HttpServletResponse response) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(id);

		Analysis analysis = analysisSubmission.getAnalysis();
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();
		FileUtilities.createAnalysisOutputFileZippedResponse(response, analysisSubmission.getName(), files);
	}
}

