package ca.corefacility.bioinformatics.irida.ria.web.projects;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.CacheObject;
import ca.corefacility.bioinformatics.irida.ria.utilities.RemoteObjectCache;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsDataTable;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

@Controller
@RequestMapping("/projects")
@Scope("session")
public class AssociatedProjectsController {
	private static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_ASSOCIATED_PROJECTS = "associated";
	public static final String ASSOCIATED_PROJECTS_PAGE = ProjectsController.PROJECTS_DIR + "associated_projects";
	public static final String EDIT_ASSOCIATED_PROJECTS_PAGE = ProjectsController.PROJECTS_DIR
			+ "associated_projects_edit";

	private final RemoteRelatedProjectService remoteRelatedProjectService;
	private final ProjectService projectService;
	private final ProjectControllerUtils projectControllerUtils;
	private final RemoteAPIService apiService;
	private final UserService userService;
	private final ProjectRemoteService projectRemoteService;

	private final SampleService sampleService;

	private final Formatter<Date> dateFormatter;

	private RemoteObjectCache<Project> remoteProjectCache;

	@Autowired
	public AssociatedProjectsController(RemoteRelatedProjectService remoteRelatedProjectService,
			ProjectService projectService, ProjectControllerUtils projectControllerUtils, UserService userService,
			RemoteAPIService apiService, ProjectRemoteService projectRemoteService, SampleService sampleService,
			RemoteObjectCache<Project> remoteProjectCache) {

		this.remoteRelatedProjectService = remoteRelatedProjectService;
		this.projectService = projectService;
		this.projectControllerUtils = projectControllerUtils;
		this.userService = userService;
		this.apiService = apiService;
		this.projectRemoteService = projectRemoteService;
		this.remoteProjectCache = remoteProjectCache;
		this.sampleService = sampleService;
		dateFormatter = new DateFormatter();
	}

	/**
	 * Get the associated projects for the given project
	 * 
	 * @param projectId
	 *            The ID of the project to get associated projects
	 * @param model
	 *            A model for the view
	 * @param principal
	 *            a reference to the logged in user.
	 * @return The view name of the assocated projects view
	 */
	@RequestMapping(value = "/{projectId}/associated", method = RequestMethod.GET)
	public String getAssociatedProjectsPage(@PathVariable Long projectId, Model model, Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		User loggedInUser = userService.getUserByUsername(principal.getName());

		// Determine if the user is an owner or admin.
		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);
		model.addAttribute("isAdmin", isAdmin);

		// Add any associated projects
		User currentUser = userService.getUserByUsername(principal.getName());
		List<Map<String, String>> associatedProjects = getAssociatedProjectsForProject(project, currentUser, isAdmin);
		model.addAttribute("associatedProjects", associatedProjects);

