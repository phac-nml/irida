package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.net.HttpHeaders;

/**
 * Controller for Analysis.
 */
@Controller
@Scope("session")
@RequestMapping("/analysis")
public class AnalysisController {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);

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
	private MessageSource messageSource;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, UserService userService, MessageSource messageSource) {

		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.userService = userService;
		this.messageSource = messageSource;
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
	 * @return name of the details page view
	 */

	@RequestMapping(value = "/{submissionId}/**", produces = MediaType.TEXT_HTML_VALUE)
	public String getDetailsPage(@PathVariable Long submissionId, Model model) {
		logger.trace("reading analysis submission " + submissionId);
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		model.addAttribute("analysisName", submission.getName());

		IridaWorkflow iridaWorkflow = workflowsService.getIridaWorkflowOrUnknown(submission);

		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription()
				.getAnalysisType();
		model.addAttribute("analysisType", analysisType);
		return "analysis";
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

	/**
	 * Get the html page from the file name provided.
	 *
	 * @param submissionId {@link Long} identifier for an {@link AnalysisSubmission}
	 * @param filename     The html file name
	 * @param locale       User's locale
	 */
	@RequestMapping("/{submissionId}/html-output")
	public void getHtmlOutputForSubmission(@PathVariable Long submissionId, @RequestParam String filename,
			Locale locale, HttpServletResponse response) throws IOException {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Set<AnalysisOutputFile> files = submission.getAnalysis()
				.getAnalysisOutputFiles();
		AnalysisOutputFile outputFile = null;
		String htmlExt = "html";

		for (AnalysisOutputFile file : files) {
			if (file.getFile()
					.toFile()
					.getName()
					.contains(filename) && FilenameUtils.getExtension(filename)
					.equals(htmlExt)) {
				outputFile = file;
				break;
			}
		}

		// Set the common Http headers
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");
		response.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");

		try (InputStream inputStream = new FileInputStream(outputFile.getFile()
				.toString()); OutputStream outputStream = response.getOutputStream()) {
			response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(outputFile.getFile())));
			// Copy the file contents to the response outputstream
			IOUtils.copy(inputStream, outputStream);
		} catch (IOException e) {
			logger.debug("Html output not found.");
			response.setHeader(HttpHeaders.CONTENT_LENGTH, "0");
			String htmlOutputNotFound = messageSource.getMessage("analysis.html.file.not.found", new Object[] { filename }, locale);
			OutputStream outputStream = response.getOutputStream();
			/*
			Write the htmlNotFound message to the outputstream. We do this
			so that the page doesn't error and will instead display the
			message.
			 */
			outputStream.write(htmlOutputNotFound.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
			outputStream.close();
		}
	}
}
