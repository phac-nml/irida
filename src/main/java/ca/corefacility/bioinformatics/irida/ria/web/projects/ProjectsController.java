package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.config.WebConfigurer;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsAdminDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

import com.google.common.base.Strings;

/**
 * Controller for project related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController {
	// Sub Navigation Strings
	private static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_DASHBOARD = "dashboard";
	private static final String ACTIVE_NAV_METADATA = "metadata";
	private static final String ACTIVE_NAV_REFERENCE = "reference";

	// private static final String ACTIVE_NAV_ANALYSIS = "analysis";

	// Page Names
	public static final String PROJECTS_DIR = "projects/";
	public static final String LIST_PROJECTS_PAGE = PROJECTS_DIR + "projects";
	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";
	public static final String SPECIFIC_PROJECT_PAGE = PROJECTS_DIR + "project_details";
	public static final String CREATE_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_new";
	public static final String PROJECT_METADATA_PAGE = PROJECTS_DIR + "project_metadata";
	public static final String PROJECT_METADATA_EDIT_PAGE = PROJECTS_DIR + "project_metadata_edit";
	public static final String PROJECT_SAMPLES_PAGE = PROJECTS_DIR + "project_samples";
	public static final String PROJECT_REFERENCE_FILES_PAGE = PROJECTS_DIR + "project_reference";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final ProjectControllerUtils projectControllerUtils;
	private final ReferenceFileService referenceFileService;
	private final TaxonomyService taxonomyService;

	/*
	 * Converters
	 */
	Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;

	@Autowired
	public ProjectsController(ProjectService projectService, SampleService sampleService, UserService userService,
			ProjectControllerUtils projectControllerUtils, ReferenceFileService referenceFileService,
			TaxonomyService taxonomyService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.projectControllerUtils = projectControllerUtils;
		this.referenceFileService = referenceFileService;
		this.taxonomyService = taxonomyService;
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
	}

	/**
	 * Request for the page to display a list of all projects available to the currently logged in user.
	 *
	 * @return The name of the page.
	 */
	@RequestMapping
	public String getProjectsPage(Model model) {
		model.addAttribute("ajaxURL", "/projects/ajax/list");
		model.addAttribute("isAdmin", false);
		return LIST_PROJECTS_PAGE;
	}

	@RequestMapping("/all")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public String getAllProjectsPage(Model model) {
		model.addAttribute("ajaxURL", "/projects/ajax/list/all");
		model.addAttribute("isAdmin", true);
		return LIST_PROJECTS_PAGE;
	}

	/**
	 * Request for a specific project details page.
	 *
	 * @param projectId
	 * 		The id for the project to show details for.
	 * @param model
	 * 		Spring model to populate the html page.
	 *
	 * @return The name of the project details page.
	 */
	@RequestMapping(value = "/{projectId}")
	public String getProjectSpecificPage(@PathVariable Long projectId, final Model model, final Principal principal) {
		logger.debug("Getting project information for [Project " + projectId + "]");
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_DASHBOARD);
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
	@RequestMapping(value = "/new", method = RequestMethod.GET)
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
	@RequestMapping(value = "/new", method = RequestMethod.POST)
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
			return getCreateProjectPage(model);
		}

		return "redirect:/projects/" + project.getId() + "/metadata";
	}

	/**
	 * Returns the name of a page to add users to a *new* project.
	 *
	 * @param model
	 * 		{@link Model}
	 * @param projectId
	 * 		the id of the project to find the metadata for.
	 *
	 * @return The name of the add users to new project page.
	 */
	@RequestMapping("/{projectId}/metadata")
	public String getProjectMetadataPage(final Model model, final Principal principal, @PathVariable long projectId)
			throws IOException {
		Project project = projectService.read(projectId);

		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_METADATA);
		return PROJECT_METADATA_PAGE;
	}

	@RequestMapping(value = "/{projectId}/metadata/edit", method = RequestMethod.GET)
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
			model.addAttribute("maxFileSize", WebConfigurer.MAX_UPLOAD_SIZE);
			model.addAttribute("maxFileSizeString", fileSizeConverter.convert(WebConfigurer.MAX_UPLOAD_SIZE));
			model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_METADATA);
			return PROJECT_METADATA_EDIT_PAGE;
		} else {
			throw new AccessDeniedException("Do not have permissions to modify this project.");
		}
	}

	@RequestMapping(value = "/{projectId}/referenceFiles", method = RequestMethod.GET)
	public String getProjectReferenceFilesPage(final Model model, final Principal principal,
			@PathVariable long projectId) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute("project", project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_REFERENCE);
		return PROJECT_REFERENCE_FILES_PAGE;
	}

	@RequestMapping(value = "/{projectId}/metadata/edit", method = RequestMethod.POST)
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
	 * Handles AJAX request for getting a list of projects available to the logged in user. Produces JSON.
	 *
	 * @param principal
	 * 		{@link Principal} The currently authenticated users
	 * @param start
	 * 		The start position in the list to page.
	 * @param length
	 * 		The size of the page to display.
	 * @param draw
	 * 		Id for the table to draw, this must be returned.
	 * @param sortColumn
	 * 		The id for the column to sort by.
	 * @param direction
	 * 		The direction of the sort.
	 * @param searchValue
	 * 		Any search terms.
	 *
	 * @return JSON value of the page data.
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxProjectListForUser(
			final Principal principal,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = ProjectsDataTable.REQUEST_PARAM_SORT_COLUMN,
					defaultValue = ProjectsDataTable.SORT_DEFAULT_COLUMN) Integer sortColumn,
			@RequestParam(value = ProjectsDataTable.REQUEST_PARAM_SORT_DIRECTION,
					defaultValue = ProjectsDataTable.SORT_DEFAULT_DIRECTION) String direction,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {
		int pageNumber = ProjectsDataTable.getPageNumber(start, length);
		Sort.Direction sortDirection = ProjectsDataTable.getSortDirection(direction);
		String sortString = ProjectsDataTable.getSortStringFromColumnID(sortColumn);

		User user = userService.getUserByUsername(principal.getName());

		Page<ProjectUserJoin> page = projectService.searchProjectUsers(
				ProjectUserJoinSpecification.searchProjectNameWithUser(searchValue, user), pageNumber, length,
				sortDirection, sortString);
		List<ProjectUserJoin> projectList = page.getContent();

		return getProjectsDataMap(projectList, draw, page.getTotalElements(), sortColumn, sortDirection);
	}

	/**
	 * Handles AJAX request for getting a list of projects available to the admin user. Produces JSON.
	 *
	 * @param start
	 * 		The start position in the list to page.
	 * @param length
	 * 		The size of the page to display.
	 * @param draw
	 * 		Id for the table to draw, this must be returned.
	 * @param sortColumn
	 * 		The id for the column to sort by.
	 * @param direction
	 * 		The direction of the sort.
	 * @param searchValue
	 * 		Any search terms.
	 *
	 * @return JSON value of the page data.
	 */
	@RequestMapping(value = "/ajax/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public @ResponseBody Map<String, Object> getAjaxProjectListForAdmin(
			@RequestParam(ProjectsAdminDataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(ProjectsAdminDataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(ProjectsAdminDataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = ProjectsAdminDataTable.REQUEST_PARAM_SORT_COLUMN,
					defaultValue = ProjectsAdminDataTable.SORT_DEFAULT_COLUMN) Integer sortColumn,
			@RequestParam(value = ProjectsAdminDataTable.REQUEST_PARAM_SORT_DIRECTION,
					defaultValue = ProjectsAdminDataTable.SORT_DEFAULT_DIRECTION) String direction,
			@RequestParam(ProjectsAdminDataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {

		int pageNumber = ProjectsAdminDataTable.getPageNumber(start, length);
		Sort.Direction sortDirection = ProjectsAdminDataTable.getSortDirection(direction);
		String sortString = ProjectsAdminDataTable.getSortStringFromColumnID(sortColumn);

		// Get the page information
		Page<Project> page = projectService.search(ProjectSpecification.searchProjectName(searchValue), pageNumber,
				length, sortDirection, sortString);

		Map<String, Object> map = new HashMap<>();
		map.put(ProjectsAdminDataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(ProjectsAdminDataTable.RESPONSE_PARAM_RECORDS_TOTAL, page.getTotalElements());
		map.put(ProjectsAdminDataTable.RESPONSE_PARAM_RECORDS_FILTERED, page.getTotalElements());

		// Create the format required by DataTable
		List<Map<String, String>> projectsData = new ArrayList<>();
		for (Project p : page.getContent()) {
			Map<String, String> l = new HashMap<>();

			l.put("id", p.getId().toString());
			l.put("name", p.getName());
			l.put("organism", p.getOrganism());
			l.put("samples", String.valueOf(sampleService.getSamplesForProject(p).size()));
			l.put("members", String.valueOf(userService.getUsersForProject(p).size()));
			l.put("dateCreated", dateFormatter.print(p.getCreatedDate(), LocaleContextHolder.getLocale()));
			l.put("dateModified", p.getModifiedDate().toString());
			projectsData.add(l);
		}
		map.put(ProjectsDataTable.RESPONSE_PARAM_DATA, projectsData);
		map.put(ProjectsDataTable.RESPONSE_PARAM_SORT_COLUMN, sortColumn);
		map.put(ProjectsDataTable.RESPONSE_PARAM_SORT_DIRECTION, sortDirection);

		return map;
	}

	/**
	 * Search for taxonomy terms. This method will return a map of found taxonomy terms and their child nodes.
	 * <p/>
	 * Note: If the search term was not included in the results, it will be added as an option
	 *
	 * @param searchTerm
	 * 		The term to find taxa for
	 *
	 * @return A List<Map<String,Object>> which will contain a taxonomic tree of matching terms
	 */
	@RequestMapping("/ajax/taxonomy/search")
	@ResponseBody
	public List<Map<String, Object>> searchTaxonomy(@RequestParam String searchTerm) {
		Collection<TreeNode<String>> search = taxonomyService.search(searchTerm);

		TreeNode<String> searchTermNode = new TreeNode<>(searchTerm);

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
	 * Generates a map of project information for the {@link ProjectsDataTable}
	 *
	 * @param projectList
	 * 		a List of {@link ProjectUserJoin} for the current user.
	 * @param draw
	 * 		property sent from {@link ProjectsDataTable} as the table to render information to.
	 * @param totalElements
	 * 		Total number of elements that could go into the table.
	 * @param sortColumn
	 * 		Column to sort by.
	 * @param sortDirection
	 * 		Direction to sort the column
	 *
	 * @return Map containing the information to put into the {@link ProjectsDataTable}
	 */
	public Map<String, Object> getProjectsDataMap(List<ProjectUserJoin> projectList, int draw, long totalElements,
			int sortColumn, Sort.Direction sortDirection) {
		Map<String, Object> map = new HashMap<>();
		map.put(ProjectsDataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(ProjectsDataTable.RESPONSE_PARAM_RECORDS_TOTAL, totalElements);
		map.put(ProjectsDataTable.RESPONSE_PARAM_RECORDS_FILTERED, totalElements);

		// Create the format required by DataTable
		List<Map<String, String>> projectsData = new ArrayList<>();
		for (ProjectUserJoin projectUserJoin : projectList) {
			Project p = projectUserJoin.getSubject();
			String role = projectUserJoin.getProjectRole() != null ? projectUserJoin.getProjectRole().toString() : "";
			Map<String, String> l = new HashMap<>();

			l.put("id", p.getId().toString());
			l.put("name", p.getName());
			l.put("organism", p.getOrganism());
			l.put("role", role);
			l.put("samples", String.valueOf(sampleService.getSamplesForProject(p).size()));
			l.put("members", String.valueOf(userService.getUsersForProject(p).size()));
			l.put("dateCreated", dateFormatter.print(p.getCreatedDate(), LocaleContextHolder.getLocale()));
			l.put("dateModified", p.getModifiedDate().toString());
			projectsData.add(l);
		}
		map.put(ProjectsDataTable.RESPONSE_PARAM_DATA, projectsData);
		map.put(ProjectsDataTable.RESPONSE_PARAM_SORT_COLUMN, sortColumn);
		map.put(ProjectsDataTable.RESPONSE_PARAM_SORT_DIRECTION, sortDirection);
		return map;
	}

	@RequestMapping(value = "/ajax/{projectId}/referenceFiles")
	public @ResponseBody List<Map<String, String>> getProjectReferenceFiles(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		List<Join<Project, ReferenceFile>> projectFileJoin = referenceFileService.getReferenceFilesForProject(project);
		List<Map<String, String>> response = new ArrayList<>();
		for (Join<Project, ReferenceFile> join : projectFileJoin) {
			ReferenceFile file = join.getObject();
			Map<String, String> map = new HashMap<>();
			map.put("id", file.getId().toString());
			map.put("text", file.getLabel());
			response.add(map);
		}
		return response;
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
	 * <p/>
	 * /** Recursively transform a {@link TreeNode} into a json parsable map object
	 *
	 * @param node
	 * 		The node to transform
	 *
	 * @return A Map<String,Object> which may contain more children
	 */
	private Map<String, Object> transformTreeNode(TreeNode<String> node) {
		Map<String, Object> current = new HashMap<>();
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
