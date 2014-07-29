package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectSamplesDataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsDataTable;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.*;

/**
 * Controller for all project related views
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
	private static final String ACTIVE_NAV_MEMBERS = "members";
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
	public static final String PROJECT_SAMPLES_COMBINE_TEMPLATE = PROJECTS_DIR + "partials/combine_samples";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	private static final List<ProjectRole> projectRoles = ImmutableList.of(ProjectRole.PROJECT_USER,
			ProjectRole.PROJECT_OWNER);

	// Services
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final UserService userService;
	private final SequenceFileService sequenceFileService;

	@Autowired
	public ProjectsController(ProjectService projectService, SampleService sampleService, UserService userService,
			SequenceFileService sequenceFileService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
		this.sequenceFileService = sequenceFileService;
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
	 * @param projectId
	 *            The id for the project to show details for.
	 * @param model
	 *            Spring model to populate the html page.
	 * @return The name of the project details page.
	 */
	@RequestMapping(value = "/{projectId}")
	public String getProjectSpecificPage(@PathVariable Long projectId, final Model model, final Principal principal) {
		logger.debug("Getting project information for [Project " + projectId + "]");
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_DASHBOARD);
		return SPECIFIC_PROJECT_PAGE;
	}

	/**
	 * Gets the name of the template for the project members page. Populates the
	 * template with standard info.
	 *
	 * @param model
	 *            {@link Model}
	 * @param principal
	 *            {@link Principal}
	 * @param projectId
	 *            Id for the project to show the users for
	 * @return The name of the project members page.
	 */
	@RequestMapping(value="/{projectId}/members", method=RequestMethod.GET)
	public String getProjectUsersPage(final Model model, final Principal principal, @PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_MEMBERS);
		model.addAttribute("projectRoles", projectRoles);
		return PROJECT_MEMBERS_PAGE;
	}
	
	@RequestMapping(value="/{projectId}/members", method=RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@ResponseBody
	public void addProjectMember(@PathVariable Long projectId, @RequestParam Long userId, @RequestParam String projectRole){
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		ProjectRole role = ProjectRole.fromString(projectRole);
		
		projectService.addUserToProject(project, user, role);
	}

	@RequestMapping("/{projectId}/ajax/availablemembers")
	@ResponseBody
	public Map<Long, String> getUsersAvailableForProject(@PathVariable Long projectId, @RequestParam String term) {
		Project project = projectService.read(projectId);
		List<User> usersAvailableForProject = userService.getUsersAvailableForProject(project);
		Map<Long, String> users = new HashMap<>();
		for (User user : usersAvailableForProject) {
			if (user.getLabel().toLowerCase().contains(term.toLowerCase())) {
				users.put(user.getId(), user.getLabel());
			}
		}

		return users;
	}

	/**
	 * Remove a user from a project
	 * 
	 * @param projectId
	 *            The project to remove from
	 * @param userId
	 *            The user to remove
	 * @return
	 * @throws ProjectWithoutOwnerException 
	 * @throws ProjectSelfEditException 
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@RequestMapping("{projectId}/members/remove")
	@ResponseBody
	public void removeUser(@PathVariable Long projectId, @RequestParam Long userId, Principal principal) throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		
		if(user.getUsername().equals(principal.getName())){
			throw new ProjectSelfEditException("You cannot remove yourself from a project.");
		}

		projectService.removeUserFromProject(project, user);
	}

	/**
	 * Update a user's role on a project
	 * 
	 * @param projectId
	 *            The ID of the project
	 * @param userId
	 *            The ID of the user
	 * @param projectRole
	 *            The role to set
	 * @throws ProjectWithoutOwnerException 
	 * @throws ProjectSelfEditException 
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@RequestMapping("{projectId}/members/editrole")
	@ResponseBody
	public void updateUserRole(@PathVariable Long projectId, @RequestParam Long userId,
			@RequestParam String projectRole, Principal principal) throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		
		if(user.getUsername().equals(principal.getName())){
			throw new ProjectSelfEditException("You cannot edit your own role on a project.");
		}

		ProjectRole role = ProjectRole.fromString(projectRole);

		projectService.updateUserProjectRole(project, user, role);
	}

	/**
	 * Gets the name of the template for the new project page
	 *
	 * @param model
	 *            {@link Model}
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
	 *            {@link Model}
	 * @param projectId
	 *            the id of the project to find the metadata for.
	 * @return The name of the add users to new project page.
	 */
	@RequestMapping("/{projectId}/metadata")
	public String getProjectMetadataPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		getProjectTemplateDetails(model, principal, project);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_METADATA);
		return PROJECT_METADATA_PAGE;
	}

	@RequestMapping(value = "/{projectId}/metadata/edit", method = RequestMethod.GET)
	public String getProjectMetadataEditPage(final Model model, final Principal principal, @PathVariable long projectId) {
		Project project = projectService.read(projectId);
		User user = userService.getUserByUsername(principal.getName());
		if (projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER)) {
			if (!model.containsAttribute("errors")) {
				model.addAttribute("errors", new HashMap<>());
			}
			getProjectTemplateDetails(model, principal, project);
			model.addAttribute("project", project);
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
			@RequestParam(required = false, defaultValue = "") String remoteURL) {

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
		getProjectTemplateDetails(model, principal, project);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_SAMPLES);
		return PROJECT_SAMPLES_PAGE;
	}

	@RequestMapping(value = "/ajax/{projectId}/samples", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxProjectSamplesMap(
			@PathVariable Long projectId,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(ProjectSamplesDataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = ProjectSamplesDataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = ProjectSamplesDataTable.SORT_DEFAULT_COLUMN) Integer sortColumn,
			@RequestParam(value = ProjectSamplesDataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = ProjectSamplesDataTable.SORT_DEFAULT_DIRECTION) String direction,
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

	@RequestMapping(value = "/ajax/{projectId}/members", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Collection<Join<Project, User>>> getAjaxProjectMemberMap(
			@PathVariable Long projectId) {
		Map<String, Collection<Join<Project, User>>> data = new HashMap<>();
		try {
			Project project = projectService.read(projectId);
			Collection<Join<Project, User>> users = userService.getUsersForProject(project);
			data.put(ProjectsDataTable.RESPONSE_PARAM_DATA, users);
		} catch (Exception e) {
			logger.error("Trying to access a project that does not exist.");
		}
		return data;
	}

	/**
	 * Handles AJAX request for getting a list of projects available to the
	 * logged in user. Produces JSON.
	 *
	 * @param principal
	 *            {@link Principal} The currently authenticated users
	 * @param start
	 *            The start position in the list to page.
	 * @param length
	 *            The size of the page to display.
	 * @param draw
	 *            Id for the table to draw, this must be returned.
	 * @param sortColumn
	 *            The id for the column to sort by.
	 * @param direction
	 *            The direction of the sort.
	 * @param searchValue
	 *            Any search terms.
	 * @return JSON value of the page data.
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxProjectListForUser(
			final Principal principal,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = ProjectsDataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = ProjectsDataTable.SORT_DEFAULT_COLUMN) Integer sortColumn,
			@RequestParam(value = ProjectsDataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = ProjectsDataTable.SORT_DEFAULT_DIRECTION) String direction,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {
		int pageNumber = ProjectsDataTable.getPageNumber(start, length);
		Sort.Direction sortDirection = ProjectsDataTable.getSortDirection(direction);
		String sortString = ProjectsDataTable.getSortStringFromColumnID(sortColumn);

		Page<ProjectUserJoin> page = projectService.searchProjectsByNameForUser(
				userService.getUserByUsername(principal.getName()), searchValue, pageNumber, length, sortDirection,
				sortString);
		List<ProjectUserJoin> projectList = page.getContent();

		return getProjectsDataMap(projectList, draw, page.getTotalElements(), sortColumn, sortDirection);
	}

	/**
	 * Handles AJAX request for getting a list of projects available to the
	 * admin user. Produces JSON.
	 *
	 * @param principal
	 *            {@link Principal} The currently authenticated users
	 * @param start
	 *            The start position in the list to page.
	 * @param length
	 *            The size of the page to display.
	 * @param draw
	 *            Id for the table to draw, this must be returned.
	 * @param sortColumn
	 *            The id for the column to sort by.
	 * @param direction
	 *            The direction of the sort.
	 * @param searchValue
	 *            Any search terms.
	 * @return JSON value of the page data.
	 */
	@RequestMapping(value = "/ajax/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public @ResponseBody Map<String, Object> getAjaxProjectListForAdmin(
			final Principal principal,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_START) Integer start,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_LENGTH) Integer length,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_DRAW) Integer draw,
			@RequestParam(value = ProjectsDataTable.REQUEST_PARAM_SORT_COLUMN, defaultValue = ProjectsDataTable.SORT_DEFAULT_COLUMN) Integer sortColumn,
			@RequestParam(value = ProjectsDataTable.REQUEST_PARAM_SORT_DIRECTION, defaultValue = ProjectsDataTable.SORT_DEFAULT_DIRECTION) String direction,
			@RequestParam(ProjectsDataTable.REQUEST_PARAM_SEARCH_VALUE) String searchValue) {

		int pageNumber = ProjectsDataTable.getPageNumber(start, length);
		Sort.Direction sortDirection = ProjectsDataTable.getSortDirection(direction);
		String sortString = ProjectsDataTable.getSortStringFromColumnID(sortColumn);

		// Get the page information
		Page<Project> page = projectService.searchProjectsByName(searchValue, pageNumber, length, sortDirection,
				sortString);
		List<ProjectUserJoin> projectList = getAdminProjectUserJoin(page,
				userService.getUserByUsername(principal.getName()));

		return getProjectsDataMap(projectList, draw, page.getTotalElements(), sortColumn, sortDirection);
	}

	@RequestMapping(value = "/ajax/{projectId}/samples/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Map<String, Object> postUpdateProjectSamples(@RequestParam(required = true) Long sampleId,
                                          @RequestParam(required = false) String name) {
        Map<String, Object> updateMap = new HashMap<>();
        if (!Strings.isNullOrEmpty(name)) {
            updateMap.put("sampleName", name);
        }

        Map<String, Object> resultMap = new HashMap<>();
        try {
            sampleService.update(sampleId, updateMap);
            resultMap.put("success", "Updated name");
        } catch (ConstraintViolationException e) {
            resultMap.put("error", getErrorsFromViolationException(e));
        }
        return resultMap;
    }

    @RequestMapping(value = "/ajax/{projectId}/samples/getids", produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Map<String, List<String>> getAllProjectIds(@PathVariable Long projectId) {
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
	 * Generates a map of project information for the {@link ProjectsDataTable}
	 *
	 * @param projectList
	 *            a List of {@link ProjectUserJoin} for the current user.
	 * @param draw
	 *            property sent from {@link ProjectsDataTable} as the table to
	 *            render information to.
	 * @param totalElements
	 *            Total number of elements that could go into the table.
	 * @param sortColumn
	 *            Column to sort by.
	 * @param sortDirection
	 *            Direction to sort the column
	 * @return Map containing the information to put into the
	 *         {@link ProjectsDataTable}
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

			l.put("checkbox", p.getId().toString());			l.put("id", p.getId().toString());
			l.put("name", p.getName());
			l.put("organism", p.getOrganism());
			l.put("role", role);
			l.put("samples", String.valueOf(sampleService.getSamplesForProject(p).size()));
			l.put("members", String.valueOf(userService.getUsersForProject(p).size()));
			l.put("dateCreated", Formats.DATE.format(p.getTimestamp()));
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
     * @param projectId
     *            Id of the project to remove the samples from
     *
     * @param sampleIds
     *            An array of samples to remove from a project
     * @return Map containing either success or errors.
     */
    @RequestMapping(value = "/ajax/{projectId}/samples/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> deleteProjectSamples(@PathVariable Long projectId, @RequestParam List<Long> sampleIds) {
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

    @RequestMapping(value = "/ajax/getNamesFromIds", produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<Map<String, String>> ajaxGetSampleNamesFromIds(@RequestParam List<Long> sampleIds) {
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
	 * Based on a page of projects for an user, returns a list that also
	 * includes information as to whether the user is a member of the project.
	 * 
	 * @param page
	 *            A {@link Page} of {@link Project}
	 * @param user
	 *            The currently logged in user
	 * @return A list of {@link ProjectUserJoin}
	 */
	private List<ProjectUserJoin> getAdminProjectUserJoin(Page<Project> page, User user) {
		List<Project> pageList = page.getContent();
		List<Join<Project, User>> allUsersProjects = projectService.getProjectsForUser(user);

		Map<Long, ProjectRole> roleMap = new HashMap<>();
		for (Join<Project, User> join : allUsersProjects) {
			Project p = join.getSubject();
			roleMap.put(p.getId(), ((ProjectUserJoin) join).getProjectRole());
		}

		List<ProjectUserJoin> projects = new ArrayList<>();
		for (Project project : pageList) {
			if (roleMap.containsKey(project.getId())) {
				projects.add(new ProjectUserJoin(project, user, roleMap.get(project.getId())));
			} else {
				projects.add(new ProjectUserJoin(project, user, null));
			}
		}
		return projects;
	}

	/**
	 * Adds to the current view model default template information:
	 * <ul>
	 * <li>Sidebar Information</li>
	 * <li>If the current user is an admin</li>
	 * </ul>
	 * 
	 * @param model
	 *            {@link Model} for the current view.
	 * @param principal
	 *            {@link Principal} currently logged in user.
	 * @param project
	 *            {@link} current project viewed.
	 */
	public void getProjectTemplateDetails(Model model, Principal principal, Project project) {
		User loggedInUser = userService.getUserByUsername(principal.getName());

		// Determine if the user is an owner or admin.
		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);
		model.addAttribute("isAdmin", isAdmin);

		// Find out who the owner of the project is.
		Collection<Join<Project, User>> ownerJoinList = userService.getUsersForProjectByRole(project,
				ProjectRole.PROJECT_OWNER);
		boolean isOwner = false;
		for (Join<Project, User> owner : ownerJoinList) {
			if (loggedInUser.equals(owner.getObject())) {
				isOwner = true;
			}
		}

		model.addAttribute("isOwner", isOwner);

		int sampleSize = sampleService.getSamplesForProject(project).size();
		model.addAttribute("samples", sampleSize);

		int userSize = userService.getUsersForProject(project).size();
		model.addAttribute("users", userSize);

		// TODO: (Josh - 14-06-23) Get list of recent activities on project.

		// Add any associated projects
		User currentUser = userService.getUserByUsername(principal.getName());
		List<Map<String, String>> associatedProjects = getAssociatedProjects(project, currentUser, isAdmin);
		model.addAttribute("associatedProjects", associatedProjects);
	}

	/**
	 * Find all projects that have been associated with a project.
	 *
	 * @param currentProject
	 *            The project to find the associated projects of.
	 * @param currentUser
	 *            The currently logged in user.
	 * @return List of Maps containing information about the associated
	 *         projects.
	 */
	private List<Map<String, String>> getAssociatedProjects(Project currentProject, User currentUser, boolean isAdmin) {
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(currentProject);

		// Need to know if the user has rights to view the project
		List<Join<Project, User>> userProjectJoin = projectService.getProjectsForUser(currentUser);

		List<Map<String, String>> projects = new ArrayList<>();
		// Create a quick lookup list
		Map<Long, Boolean> usersProjects = new HashMap<>(userProjectJoin.size());
		for (Join<Project, User> join : userProjectJoin) {
			usersProjects.put(join.getSubject().getId(), true);
		}

		for (RelatedProjectJoin rpj : relatedProjectJoins) {
			Project project = rpj.getObject();

			Map<String, String> map = new HashMap<>();
			map.put("name", project.getLabel());
			map.put("id", project.getId().toString());
			map.put("auth", isAdmin || usersProjects.containsKey(project.getId()) ? "authorized" : "");

			// TODO: (Josh - 2014-07-07) Will need to add remote location
			// information here.
			projects.add(map);
		}
		return projects;
	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings
	 * for displaing in the UI.
	 *
	 * @param e
	 *            {@link ConstraintViolationException} for the form submitted.
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

	@ExceptionHandler({ProjectWithoutOwnerException.class, ProjectSelfEditException.class})
	@ResponseBody
	public ResponseEntity<String> roleChangeErrorHandler(Exception ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.FORBIDDEN);
	}
}
