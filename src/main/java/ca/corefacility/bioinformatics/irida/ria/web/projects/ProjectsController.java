package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.config.web.IridaUIWebConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Controller for project related views
 */
@Controller
public class ProjectsController {
	// Sub Navigation Strings
	private static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_METADATA = "metadata";
	private static final String ACTIVE_NAV_REFERENCE = "reference";
	private static final String ACTIVE_NAV_ACTIVITY = "activity";

	// Page Names
	public static final String PROJECTS_DIR = "projects/";
	public static final String LIST_PROJECTS_PAGE = PROJECTS_DIR + "projects";
	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";
	public static final String SPECIFIC_PROJECT_PAGE = PROJECTS_DIR + "project_details";
	public static final String CREATE_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_new";
	public static final String PROJECT_METADATA_PAGE = PROJECTS_DIR + "project_metadata";
	public static final String PROJECT_METADATA_EDIT_PAGE = PROJECTS_DIR + "project_metadata_edit";
	public static final String PROJECT_SAMPLES_PAGE = PROJECTS_DIR + "project_samples";
	public static final String PROJECT_ACTIVITY_PAGE = PROJECTS_DIR + "project_details";
	public static final String PROJECT_REFERENCE_FILES_PAGE = PROJECTS_DIR + "project_reference";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final ProjectControllerUtils projectControllerUtils;
	private final TaxonomyService taxonomyService;

	/*
	 * Converters
	 */
	Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;


	// HTTP session variable name for Galaxy callback variable
	public static final String GALAXY_CALLBACK_VARIABLE_NAME = "galaxyExportToolCallbackURL";
	public static final String GALAXY_CLIENT_ID_NAME = "galaxyExportToolClientID";

	@Autowired
	public ProjectsController(ProjectService projectService, SampleService sampleService, UserService userService,
			ProjectControllerUtils projectControllerUtils, TaxonomyService taxonomyService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.projectControllerUtils = projectControllerUtils;
		this.taxonomyService = taxonomyService;
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
	}

	/**
	 * Request for the page to display a list of all projects available to the
	 * currently logged in user.
	 *
	 * @param model
	 *            The model to add attributes to for the template.
	 * @param galaxyCallbackURL
	 *            The URL at which to call the Galaxy export tool
	 * @param galaxyClientID
	 *            The OAuth2 client ID of the Galaxy instance to export to
	 * @param httpSession
	 *            The user's session
	 * @return The name of the page.
	 */
	@RequestMapping("/projects")
	public String getProjectsPage(Model model,
			@RequestParam(value="galaxyCallbackUrl",required=false) String galaxyCallbackURL,
			@RequestParam(value="galaxyClientID",required=false) String galaxyClientID,
			HttpSession httpSession) {
		model.addAttribute("ajaxURL", "/projects/ajax/list");

		//External exporting functionality
		if(galaxyCallbackURL != null && galaxyClientID != null) {
			httpSession.setAttribute(GALAXY_CALLBACK_VARIABLE_NAME, galaxyCallbackURL);
			httpSession.setAttribute(GALAXY_CLIENT_ID_NAME, galaxyClientID);
		}

		return LIST_PROJECTS_PAGE;
	}

	/**
	 * Get the admin projects page.
	 *
	 * @param model
	 * 		{@link Model}
	 *
	 * @return The name of the page
	 */
	@RequestMapping("/projects/all")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public String getAllProjectsPage(Model model) {
		model.addAttribute("ajaxURL", "/projects/admin/ajax/list");
		return LIST_PROJECTS_PAGE;
	}

