package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.ProjectBaseController;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

/**
 * Controller for project related views
 */
@Controller
public class ProjectsController extends ProjectBaseController {
	// Sub Navigation Strings
	public static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_ACTIVITY = "activity";
	private static final String ACTIVE_NAV_ANALYSES = "analyses";

	// Page Names
	public static final String PROJECTS_DIR = "projects/";
	public static final String SPECIFIC_PROJECT_PAGE = PROJECTS_DIR + "project_details";
	public static final String SYNC_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_sync";

	// Services
	private final ProjectService projectService;
	private final TaxonomyService taxonomyService;
	private final ProjectRemoteService projectRemoteService;
	private final RemoteAPIService remoteApiService;
	private final UpdateSamplePermission updateSamplePermission;

	/*
	 * Converters
	 */
	Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;

	@Autowired
	public ProjectsController(ProjectService projectService, ProjectRemoteService projectRemoteService,
			TaxonomyService taxonomyService, RemoteAPIService remoteApiService,
			UpdateSamplePermission updateSamplePermission) {
		this.projectService = projectService;
		this.projectRemoteService = projectRemoteService;
		this.taxonomyService = taxonomyService;
		this.dateFormatter = new DateFormatter();
		this.remoteApiService = remoteApiService;
		this.fileSizeConverter = new FileSizeConverter();
		this.updateSamplePermission = updateSamplePermission;
	}