		Map<RemoteAPI, List<RemoteRelatedProject>> remoteRelatedProjectsByApi = getRemoteRelatedProjectsByApi(project);
		model.addAttribute("remoteProjectsByApi", remoteRelatedProjectsByApi);

		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ASSOCIATED_PROJECTS);

		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return ASSOCIATED_PROJECTS_PAGE;
	}

	/**
	 * Add an associated project to a project
	 * 
	 * @param projectId
	 *            The subject project id
	 * @param associatedProjectId
	 *            The associated project id
	 * @return "success" if the request was successful
	 */
	@RequestMapping(value = "/{projectId}/associated", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> addAssociatedProject(@PathVariable Long projectId, @RequestParam Long associatedProjectId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedProjectId);

		projectService.addRelatedProject(project, associatedProject);

		return ImmutableMap.of("result", "success");
	}

	/**
	 * Delete an associated project to a project
	 * 
	 * @param projectId
	 *            The subject project id
	 * @param associatedProjectId
	 *            The associated project id
	 * @return "success" if the request was successful
	 */
	@RequestMapping(value = "/{projectId}/associated", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> removeAssociatedProject(@PathVariable Long projectId,
			@RequestParam Long associatedProjectId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedProjectId);

		projectService.removeRelatedProject(project, associatedProject);

		return ImmutableMap.of("result", "success");
	}

	/**
	 * Get the edit associated projects page
	 * 
	 * @param projectId
	 *            The ID of the current project
	 * @param model
	 *            Model object to be passed to the view
	 * @param principal
	 *            The logged in user
	 * @return The name of the edit associated projects view
	 */
	@RequestMapping("/{projectId}/associated/edit")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId, 'isProjectOwner')")
	public String editAssociatedProjectsForProject(@PathVariable Long projectId, Model model, Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		Iterable<RemoteAPI> remoteApis = apiService.findAll();
		model.addAttribute("apis", remoteApis);

		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_ASSOCIATED_PROJECTS);

		return EDIT_ASSOCIATED_PROJECTS_PAGE;
	}

	/**
	 * Get {@link Project}s that could be associated with this project
	 * 
	 * @param projectId
	 *            The current project ID
	 * @param principal
	 *            The logged in user
	 * @param page
	 *            The page to request
	 * @param count
	 *            The number of elements in the page
	 * @param sortedBy
	 *            The property to sort by
	 * @param sortDir
	 *            The direction to sort in
	 * @param projectName
	 *            The project name to search for
	 * @return A {@code Map<String,Object>} of elements for a datatable
	 */
	@RequestMapping("/{projectId}/associated/ajax/available")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId, 'isProjectOwner')")
	@ResponseBody
	public Map<String, Object> getPotentialAssociatedProjects(@PathVariable Long projectId, final Principal principal,
			@RequestParam Integer page, @RequestParam Integer count, @RequestParam String sortedBy,
			@RequestParam String sortDir,
			@RequestParam(value = "name", required = false, defaultValue = "") String projectName) {
		Project project = projectService.read(projectId);

		Sort.Direction sortDirection = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);

		// check if the logged in user is an Admin
		User loggedInUser = userService.getUserByUsername(principal.getName());
		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);

		List<Project> projects;
		long totalElements;
		int totalPages;

		// if they're an admin or project manager we have to call different
		// methods
		if (isAdmin) {
			// get projects with the given name except the current project
			Specification<Project> specification = where(ProjectSpecification.searchProjectName(projectName)).and(
					ProjectSpecification.excludeProject(project));
			Page<Project> search = projectService.search(specification, page, count, sortDirection, sortedBy);

			totalElements = search.getTotalElements();
			totalPages = search.getTotalPages();
			projects = search.getContent();
		} else {
			// need to append project. to the sort string for ProjectUserJoins
			String sortString = "project." + sortedBy;

			// get projects with the given name except the current project
			Specification<ProjectUserJoin> specification = where(
					ProjectUserJoinSpecification.searchProjectNameWithUser(projectName, loggedInUser)).and(
					ProjectUserJoinSpecification.excludeProject(project));
			Page<ProjectUserJoin> searchProjectUsers = projectService.searchProjectUsers(specification, page, count,
					sortDirection, sortString);

			totalElements = searchProjectUsers.getTotalElements();
			totalPages = searchProjectUsers.getTotalPages();

			// transform the ProjectUserJoin page to a Project list
			projects = new ArrayList<>();
			searchProjectUsers.forEach((puj) -> projects.add(puj.getSubject()));
		}

		Map<String, Object> map = getProjectsDataMap(projects, relatedProjectJoins);
		map.put("totalAssociated", totalElements);
		map.put("totalPages", totalPages);

		return map;
	}

	/**
	 * Get the remote projects that could potentially be associated with this
	 * project
	 * 
	 * @param projectId
	 *            The current {@link Project} ID
	 * @param apiId
	 *            The ID of the {@link RemoteAPI} to get projects for
	 * @return A List of Maps of the project properties
	 */
	@RequestMapping("/{projectId}/associated/remote/{apiId}/available")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId, 'isProjectOwner')")
	@ResponseBody
	public List<Map<String, String>> getPotentialRemoteAssociatedProjectsForApi(@PathVariable Long projectId,
			@PathVariable Long apiId) {

		Project project = projectService.read(projectId);
		RemoteAPI api = apiService.read(apiId);
		List<Project> listProjectsForAPI = projectRemoteService.listProjectsForAPI(api);
		List<RemoteRelatedProject> remoteProjectsForProject = remoteRelatedProjectService
				.getRemoteProjectsForProject(project);

		return getRemoteAssociatedProjectsMap(listProjectsForAPI, remoteProjectsForProject, api);
	}

	/**
	 * Add a {@link RemoteRelatedProject} to the current {@link Project}
	 * 
	 * @param projectId
	 *            The ID of the owning project
	 * @param associatedProjectId
	 *            The Cache ID of the {@link RemoteProject}
	 * @param apiId
	 *            The ID of the api this project resides on
	 * @return a Map representation of the status of adding the associated
	 *         project.
	 */
	@RequestMapping(value = "/{projectId}/associated/remote", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> addRemoteAssociatedProject(@PathVariable Long projectId,
			@RequestParam Integer associatedProjectId, @RequestParam Long apiId) {
		Project project = projectService.read(projectId);
		RemoteAPI remoteAPI = apiService.read(apiId);
		CacheObject<Project> cacheObject = remoteProjectCache.readResource(associatedProjectId);
		Project readResource = cacheObject.getResource();

		Link selfLink = readResource.getLink(Link.REL_SELF);
		RemoteRelatedProject remoteRelatedProject = new RemoteRelatedProject(project, remoteAPI,
				selfLink.getHref());
		remoteRelatedProjectService.create(remoteRelatedProject);

		return ImmutableMap.of("result", "success");
	}

	/**
	 * Delete a remote associated project from a project
	 * 
	 * @param projectId
	 *            The ID of the project to remove the association from
	 * @param associatedProjectId
	 *            The cache identifier for the remote element
	 * @return a Map representation of the status of removing the associated
	 *         project.
	 */
	@RequestMapping(value = "/{projectId}/associated/remote/{associatedProjectId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> removeRemoteAssociatedProject(@PathVariable Long projectId,
			@PathVariable Integer associatedProjectId) {
		Project project = projectService.read(projectId);
		CacheObject<Project> cacheObject = remoteProjectCache.readResource(associatedProjectId);
		Project readResource = cacheObject.getResource();

		Link selfLink = readResource.getLink(Link.REL_SELF);
		RemoteRelatedProject remoteRelatedProjectForProjectAndURI = remoteRelatedProjectService
				.getRemoteRelatedProjectForProjectAndURI(project, selfLink.getHref());
		remoteRelatedProjectService.delete(remoteRelatedProjectForProjectAndURI.getId());

		return ImmutableMap.of("result", "success");
	}

	/**
	 * Get the associated {@link Sample}s for a given {@link Project}
	 * 
	 * @param projectId
	 *            the ID of the local project
	 * @return {@code Map<String, Object>} containing samples: list of samples
	 */
	@RequestMapping(value = "/{projectId}/associated/samples")
	@ResponseBody
	public Map<String, Object> getAssociatedSamplesForProject(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);

		List<RelatedProjectJoin> authorizedRelatedProjectsForUser = projectService.getRelatedProjects(project);

		List<Map<String, Object>> sampleList = new ArrayList<>();
		// for each project
		for (RelatedProjectJoin rp : authorizedRelatedProjectsForUser) {
			Project object = rp.getObject();
			// get the samples in the project
			List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(object);

			// get the sample map marking the sample type as ASSOCIATED
			List<Map<String, Object>> collect = samplesForProject
					.stream()
					.map((j) -> ProjectSamplesController.getSampleMap(j.getObject(), j.getSubject(),
							ProjectSamplesController.SampleType.ASSOCIATED, j.getObject().getId()))
					.collect(Collectors.toList());

			sampleList.addAll(collect);
		}

		return ImmutableMap.of("samples", sampleList);
	}

	/**
	 * Get a list of the {@link RemoteProject} parameters
	 * 
	 * @param projects
	 *            A list of the {@link RemoteProject}s to display
	 * @param associatedProjects
	 *            The {@link RemoteRelatedProject}s associated with the current
	 *            project
	 * @return
	 */
	private List<Map<String, String>> getRemoteAssociatedProjectsMap(List<Project> projects,
			List<RemoteRelatedProject> associatedProjects, RemoteAPI api) {
		List<Map<String, String>> list = new ArrayList<>();

		Map<String, Boolean> remoteUrls = new HashMap<>();
		for (RemoteRelatedProject remote : associatedProjects) {
			String remoteProjectURI = remote.getRemoteProjectURI();
			remoteUrls.put(remoteProjectURI, true);
		}

		for (Project project : projects) {
			Map<String, String> pmap = new HashMap<>();
			Integer remoteId = remoteProjectCache.addResource(project, api);

			pmap.put("id", project.getId().toString());
			pmap.put("remoteId", remoteId.toString());
			pmap.put("name", project.getName());
			pmap.put("organism", project.getOrganism());
			pmap.put("createdDate", dateFormatter.print(project.getCreatedDate(), LocaleContextHolder.getLocale()));
			
			Link selfLink = project.getLink(Link.REL_SELF);
			if (remoteUrls.containsKey(selfLink.getHref())) {
				pmap.put("associated", "associated");
			}

			list.add(pmap);
		}

		return list;
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
	private List<Map<String, String>> getAssociatedProjectsForProject(Project currentProject, User currentUser,
			boolean isAdmin) {
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(currentProject);

		List<Map<String, String>> projects = new ArrayList<>();

		for (RelatedProjectJoin rpj : relatedProjectJoins) {
			Project project = rpj.getObject();

			Map<String, String> map = new HashMap<>();
			map.put("name", project.getLabel());
			map.put("id", project.getId().toString());
			map.put("auth", "authorized");

			projects.add(map);
		}
		return projects;
	}

	/**
	 * Get the {@link RemoteRelatedProject} sorted by {@link RemoteAPI}
	 * 
	 * @param currentProject
	 *            The current project to get related projects for
	 * @return A Map<RemoteAPI,List<RemoteRelatedProject>> of the relationships
	 */
	private Map<RemoteAPI, List<RemoteRelatedProject>> getRemoteRelatedProjectsByApi(Project currentProject) {
		List<RemoteRelatedProject> remoteProjectsForProject = remoteRelatedProjectService
				.getRemoteProjectsForProject(currentProject);
		Map<RemoteAPI, List<RemoteRelatedProject>> projectsByApi = new HashMap<>();
		for (RemoteRelatedProject p : remoteProjectsForProject) {
			RemoteAPI api = p.getRemoteAPI();
			if (!projectsByApi.containsKey(api)) {
				List<RemoteRelatedProject> list = new ArrayList<>();
				projectsByApi.put(api, list);
			}

			projectsByApi.get(api).add(p);
		}

		return projectsByApi;
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
	private Map<String, Object> getProjectsDataMap(Iterable<Project> projectList,
			List<RelatedProjectJoin> relatedProjectJoins) {
		Map<String, Object> map = new HashMap<>();

		Map<Project, Boolean> related = new HashMap<>();

		relatedProjectJoins.forEach((p) -> related.put(p.getObject(), true));

		// Create the format required by DataTable
		List<Map<String, String>> projectsData = new ArrayList<>();
		for (Project project : projectList) {
			Map<String, String> projectMap = new HashMap<>();
			projectMap.put("id", project.getId().toString());
			projectMap.put("name", project.getName());
			projectMap.put("organism", project.getOrganism());
			projectMap.put("createdDate",
					dateFormatter.print(project.getCreatedDate(), LocaleContextHolder.getLocale()));

			if (related.containsKey(project)) {
				projectMap.put("associated", "associated");
			}

			projectsData.add(projectMap);
		}
		map.put("associated", projectsData);
		return map;
	}

	/**
	 * Handle entity exists exceptions for creating {@link RelatedProjectJoin}s
	 * 
	 * @param ex the exception to handle.
	 * @return a {@link ResponseEntity} to render the exception to the client.
	 */
	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<String> handleEntityExistsException(EntityExistsException ex) {
		return new ResponseEntity<>("This relationship already exists.", HttpStatus.CONFLICT);
	}

}