	/**
	 * Request for a specific project details page.
	 *
	 * @param projectId
	 *            The id for the project to show details for.
	 * @param model
	 *            Spring model to populate the html page.
	 * @param principal
	 *            a reference to the logged in user.
	 *
	 * @return The name of the project details page.
	 */
	@RequestMapping(value = "/projects/{projectId}/activity")
	public String getProjectSpecificPage(@PathVariable Long projectId, final Model model, final Principal principal) {
		logger.debug("Getting project information for [Project " + projectId + "]");
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ACTIVITY);
		return SPECIFIC_PROJECT_PAGE;
	}

	/**
	 * Gets the name of the template for the new project page
	 *
	 * @param model
	 * 		{@link Model}
	 *
	 * @return The name of the create new project page
	 */
	@RequestMapping(value = "/projects/new", method = RequestMethod.GET)
	public String getCreateProjectPage(final Model model) {
		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<>());
		}
		return CREATE_NEW_PROJECT_PAGE;
	}

	/**
	 * Creates a new project and displays a list of users for the user to add to the project
	 *
	 * @param model
	 * 		{@link Model}
	 * @param name
	 * 		String name of the project
	 * @param organism
	 * 		Organism name
	 * @param projectDescription
	 * 		Brief description of the project
	 * @param remoteURL
	 * 		URL for the project wiki
	 *
	 * @return The name of the add users to project page
	 */
	@RequestMapping(value = "/projects/new", method = RequestMethod.POST)
	public String createNewProject(final Model model, @RequestParam(required = false, defaultValue = "") String name,
			@RequestParam(required = false, defaultValue = "") String organism,
			@RequestParam(required = false, defaultValue = "") String projectDescription,
			@RequestParam(required = false, defaultValue = "") String remoteURL) {

		Project p = new Project(name);
		p.setOrganism(organism);
		p.setProjectDescription(projectDescription);
		p.setRemoteURL(remoteURL);
		Project project;
		try {
			project = projectService.create(p);
		} catch (ConstraintViolationException e) {
			model.addAttribute("errors", getErrorsFromViolationException(e));
			model.addAttribute("project", p);
			return getCreateProjectPage(model);
		}

		return "redirect:/projects/" + project.getId() + "/metadata";
	}

	/**
	 * Returns the name of a page to add users to a *new* project.
	 *
	 * @param model
	 *            {@link Model}
	 * @param principal
	 *            a reference to the logged in user.
	 * @param projectId
	 *            the id of the project to find the metadata for.
	 *
	 * @return The name of the add users to new project page.
	 */
	@RequestMapping("/projects/{projectId}/metadata")
	public String getProjectMetadataPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);

		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_METADATA);
		return PROJECT_METADATA_PAGE;
	}

	@RequestMapping(value = "/projects/{projectId}/metadata/edit", method = RequestMethod.GET)
	public String getProjectMetadataEditPage(final Model model, final Principal principal,
			@PathVariable long projectId) throws IOException {
		Project project = projectService.read(projectId);
		User user = userService.getUserByUsername(principal.getName());
		if (user.getSystemRole().equals(Role.ROLE_ADMIN) || projectService
				.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER)) {
			if (!model.containsAttribute("errors")) {
				model.addAttribute("errors", new HashMap<>());
			}
			projectControllerUtils.getProjectTemplateDetails(model, principal, project);

			model.addAttribute("project", project);
			model.addAttribute("maxFileSize", IridaUIWebConfig.MAX_UPLOAD_SIZE);
			model.addAttribute("maxFileSizeString", fileSizeConverter.convert(IridaUIWebConfig.MAX_UPLOAD_SIZE));
			model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_METADATA);
			return PROJECT_METADATA_EDIT_PAGE;
		} else {
			throw new AccessDeniedException("Do not have permissions to modify this project.");
		}
	}

	@RequestMapping(value = "/projects/{projectId}/referenceFiles", method = RequestMethod.GET)
	public String getProjectReferenceFilesPage(final Model model, final Principal principal,
			@PathVariable long projectId) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute("project", project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_REFERENCE);
		return PROJECT_REFERENCE_FILES_PAGE;
	}

	@RequestMapping(value = "/projects/{projectId}/metadata/edit", method = RequestMethod.POST)
	public String postProjectMetadataEditPage(final Model model, final Principal principal,
			@PathVariable long projectId, @RequestParam(required = false, defaultValue = "") String name,
			@RequestParam(required = false, defaultValue = "") String organism,
			@RequestParam(required = false, defaultValue = "") String projectDescription,
			@RequestParam(required = false, defaultValue = "") String remoteURL) throws IOException {

		Map<String, Object> updatedValues = new HashMap<>();
		if (!Strings.isNullOrEmpty(name)) {
			updatedValues.put("name", name);
		}
		if (!Strings.isNullOrEmpty(organism)) {
			updatedValues.put("organism", organism);
		}
		if (!Strings.isNullOrEmpty(projectDescription)) {
			updatedValues.put("projectDescription", projectDescription);
		}
		if (!Strings.isNullOrEmpty(remoteURL)) {
			updatedValues.put("remoteURL", remoteURL);
		}
		if (updatedValues.size() > 0) {
			try {
				projectService.update(projectId, updatedValues);
			} catch (ConstraintViolationException ex) {
				model.addAttribute("errors", getErrorsFromViolationException(ex));
				return getProjectMetadataEditPage(model, principal, projectId);
			}
		}
		return "redirect:/projects/" + projectId + "/metadata";
	}

	/**
	 * Search for taxonomy terms. This method will return a map of found taxonomy terms and their child nodes.
	 * <p>
	 * Note: If the search term was not included in the results, it will be added as an option
	 *
	 * @param searchTerm
	 * 		The term to find taxa for
	 *
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
	 * User mapping to get a list of all project they are on.
	 * @param principal {@link Principal} currently logged in user.
	 * @return {@link List} of project {@link Map}
	 */
	@RequestMapping("/projects/ajax/list")
	@ResponseBody
	public List<Map<String, Object>> getAjaxProjectList(final Principal principal) {

		User user = userService.getUserByUsername(principal.getName());
		return getProjectsDataMap(projectService.getProjectsForUser(user));
	}

	/**
	 * Admin mapping to get a list of all project they are on.
	 *
	 * @param principal
	 * 		{@link Principal} currently logged in user.
	 *
	 * @return {@link List} of project {@link Map}
	 */
	@RequestMapping("/projects/admin/ajax/list")
	@ResponseBody
	public List<Map<String, Object>> getAjaxAdminProjectsList(final Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		return generateAdminProjectMap(user, (List<Project>) projectService.findAll());
	}

	/**
	 * Generates a map of project information for the {@link ProjectsDataTable}
	 *
	 * @return Map containing the information to put into the {@link ProjectsDataTable}
	 */
	public List<Map<String, Object>> getProjectsDataMap(List<Join<Project, User>> projectList) {
		// Create the format required by DataTable
		List<Map<String, Object>> projectsData = new ArrayList<>(projectList.size());
		for (Join<Project, User> join : projectList) {
			Project p = join.getSubject();
			String role = ((ProjectUserJoin) join).getProjectRole() != null ?
					((ProjectUserJoin) join).getProjectRole().toString() :
					"";
			projectsData.add(createProjectDetailsMap(p, role));
		}
		return projectsData;
	}

	/**
	 * Generates a map of projects for an admin user.
	 * @param user {@link User} currently logged in user.
	 * @param projects {@link List} of {@link Project}
	 * @return {@link List} of {@link Map} of project data.
	 */
	private List<Map<String, Object>> generateAdminProjectMap(User user, List<Project> projects) {
		List<Map<String, Object>> result = new ArrayList<>(projects.size());
		for (Project project : projects) {
			String role = projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER) ?
					ProjectRole.PROJECT_OWNER.toString() :
					projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_USER) ?
							ProjectRole.PROJECT_USER.toString() :
							"PROJECT_NONE";
			result.add(createProjectDetailsMap(project, role));
		}
		return result;
	}

	/**
	 * Generates a map of the project data.  This is required for specific fields such as 'role', 'samples' and
	 * 'members'
	 *
	 * @param project
	 * 		{@link Project}
	 * @param role
	 * 		{@link String} user's role on the project.
	 *
	 * @return
	 */
	private Map<String, Object> createProjectDetailsMap(Project project, String role) {
		Map<String, Object> map = new HashMap<>();
		map.put("item", project);
		map.put("link", "projects/" + project.getId());
		map.put("custom", ImmutableMap.of(
				"samples", String.valueOf(sampleService.getSamplesForProject(project).size()),
				"members", String.valueOf(userService.getUsersForProject(project).size()),
				"role", role
		));
		return map;
	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings for displaing in the UI.
	 *
	 * @param e
	 * 		{@link ConstraintViolationException} for the form submitted.
	 *
	 * @return Map of string {fieldName, error}
	 */
	private Map<String, String> getErrorsFromViolationException(ConstraintViolationException e) {
		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			String message = violation.getMessage();
			String field = violation.getPropertyPath().toString();
			errors.put(field, message);
		}
		return errors;
	}

	@ExceptionHandler({ ProjectWithoutOwnerException.class, ProjectSelfEditException.class })
	@ResponseBody
	public ResponseEntity<String> roleChangeErrorHandler(Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	/**
	 * }
	 * <p>
	 * /** Recursively transform a {@link TreeNode} into a json parsable map object
	 *
	 * @param node
	 * 		The node to transform
	 *
	 * @return A Map<String,Object> which may contain more children
	 */
	private Map<String, Object> transformTreeNode(TreeNode<String> node) {
		Map<String, Object> current = new HashMap<>();

		// add the node properties to the map
		for (Entry<String, Object> property : node.getProperties().entrySet()) {
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
}