	/**
	 * Request for a specific project details page.
	 *
	 * @param model Spring model to populate the html page.
	 * @return The name of the project details page.
	 */
	@RequestMapping(value = "/projects/{projectId}/activity")
	public String getProjectSpecificPage(final Model model) {
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ACTIVITY);
		return SPECIFIC_PROJECT_PAGE;
	}

	/**
	 * Get the page to synchronize remote projects
	 *
	 * @param model Model to render for view
	 * @return Name of the project sync page
	 */
	@RequestMapping(value = "/projects/synchronize", method = RequestMethod.GET)
	public String getSynchronizeProjectPage(final Model model) {

		Iterable<RemoteAPI> apis = remoteApiService.findAll();
		model.addAttribute("apis", apis);
		model.addAttribute("frequencies", ProjectSyncFrequency.values());
		model.addAttribute("defaultFrequency", ProjectSyncFrequency.WEEKLY);

		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<>());
		}

		return SYNC_NEW_PROJECT_PAGE;
	}

	/**
	 * Get a {@link Project} from a remote api and mark it to be synchronized in
	 * this IRIDA installation
	 *
	 * @param url           the URL of the remote project
	 * @param syncFrequency How often to sync the project
	 * @param model         Model for the view
	 * @return Redirect to the new project. If an oauth exception occurs it will
	 * be forwarded back to the creation page.
	 */
	@RequestMapping(value = "/projects/synchronize", method = RequestMethod.POST)
	public String syncProject(@RequestParam String url, @RequestParam ProjectSyncFrequency syncFrequency, Model model) {

		try {
			Project read = projectRemoteService.read(url);
			read.setId(null);
			read.getRemoteStatus()
					.setSyncStatus(SyncStatus.MARKED);
			read.setSyncFrequency(syncFrequency);

			read = projectService.create(read);

			return "redirect:/projects/" + read.getId() + "/settings";
		} catch (IridaOAuthException ex) {
			Map<String, String> errors = new HashMap<>();
			errors.put("oauthError", ex.getMessage());
			model.addAttribute("errors", errors);
			return getSynchronizeProjectPage(model);
		} catch (EntityNotFoundException ex) {
			Map<String, String> errors = new HashMap<>();
			errors.put("urlError", ex.getMessage());
			model.addAttribute("errors", errors);
			return getSynchronizeProjectPage(model);
		}
	}

	/**
	 * List all the {@link Project}s that can be read for a user from a given
	 * {@link RemoteAPI}
	 *
	 * @param apiId the local ID of the {@link RemoteAPI}
	 * @return a List of {@link Project}s
	 */
	@RequestMapping(value = "/projects/ajax/api/{apiId}")
	@ResponseBody
	public List<ProjectByApiResponse> ajaxGetProjectsForApi(@PathVariable Long apiId) {
		RemoteAPI api = remoteApiService.read(apiId);
		List<Project> listProjectsForAPI = projectRemoteService.listProjectsForAPI(api);

		return listProjectsForAPI.stream()
				.map(ProjectByApiResponse::new)
				.collect(Collectors.toList());
	}

	/**
	 * Get the page for analyses shared with a given {@link Project}
	 *
	 * @param model model for view variables
	 * @return name of the analysis view page
	 */
	@RequestMapping("/projects/{projectId}/analyses")
	public String getProjectAnalysisList(Model model) {
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ANALYSES);
		model.addAttribute("page", "analyses");
		return "projects/analyses/pages/analyses_table.html";
	}

	/**
	 * Get the page for analysis output files shared with a given {@link Project}
	 *
	 * @param projectId the ID of the {@link Project}
	 * @param model     model for view variables
	 * @return name of the analysis view page
	 */
	@RequestMapping("/projects/{projectId}/analyses/shared-outputs")
	public String getProjectSharedOutputFilesPage(@PathVariable Long projectId, Model model) {
		model.addAttribute("ajaxURL", "/ajax/analysis/project/" + projectId + "/list");
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ANALYSES);
		model.addAttribute("page", "shared");
		return "projects/analyses/pages/outputs.html";
	}

	/**
	 * Get the page for automated analysis output files shared with a given {@link Project}
	 *
	 * @param projectId the ID of the {@link Project}
	 * @param model     model for view variables
	 * @return name of the analysis view page
	 */
	@RequestMapping("/projects/{projectId}/analyses/automated-outputs")
	public String getProjectAutomatedOutputFilesPage(@PathVariable Long projectId, Model model) {
		model.addAttribute("ajaxURL", "/ajax/analysis/project/" + projectId + "/list");
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ANALYSES);
		model.addAttribute("page", "automated");
		return "projects/analyses/pages/outputs.html";
	}

	/**
	 * Search for taxonomy terms. This method will return a map of found taxonomy terms and their child nodes.
	 * <p>
	 * Note: If the search term was not included in the results, it will be added as an option
	 *
	 * @param searchTerm The term to find taxa for
	 * @return A {@code List<Map<String,Object>>} which will contain a taxonomic tree of matching terms
	 */
	@RequestMapping("/projects/ajax/taxonomy/search")
	@ResponseBody
	public List<Map<String, Object>> searchTaxonomy(@RequestParam String searchTerm) {
		Collection<TreeNode<String>> search = taxonomyService.search(searchTerm);

		TreeNode<String> searchTermNode = new TreeNode<>(searchTerm);
		// add a property to this node to indicate that it's the search term
		searchTermNode.addProperty("searchTerm", true);

		List<Map<String, Object>> elements = new ArrayList<>();

		// get the search term in first if it's not there yet
		if (!search.contains(searchTermNode)) {
			elements.add(transformTreeNode(searchTermNode));
		}

		for (TreeNode<String> node : search) {
			Map<String, Object> transformTreeNode = transformTreeNode(node);
			elements.add(transformTreeNode);
		}
		return elements;
	}

	/**
	 * Handle a {@link ProjectWithoutOwnerException} error.  Returns a forbidden error
	 *
	 * @param ex the exception to handle.
	 * @return response entity with FORBIDDEN error
	 */
	@ExceptionHandler(ProjectWithoutOwnerException.class)
	@ResponseBody
	public ResponseEntity<String> roleChangeErrorHandler(Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	/**
	 * Test whether the logged in user can modify a {@link Sample}
	 *
	 * @param sample the {@link Sample} to check
	 * @return true if they can modify
	 */
	private boolean canModifySample(Sample sample) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		return updateSamplePermission.isAllowed(authentication, sample);
	}

	/**
	 * }
	 * <p>
	 * /** Recursively transform a {@link TreeNode} into a json parsable map object
	 *
	 * @param node The node to transform
	 * @return A Map<String,Object> which may contain more children
	 */
	private Map<String, Object> transformTreeNode(TreeNode<String> node) {
		Map<String, Object> current = new HashMap<>();

		// add the node properties to the map
		for (Entry<String, Object> property : node.getProperties()
				.entrySet()) {
			current.put(property.getKey(), property.getValue());
		}

		current.put("id", node.getValue());
		current.put("text", node.getValue());

		List<Object> children = new ArrayList<>();
		for (TreeNode<String> child : node.getChildren()) {
			Map<String, Object> transformTreeNode = transformTreeNode(child);
			children.add(transformTreeNode);
		}

		if (!children.isEmpty()) {
			current.put("children", children);
		}

		return current;
	}

	/**
	 * Response class for a {@link Project} and its {@link RemoteStatus}
	 */
	public class ProjectByApiResponse {
		private RemoteStatus remoteStatus;
		private Project project;

		public ProjectByApiResponse(Project project) {
			this.project = project;
			this.remoteStatus = project.getRemoteStatus();
		}

		public Project getProject() {
			return project;
		}

		public RemoteStatus getRemoteStatus() {
			return remoteStatus;
		}
	}
}
