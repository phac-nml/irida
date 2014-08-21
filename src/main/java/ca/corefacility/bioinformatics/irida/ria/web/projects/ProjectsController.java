package ca.corefacility.bioinformatics.irida.ria.web.projects;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
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

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.config.WebConfigurer;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectSamplesDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsAdminDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
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
	private static final String ACTIVE_NAV_SAMPLES = "samples";

	private static final String PROJECT_NAME_PROPERTY = "name";

	// private static final String ACTIVE_NAV_ANALYSIS = "analysis";

	// Page Names
	private static final String PROJECTS_DIR = "projects/";
	public static final String LIST_PROJECTS_PAGE = PROJECTS_DIR + "projects";
	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";
	public static final String SPECIFIC_PROJECT_PAGE = PROJECTS_DIR + "project_details";
	public static final String CREATE_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_new";
	public static final String PROJECT_METADATA_PAGE = PROJECTS_DIR + "project_metadata";
	public static final String PROJECT_METADATA_EDIT_PAGE = PROJECTS_DIR + "project_metadata_edit";
	public static final String PROJECT_SAMPLES_PAGE = PROJECTS_DIR + "project_samples";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final SequenceFileService sequenceFileService;
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
			SequenceFileService sequenceFileService, ProjectControllerUtils projectControllerUtils,
			ReferenceFileService referenceFileService, TaxonomyService taxonomyService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.sequenceFileService = sequenceFileService;
		this.projectControllerUtils = projectControllerUtils;
		this.referenceFileService = referenceFileService;
		this.taxonomyService = taxonomyService;
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
	}

	/**
	 * Request for the page to display a list of all projects available to the
	 * currently logged in user.
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
	 * @param projectId The id for the project to show details for.
	 * @param model     Spring model to populate the html page.
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
	 * @param model {@link Model}
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
	 * Creates a new project and displays a list of users for the user to add to
	 * the project
	 *
	 * @param model              {@link Model}
	 * @param name               String name of the project
	 * @param organism           Organism name
	 * @param projectDescription Brief description of the project
	 * @param remoteURL          URL for the project wiki
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
	 * @param model     {@link Model}
	 * @param projectId the id of the project to find the metadata for.
	 * @return The name of the add users to new project page.
	 */
	@RequestMapping("/{projectId}/metadata")
	public String getProjectMetadataPage(final Model model, final Principal principal, @PathVariable long projectId)
			throws IOException {
		Project project = projectService.read(projectId);

		// Let's add the reference files
		List<Map<String, String>> referenceFiles = getReferenceFileData(project);

		model.addAttribute("project", project);
		model.addAttribute("referenceFiles", referenceFiles);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_METADATA);
		return PROJECT_METADATA_PAGE;
	}

	@RequestMapping(value = "/{projectId}/metadata/edit", method = RequestMethod.GET)
	public String getProjectMetadataEditPage(final Model model, final Principal principal,
			@PathVariable long projectId) throws IOException {
		Project project = projectService.read(projectId);
		User user = userService.getUserByUsername(principal.getName());
		if (user.getSystemRole().equals(Role.ROLE_ADMIN) || projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER)) {
			if (!model.containsAttribute("errors")) {
				model.addAttribute("errors", new HashMap<>());
			}
			projectControllerUtils.getProjectTemplateDetails(model, principal, project);

			// Let's add the reference files
			List<Map<String, String>> referenceFiles = getReferenceFileData(project);

			model.addAttribute("referenceFiles", referenceFiles);
			model.addAttribute("project", project);
			model.addAttribute("maxFileSize", WebConfigurer.MAX_UPLOAD_SIZE);
			model.addAttribute("maxFileSizeString", fileSizeConverter.convert(WebConfigurer.MAX_UPLOAD_SIZE));
			model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_METADATA);
			return PROJECT_METADATA_EDIT_PAGE;
		} else {
			throw new AccessDeniedException("Do not have permissions to modify this project.");
		}
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

	@RequestMapping("/{projectId}/samples")
	public String getProjectSamplesPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		// Set up the template information
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_SAMPLES);
		return PROJECT_SAMPLES_PAGE;
	}

	@RequestMapping(value = "/ajax/{projectId}/samples", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxProjectSamplesMap(
			@PathVariable Long projectId,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = ProjectSamplesDataTable.REQUEST_PARAM_SORT_COLUMN,
					defaultValue = ProjectSamplesDataTable.SORT_DEFAULT_COLUMN) Integer sortColumn,
			@RequestParam(value = ProjectSamplesDataTable.REQUEST_PARAM_SORT_DIRECTION,
					defaultValue = ProjectSamplesDataTable.SORT_DEFAULT_DIRECTION) String direction,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {
		Map<String, Object> response = new HashMap<>();
		Sort.Direction sortDirection = ProjectSamplesDataTable.getSortDirection(direction);
		String sortString = ProjectSamplesDataTable.getSortStringFromColumnID(sortColumn);

		int pageNum = ProjectSamplesDataTable.getPageNumber(start, length);
		try {
			Project project = projectService.read(projectId);
			Page<ProjectSampleJoin> page = sampleService.getSamplesForProjectWithName(project, searchValue, pageNum,
					length, sortDirection, sortString);
			List<Map<String, String>> samplesList = new ArrayList<>();
			for (Join<Project, Sample> join : page.getContent()) {
				Map<String, String> sMap = new HashMap<>();
				Sample s = join.getObject();
				sMap.put(ProjectSamplesDataTable.ID, s.getId().toString());
				sMap.put(ProjectSamplesDataTable.NAME, s.getSampleName());
				sMap.put(ProjectSamplesDataTable.NUM_FILES,
						String.valueOf(sequenceFileService.getSequenceFilesForSample(s).size()));
				sMap.put(ProjectSamplesDataTable.CREATED_DATE, Formats.DATE.format(join.getTimestamp()));
				samplesList.add(sMap);
			}
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_DATA, samplesList);
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_DRAW, draw);
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_RECORDS_FILTERED, page.getTotalElements());
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_RECORDS_TOTAL, page.getTotalElements());
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_SORT_COLUMN, sortColumn);
			response.put(ProjectSamplesDataTable.RESPONSE_PARAM_SORT_DIRECTION, sortDirection);
		} catch (Exception e) {
			logger.error("Error retrieving project sample information :" + e.getLocalizedMessage());
		}
		return response;
	}

	/**
	 * Handles AJAX request for getting a list of projects available to the
	 * logged in user. Produces JSON.
	 *
	 * @param principal   {@link Principal} The currently authenticated users
	 * @param start       The start position in the list to page.
	 * @param length      The size of the page to display.
	 * @param draw        Id for the table to draw, this must be returned.
	 * @param sortColumn  The id for the column to sort by.
	 * @param direction   The direction of the sort.
	 * @param searchValue Any search terms.
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
	 * Handles AJAX request for getting a list of projects available to the
	 * admin user. Produces JSON.
	 *
	 * @param start       The start position in the list to page.
	 * @param length      The size of the page to display.
	 * @param draw        Id for the table to draw, this must be returned.
	 * @param sortColumn  The id for the column to sort by.
	 * @param direction   The direction of the sort.
	 * @param searchValue Any search terms.
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

	@RequestMapping(value = "/ajax/{projectId}/samples/getids", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, List<String>> getAllProjectIds(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		List<String> sampleIdList = new ArrayList<>();
		List<Join<Project, Sample>> psj = sampleService.getSamplesForProject(project);
		for (Join<Project, Sample> join : psj) {
			sampleIdList.add(join.getObject().getId().toString());
		}
		Map<String, List<String>> result = new HashMap<>();
		result.put("ids", sampleIdList);
		return result;
	}

	/**
	 * Search for projects available for a user to copy samples to. If the user
	 * is an admin it will show all projects.
	 *
	 * @param projectId The current project id
	 * @param term      A search term
	 * @param pageSize  The size of the page requests
	 * @param page      The page number (0 based)
	 * @param principal The logged in user.
	 * @return a Map<String,Object> containing: total: total number of elements
	 * results: A Map<Long,String> of project IDs and project names.
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/available_projects")
	@ResponseBody
	public Map<String, Object> getProjectsAvailableToCopySamples(@PathVariable Long projectId,
			@RequestParam String term, @RequestParam int pageSize, @RequestParam int page, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());

		Map<Long, String> vals = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		if (user.getAuthorities().contains(Role.ROLE_ADMIN)) {
			Page<Project> projects = projectService.search(ProjectSpecification.searchProjectName(term), page,
					pageSize, Direction.ASC, PROJECT_NAME_PROPERTY);
			for (Project p : projects) {
				vals.put(p.getId(), p.getName());
			}
			response.put("total", projects.getTotalElements());
		} else {
			// search for projects with a given name where the user is an owner
			Specification<ProjectUserJoin> spec = where(
					ProjectUserJoinSpecification.searchProjectNameWithUser(term, user)).and(
					ProjectUserJoinSpecification.getProjectJoinsWithRole(user, ProjectRole.PROJECT_OWNER));
			Page<ProjectUserJoin> projects = projectService.searchProjectUsers(spec, page, pageSize, Direction.ASC);
			for (ProjectUserJoin p : projects) {
				vals.put(p.getSubject().getId(), p.getSubject().getName());
			}
			response.put("total", projects.getTotalElements());
		}

		response.put("results", vals);

		return response;
	}

	/**
	 * Copy or move samples from one project to another
	 *
	 * @param projectId          The original project id
	 * @param sampleIds          The sample ids to move
	 * @param newProjectId       The new project id
	 * @param removeFromOriginal true/false whether to remove the samples from the original
	 *                           project
	 * @return A list of warnings
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/copy")
	@ResponseBody
	public Map<String, Object> copySampleToProject(@PathVariable Long projectId, @RequestParam List<Long> sampleIds,
			@RequestParam Long newProjectId, @RequestParam boolean removeFromOriginal) {
		Project originalProject = projectService.read(projectId);
		Project newProject = projectService.read(newProjectId);

		Map<String, Object> response = new HashMap<>();
		List<String> warnings = new ArrayList<>();

		int totalCopied = 0;

		for (Long sampleId : sampleIds) {
			Sample sample = sampleService.read(sampleId);
			try {
				projectService.addSampleToProject(newProject, sample);
				logger.trace("Copied sample " + sampleId + " to project " + newProjectId);
				totalCopied++;

			} catch (EntityExistsException ex) {
				logger.warn("Attempted to add sample " + sampleId + " to project " + newProjectId
						+ " where it already exists.", ex);

				warnings.add(sample.getLabel());
			}

			if (removeFromOriginal) {
				projectService.removeSampleFromProject(originalProject, sample);
				logger.trace("Removed sample " + sampleId + " from original project " + projectId);
			}
		}

		if (!warnings.isEmpty()) {
			response.put("warnings", warnings);
		}
		response.put("totalCopied", totalCopied);

		return response;
	}
	
	/**
	 * Search for taxonomy terms. This method will return a map of found
	 * taxonomy terms and their child nodes.
	 * 
	 * Note: If the search term was not included in the results, it will be
	 * added as an option
	 * 
	 * @param searchTerm
	 *            The term to find taxa for
	 * @return A List<Map<String,Object>> which will contain a taxonomic tree of
	 *         matching terms
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
	 * @param projectList   a List of {@link ProjectUserJoin} for the current user.
	 * @param draw          property sent from {@link ProjectsDataTable} as the table to
	 *                      render information to.
	 * @param totalElements Total number of elements that could go into the table.
	 * @param sortColumn    Column to sort by.
	 * @param sortDirection Direction to sort the column
	 * @return Map containing the information to put into the
	 * {@link ProjectsDataTable}
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

	/**
	 * Remove a list of samples from a a Project.
	 *
	 * @param projectId Id of the project to remove the samples from
	 * @param sampleIds An array of samples to remove from a project
	 * @return Map containing either success or errors.
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/delete", produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> deleteProjectSamples(@PathVariable Long projectId,
			@RequestParam List<Long> sampleIds) {
		Project project = projectService.read(projectId);
		Map<String, Object> result = new HashMap<>();
		for (Long id : sampleIds) {
			try {
				Sample sample = sampleService.read(id);
				projectService.removeSampleFromProject(project, sample);
			} catch (EntityNotFoundException e) {
				result.put("error", "Cannot find sample with id: " + id);
			}

		}
		result.put("success", "DONE!");
		return result;
	}

	/**
	 * For a list of sample ids, this function will generate a map of {id, name}
	 *
	 * @param sampleIds A list of sample ids.
	 * @return A list of map of {id, name}
	 */
	@RequestMapping(value = "/ajax/getNamesFromIds", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> ajaxGetSampleNamesFromIds(@RequestParam List<Long> sampleIds) {
		List<Map<String, String>> resultList = new ArrayList<>();
		for (Long id : sampleIds) {
			Map<String, String> results = new HashMap<>();
			Sample sample = sampleService.read(id);
			results.put("id", id.toString());
			results.put("text", sample.getSampleName());
			resultList.add(results);
		}
		return resultList;
	}

	/**
	 * Merges a list of samples into either the first sample in the list with a
	 * new name if provided, or into the selected sample based on the id.
	 *
	 * @param projectId     The id for the project the samples belong to.
	 * @param sampleIds     A list of sample ids for samples to merge.
	 * @param mergeSampleId (Optional) The id of the sample to merge the other into.
	 * @param newName       (Optional) The new name for the final sample.
	 * @return
	 */
	@RequestMapping(value = "/ajax/{projectId}/samples/merge", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxSamplesMerge(@PathVariable Long projectId,
			@RequestParam List<Long> sampleIds, @RequestParam(required = false) Long mergeSampleId,
			@RequestParam(required = false) String newName) {
		Map<String, Object> result = new HashMap<>();
		Project project = projectService.read(projectId);
		Sample mergeIntoSample = null;
		// Determine if it is a new name or and existing sample
		try {
			if (sampleIds.contains(mergeSampleId)) {
				mergeIntoSample = sampleService.read(mergeSampleId);
				sampleIds.remove(mergeSampleId);
			} else {
				mergeIntoSample = sampleService.read(sampleIds.remove(0));
			}
		} catch (EntityNotFoundException e) {
			result.put("error", e.getLocalizedMessage());

		}
		// Rename if a new name is given
		if (!Strings.isNullOrEmpty(newName)) {
			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("sampleName", newName);
			try {
				mergeIntoSample = sampleService.update(mergeIntoSample.getId(), updateMap);
			} catch (ConstraintViolationException e) {
				result.put("error", getErrorsFromViolationException(e));
			}
		}
		if (!result.containsKey("error")) {
			Sample[] mergeSamples = new Sample[sampleIds.size()];
			for (int i = 0; i < sampleIds.size(); i++) {
				mergeSamples[i] = sampleService.read(sampleIds.get(i));
			}
			sampleService.mergeSamples(project, mergeIntoSample, mergeSamples);
			result.put("success", mergeIntoSample.getSampleName());
		}
		return result;
	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings
	 * for displaing in the UI.
	 *
	 * @param e {@link ConstraintViolationException} for the form submitted.
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
	 * Get the information about a projects reference files in a format that can be used by the UI.
	 *
	 * @param project {@link Project} Currently viewed project.
	 * @return List of reference file info.
	 * @throws IOException
	 */
	private List<Map<String, String>> getReferenceFileData(Project project) throws IOException {
		List<Join<Project, ReferenceFile>> joinList = referenceFileService.getReferenceFilesForProject(project);
		List<Map<String, String>> mapList = new ArrayList<>();
		for (Join<Project, ReferenceFile> join : joinList) {
			ReferenceFile file = join.getObject();
			Map<String, String> map = new HashMap<>();
			map.put("id", file.getId().toString());
			map.put("label", file.getLabel());
			map.put("createdDate", dateFormatter.print(file.getCreatedDate(), LocaleContextHolder.getLocale()));
			Path path = file.getFile();
			long size = 0;
			if (Files.exists(path)) {
				size = Files.size(path);
			}
			map.put("size", fileSizeConverter.convert(size));
			mapList.add(map);
		}
		return mapList;
	}
	
	/**
	 * Recursively transform a {@link TreeNode} into a json parsable map object
	 * 
	 * @param node
	 *            The node to transform
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
