package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.security.Principal;
import java.util.*;

import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.*;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;


import com.google.common.collect.ImmutableMap;


/**
 * Controller for Analysis.
 */
@Controller
@Scope("session")
@RequestMapping("/analysis")
public class AnalysisController {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);
	// PAGES
	public static final Map<AnalysisType, String> PREVIEWS = ImmutableMap
			.of(BuiltInAnalysisTypes.PHYLOGENOMICS, "tree", BuiltInAnalysisTypes.SISTR_TYPING, "sistr",
					BuiltInAnalysisTypes.MLST_MENTALIST, "tree");
	private static final String BASE = "analysis/";
	public static final String PAGE_ANALYSIS_LIST = "analyses/analyses";
	public static final String PAGE_USER_ANALYSIS_OUPUTS = "analyses/user-analysis-outputs";
	public static final String ANALYSIS_PAGE = "analysis";

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private UserService userService;
	private EmailController emailController;

	/*
	 *
	 */
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, UserService userService, EmailController emailController, EntityManagerFactory entityManagerFactory) {

		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.userService = userService;
		this.emailController = emailController;
		this.entityManagerFactory=entityManagerFactory;
	}

	// ************************************************************************************************
	// PAGES
	// ************************************************************************************************

	/**
	 * Get the admin all {@link Analysis} list page
	 *
	 * @param model Model for view variables
	 * @return Name of the analysis page view
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("/all")
	public String getAdminAnalysisList(Model model) {
		model.addAttribute("isAdmin", true);
		model.addAttribute("all", true);
		return PAGE_ANALYSIS_LIST;
	}

	/**
	 * Get the user {@link Analysis} list page
	 *
	 * @param model Model for view variables
	 * @param principal Principal {@link User}
	 * @return Name of the analysis page view
	 */
	@RequestMapping()
	public String getUserAnalysisList(Model model, Principal principal) {

		// Determine if the user is an owner or admin.
		User loggedInUser = userService.getUserByUsername(principal.getName());
		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);
		model.addAttribute("isAdmin", isAdmin);
		return PAGE_ANALYSIS_LIST;
	}


	/**
	 * Get the user {@link Analysis} list page
	 *
	 * @return Name of the analysis page view
	 */
	@RequestMapping("/user/analysis-outputs")
	public String getUserAnalysisOutputsPage() {
		return PAGE_USER_ANALYSIS_OUPUTS;
	}


	/**
	 * Redirects to /{submissionId}/* if there is
	 * no trailing slash at the end of the url
	 *
	 * @param submissionId the ID of the submission
	 * @return redirect
	 */

	@RequestMapping(value = "/{submissionId}**")
	public String getDetailsPageRedirect(@PathVariable Long submissionId) {
		return "redirect:/analysis/" + submissionId + "/";
	}

	/**
	 * View details about an individual analysis submission
	 *
	 * @param submissionId the ID of the submission
	 * @param model        Model for the view
	 * @param principal    Principal {@link User}
	 * @return name of the details page view
	 */

	@RequestMapping(value = "/{submissionId}/**", produces = MediaType.TEXT_HTML_VALUE)
	public String getDetailsPage(@PathVariable Long submissionId, Model model, final Principal principal) {
		logger.trace("reading analysis submission " + submissionId);
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		model.addAttribute("analysisSubmission", submission);

		final User currentUser = userService.getUserByUsername(principal.getName());

		// AnalysisControllerTest throws a null pointer error if not checked
		if (currentUser != null) {
			model.addAttribute("isAdmin", currentUser.getSystemRole()
					.equals(Role.ROLE_ADMIN));
		}

		IridaWorkflow iridaWorkflow = workflowsService.getIridaWorkflowOrUnknown(submission);

		// Get the name of the workflow
		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription()
				.getAnalysisType();
		model.addAttribute("analysisType", analysisType);
		model.addAttribute("mailConfigured", emailController.isMailConfigured());

		if (submission.getAnalysisState() == AnalysisState.ERROR) {
			model.addAttribute("previousState", getPreviousStateBeforeError(submissionId));
		}

		return "analysis";
	}

	/**
	 * Gets the state of analysis prior to error
	 *
	 * @param submissionId {@link Long} identifier for an {@link AnalysisSubmission}
	 * @return {@link String} State of analysis prior to error
	 */
	private AnalysisState getPreviousStateBeforeError(Long submissionId) {
		AuditReader audit = AuditReaderFactory.get(entityManagerFactory.createEntityManager());
		AnalysisSubmission previousRevision = null;

		if (audit != null) {
			// Get revisions from the analysis submission audit table for the submission
			List<?> auditResultSet = audit.createQuery()
					.forRevisionsOfEntity(AnalysisSubmission.class, true, false)
					.add(AuditEntity.id()
							.eq(submissionId))
					.getResultList();

			// Go through the revisions and find the first one with an error. The revision
			// prior is set to the previousRevision and it's state is set in the model
			for (Object auditResult : auditResultSet) {
				AnalysisSubmission auditedSubmission = (AnalysisSubmission) auditResult;
				if (auditedSubmission != null && auditedSubmission.getAnalysisState() == AnalysisState.ERROR) {
					break;
				}
				previousRevision = auditedSubmission;
			}
		}
		if (previousRevision == null) {
			return null;
		}

		return previousRevision.getAnalysisState();
	}

	/**
	 * Get the page for viewing advanced phylogenetic visualization
	 *
	 * @param submissionId {@link Long} identifier for an {@link AnalysisSubmission}
	 * @param model        {@link Model}
	 * @return {@link String} path to the page template.
	 */
	@RequestMapping("/{submissionId}/advanced-phylo")
	public String getAdvancedPhylogeneticVisualizationPage(@PathVariable Long submissionId, Model model) {

		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);

		model.addAttribute("submissionId", submissionId);
		model.addAttribute("submission", submission);
		return BASE + "visualizations/phylocanvas-metadata";
	}
}
