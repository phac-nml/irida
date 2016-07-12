package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.core.export.CsvExport;
import com.github.dandelion.datatables.core.export.ExportConf;
import com.github.dandelion.datatables.core.export.ExportUtils;
import com.github.dandelion.datatables.core.export.HtmlTableBuilder;
import com.github.dandelion.datatables.core.export.ReservedFormat;
import com.github.dandelion.datatables.core.html.HtmlTable;
import com.github.dandelion.datatables.extras.export.poi.XlsxExport;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.config.web.IridaRestApiWebConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.ProjectsDatatableUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

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
	private static final String ACTIVE_NAV_SETTINGS = "settings";

	// Page Names
	public static final String PROJECTS_DIR = "projects/";
	public static final String LIST_PROJECTS_PAGE = PROJECTS_DIR + "projects";
	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";
	public static final String SPECIFIC_PROJECT_PAGE = PROJECTS_DIR + "project_details";
	public static final String CREATE_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_new";
	public static final String SYNC_NEW_PROJECT_PAGE = PROJECTS_DIR + "project_sync";
	public static final String PROJECT_METADATA_PAGE = PROJECTS_DIR + "project_metadata";
	public static final String PROJECT_METADATA_EDIT_PAGE = PROJECTS_DIR + "project_metadata_edit";
	public static final String PROJECT_SAMPLES_PAGE = PROJECTS_DIR + "project_samples";
	public static final String PROJECT_ACTIVITY_PAGE = PROJECTS_DIR + "project_details";
	public static final String PROJECT_REFERENCE_FILES_PAGE = PROJECTS_DIR + "project_reference";
	public static final String PROJECT_SETTINGS_PAGE = PROJECTS_DIR + "project_settings";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final ProjectControllerUtils projectControllerUtils;
	private final TaxonomyService taxonomyService;
	private final MessageSource messageSource;
	private final ProjectRemoteService projectRemoteService;
	private RemoteAPIService remoteApiService;
	
	@Value("${file.upload.max_size}")
	private final Long MAX_UPLOAD_SIZE = IridaRestApiWebConfig.UNLIMITED_UPLOAD_SIZE;

	/*
	 * Converters
	 */
	Formatter<Date> dateFormatter;
	FileSizeConverter fileSizeConverter;

	// HTTP session variable name for Galaxy callback variable
	public static final String GALAXY_CALLBACK_VARIABLE_NAME = "galaxyExportToolCallbackURL";
	public static final String GALAXY_CLIENT_ID_NAME = "galaxyExportToolClientID";

	// CONSTANTS
	private final List<Map<String, String>> EXPORT_TYPES = ImmutableList.of(
			ImmutableMap.of("format", ReservedFormat.XLSX, "name", "Excel"),
			ImmutableMap.of("format", ReservedFormat.CSV, "name", "CSV")
	);



	@Autowired
	public ProjectsController(ProjectService projectService, SampleService sampleService, UserService userService, ProjectRemoteService projectRemoteService,
			ProjectControllerUtils projectControllerUtils, TaxonomyService taxonomyService, RemoteAPIService remoteApiService, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.projectRemoteService = projectRemoteService;
		this.projectControllerUtils = projectControllerUtils;
		this.taxonomyService = taxonomyService;
		this.dateFormatter = new DateFormatter();
		this.messageSource = messageSource;
		this.remoteApiService = remoteApiService;
		this.fileSizeConverter = new FileSizeConverter();
	}

	/**
	 * Request for the page to display a list of all projects available to the currently logged in user.
	 *
	 * @param model
	 * 		The model to add attributes to for the template.
	 * @param galaxyCallbackURL
	 * 		The URL at which to call the Galaxy export tool
	 * @param galaxyClientID
	 * 		The OAuth2 client ID of the Galaxy instance to export to
	 * @param httpSession
	 * 		The user's session
	 *
	 * @return The name of the page.
	 */
	@RequestMapping("/projects")
	public String getProjectsPage(Model model,
			@RequestParam(value = "galaxyCallbackUrl", required = false) String galaxyCallbackURL,
			@RequestParam(value = "galaxyClientID", required = false) String galaxyClientID, HttpSession httpSession) {
		model.addAttribute("ajaxURL", "/projects/ajax/list");
		model.addAttribute("exportTypes", EXPORT_TYPES);
		model.addAttribute("isAdmin", false);

		// External exporting functionality
		if (galaxyCallbackURL != null && galaxyClientID != null) {
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
		model.addAttribute("isAdmin", true);
		model.addAttribute("exportTypes", EXPORT_TYPES);
		return LIST_PROJECTS_PAGE;
	}

	/**
	 * Request for a specific project details page.
	 *
	 * @param projectId
	 * 		The id for the project to show details for.
	 * @param model
	 * 		Spring model to populate the html page.
	 * @param principal
	 * 		a reference to the logged in user.
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
	 * Request for a {@link Project} settings page
	 * 
	 * @param projectId
	 *            the ID of the {@link Project} to read
	 * @param model
	 *            Model for the view
	 * @param principal
	 *            Logged in user
	 * @return name of the project settings page
	 */
	@RequestMapping(value = "/projects/{projectId}/settings")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public String getProjectSettingsPage(@PathVariable Long projectId, final Model model, final Principal principal) {
		logger.debug("Getting project settings for [Project " + projectId + "]");
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute("frequencies", ProjectSyncFrequency.values());
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_SETTINGS);
		return PROJECT_SETTINGS_PAGE;
	}

	/**
	 * Update the project assembly setting for the {@link Project}
	 * 
	 * @param projectId
	 *            the ID of a {@link Project}
	 * @param assemble
	 *            Whether or not to do automated assemblies
	 * @param model
	 *            Model for the view
	 * @return success message if successful
	 */
	@RequestMapping(value = "/projects/{projectId}/settings/assemble", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateAssemblySetting(@PathVariable Long projectId, @RequestParam boolean assemble,
			final Model model, Locale locale) {
		Project read = projectService.read(projectId);
		
		Map<String,Object> updates = new HashMap<>();
		updates.put("assembleUploads", assemble);
		
		projectService.updateProjectSettings(read, updates);
		

		String message = null;
		if (assemble) {
			message = messageSource.getMessage("project.settings.notifications.assemble.enabled",
					new Object[] { read.getLabel() }, locale);
		} else {
			message = messageSource.getMessage("project.settings.notifications.assemble.disabled",
					new Object[] { read.getLabel() }, locale);
		}

		return ImmutableMap.of("result", message);
	}

	/**
	 * Update the project sync settings
	 * 
	 * @param projectId
	 *            the project id to update
	 * @param frequency
	 *            the sync frequency to set
	 * @param forceSync
	 *            Set the project's sync status to MARKED
	 * @param locale
	 *            user's locale
	 * @return result message if successful
	 */
	@RequestMapping(value = "/projects/{projectId}/settings/sync", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateProjectSyncSettings(@PathVariable Long projectId,
			@RequestParam(required = false) ProjectSyncFrequency frequency,
			@RequestParam(required = false, defaultValue = "false") boolean forceSync, Locale locale) {
		Project read = projectService.read(projectId);
		RemoteStatus remoteStatus = read.getRemoteStatus();

		Map<String, Object> updates = new HashMap<>();
		
		if(frequency != null){
			updates.put("syncFrequency", frequency);
		}
		
		if(forceSync){
			remoteStatus.setSyncStatus(SyncStatus.MARKED);
			updates.put("remoteStatus", remoteStatus);
		}

		projectService.updateProjectSettings(read, updates);

		String message = messageSource.getMessage("project.settings.notifications.sync", new Object[] {}, locale);

		return ImmutableMap.of("result", message);
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
	 * Get the page to synchronize remote projects
	 * 
	 * @param model
	 *            Model to render for view
	 * @return Name of the project sync page
	 */
	@RequestMapping(value = "/projects/synchronize", method = RequestMethod.GET)
	public String getSynchronizeProjectPage(final Model model) {
		
		Iterable<RemoteAPI> apis = remoteApiService.findAll();
		model.addAttribute("apis",apis);
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
	 * @param url
	 *            the URL of the remote project
	 * @return Redirect to the new project. If an oauth exception occurs it will
	 *         be forwarded back to the creation page.
	 */
	@RequestMapping(value = "/projects/synchronize", method = RequestMethod.POST)
	public String syncProject(@RequestParam String url, @RequestParam ProjectSyncFrequency syncFrequency, Model model) {

		try {
			Project read = projectRemoteService.read(url);
			read.setId(null);
			read.getRemoteStatus().setSyncStatus(SyncStatus.MARKED);
			read.setSyncFrequency(syncFrequency);

			read = projectService.create(read);

			return "redirect:/projects/" + read.getId() + "/metadata";
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
	 * @param apiId
	 *            the local ID of the {@link RemoteAPI}
	 * @return a List of {@link Project}s
	 */
	@RequestMapping(value = "/projects/ajax/api/{apiId}")
	@ResponseBody
	public List<Project> ajaxGetProjectsForApi(@PathVariable Long apiId) {
		RemoteAPI api = remoteApiService.read(apiId);
		return projectRemoteService.listProjectsForAPI(api);
	}

	/**
	 * Creates a new project and displays a list of users for the user to add to
	 * the project
	 *
	 * @param model
	 *            {@link Model}
	 * @param name
	 *            String name of the project
	 * @param organism
	 *            Organism name
	 * @param projectDescription
	 *            Brief description of the project
	 * @param remoteURL
	 *            URL for the project wiki
	 * @param assemble
	 *            Enable or disable automated assemblies
	 *
	 * @return The name of the add users to project page
	 */
	@RequestMapping(value = "/projects/new", method = RequestMethod.POST)
	public String createNewProject(final Model model, @RequestParam(required = false, defaultValue = "") String name,
			@RequestParam(required = false, defaultValue = "") String organism,
			@RequestParam(required = false, defaultValue = "") String projectDescription,
			@RequestParam(required = false, defaultValue = "") String remoteURL,
			@RequestParam(required = false, defaultValue = "false") boolean assemble) {

		Project p = new Project(name);
		p.setOrganism(organism);
		p.setProjectDescription(projectDescription);
		p.setRemoteURL(remoteURL);
		p.setAssembleUploads(assemble);

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
	 * 		{@link Model}
	 * @param principal
	 * 		a reference to the logged in user.
	 * @param projectId
	 * 		the id of the project to find the metadata for.
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
	public String getProjectMetadataEditPage(final Model model, final Principal principal, @PathVariable long projectId)
			throws IOException {
		Project project = projectService.read(projectId);
		User user = userService.getUserByUsername(principal.getName());
		if (user.getSystemRole().equals(Role.ROLE_ADMIN)
				|| projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER)) {
			if (!model.containsAttribute("errors")) {
				model.addAttribute("errors", new HashMap<>());
			}
			projectControllerUtils.getProjectTemplateDetails(model, principal, project);

			model.addAttribute("project", project);
			model.addAttribute("maxFileSize", MAX_UPLOAD_SIZE);
			if (MAX_UPLOAD_SIZE > 0) {
				model.addAttribute("maxFileSizeString", fileSizeConverter.convert(MAX_UPLOAD_SIZE));
			} else {
				model.addAttribute("maxFileSizeString", "∞");
			}
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
	 *
	 * @param criterias
	 * 		the search criteria to apply
	 * @param principal
	 * 		{@link Principal} currently logged in user.
	 *
	 * @return {@link List} of project {@link Map}
	 */
	@RequestMapping("/projects/ajax/list")
	@ResponseBody
	public DatatablesResponse<Map<String, Object>> getAjaxProjectList(@DatatablesParams DatatablesCriterias criterias,
			final Principal principal) {
		final String search = criterias.getSearch();
		final Map<String, String> searchMap = ProjectsDatatableUtils.generateSearchMap(criterias.getColumnDefs());

		final String organismName = searchMap.getOrDefault("organism", "");
		final String projectName = searchMap.getOrDefault("name", "");

		final Map<String, Object> sortProperties = ProjectsDatatableUtils.getSortProperties(criterias);
		final Integer currentPage = ProjectsDatatableUtils.getCurrentPage(criterias);
		final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
		final String sortName = sortProperties.get("sort_string").toString();

		final Page<Project> page = projectService.findProjectsForUser(search, projectName, organismName, currentPage,
				criterias.getLength(), direction, sortName);
		List<Map<String, Object>> projects = new ArrayList<>(page.getSize());
		projects.addAll(page.getContent().stream().map(join -> createProjectMap(join))
				.collect(Collectors.toList()));
		DataSet<Map<String, Object>> dataSet = new DataSet<>(projects, page.getTotalElements(),
				page.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}

	/**
	 * Admin mapping to get a list of all project they are on.
	 *
	 * @param criterias
	 * 			the search criteria to apply
	 * @return {@link List} of project {@link Map}
	 */
	@RequestMapping("/projects/admin/ajax/list")
	@ResponseBody
	public DatatablesResponse<Map<String, Object>> getAjaxAdminProjectsList(
			@DatatablesParams DatatablesCriterias criterias) {
		final String search = criterias.getSearch();
		final Map<String, String> searchMap = ProjectsDatatableUtils.generateSearchMap(criterias.getColumnDefs());
		final String organismName = searchMap.getOrDefault("organism", "");
		final String projectName = searchMap.getOrDefault("name", "");
		
		Map<String, Object> sortProperties = ProjectsDatatableUtils.getSortProperties(criterias);
		final Integer currentPage = ProjectsDatatableUtils.getCurrentPage(criterias);
		final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
		final String sortName = sortProperties.get("sort_string").toString();

		final Page<Project> page = projectService.findAllProjects(search, projectName, organismName, currentPage,
				criterias.getLength(), direction, sortName);
		List<Map<String, Object>> projects = new ArrayList<>(page.getSize());
		projects.addAll(page.getContent().stream().map(this::createProjectMap).collect(Collectors.toList()));
		DataSet<Map<String, Object>> dataSet = new DataSet<>(projects, page.getTotalElements(),
				page.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}

	/**
	 * Get a listing of all project available to current user.
	 *
	 * @param type
	 * 		{@link String} Type of export.  See {@link ReservedFormat}
	 * @param isAdmin
	 * 		{@link Boolean} If the current user is viewing the admin section
	 * @param request
	 * 		{@link HttpServletRequest}
	 * @param response
	 * 		{@link HttpServletResponse}
	 * @param locale
	 * 		{@link Locale} Current users locale
	 *
	 * @throws IOException
	 */
	@RequestMapping("/projects/ajax/export")
	public void exportProjectsTableAsExcel(@RequestParam(value = "dtf") String type,
			@RequestParam(required = false, defaultValue = "false", value = "admin") Boolean isAdmin, HttpServletRequest request,
			HttpServletResponse response, Principal principal, Locale locale) throws IOException {
		List<Project> projects;

		// If viewing the admin projects page give the user all the projects.
		if (isAdmin) {
			projects = (List<Project>) projectService.findAll();
		}
		// If on the users projects page, give the user their projects.
		else {
			projects = new ArrayList<>();
			User user = userService.getUserByUsername(principal.getName());
			List<Join<Project, User>> projectsForUser = projectService.getProjectsForUser(user);
			for(Join<Project, User> join : projectsForUser) {
				projects.add(join.getSubject());
			}
		}

		List<Map<String, Object>> projectMap = projects.stream().map(this::createProjectMap)
				.collect(Collectors.toList());

		// Build export configuration
		ExportConf exportConf;
		if (type.equals(ReservedFormat.XLSX)) {
			exportConf = new ExportConf.Builder(ReservedFormat.XLSX)
					.header(true)
					.exportClass(new XlsxExport())
					.build();
		} else {
			exportConf = new ExportConf.Builder(ReservedFormat.CSV)
					.header(true)
					.exportClass(new CsvExport())
					.build();
		}

		// Format the file name based on ISO standard date format.
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat(
				messageSource.getMessage("date.iso-8601", null, locale));
		exportConf.setFileName("IRIDA_projects_" + dateFormat.format(date));

		// Build the table to export
		HtmlTable table = new HtmlTableBuilder<Map<String, Object>>()
				.newBuilder("projects", projectMap, request, exportConf)
				.column().fillWithProperty("id").title("ID")
				.column().fillWithProperty("name").title("Name")
				.column().fillWithProperty("description").title("Description")
				.column().fillWithProperty("organism").title("Organism")
				.column().fillWithProperty("samples").title("Samples")
				.column().fillWithProperty("createdDate").title("Created Date")
				.column().fillWithProperty("modifiedDate").title("Modified Date")
				.build();

		ExportUtils.renderExport(table, exportConf, response);
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

	@ExceptionHandler(ProjectWithoutOwnerException.class)
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

	/**
	 * Extract the details of the a {@link Project} into a {@link Map} which is consumable by the UI
	 *
	 * @param project
	 * 		{@link Project}
	 *
	 * @return {@link Map}
	 */
	public Map<String, Object> createProjectMap(Project project) {
		Map<String, Object> map = new HashMap<>();

		map.put("id", project.getId());
		map.put("name", project.getName());
		map.put("description", Strings.isNullOrEmpty(project.getProjectDescription()) ? "" : project.getProjectDescription());
		map.put("organism", Strings.isNullOrEmpty(project.getOrganism()) ? "" : project.getOrganism());
		map.put("samples", sampleService.getNumberOfSamplesForProject(project));
		map.put("createdDate", project.getCreatedDate());
		map.put("modifiedDate", project.getModifiedDate());
		map.put("remote", project.isRemote());

		return map;
	}
}
