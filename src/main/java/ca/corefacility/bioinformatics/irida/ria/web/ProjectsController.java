package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.DataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Iterables;

/**
 * Controller for all project related views
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController {
	public static final String PROJECTS_PAGE = "projects";
	public static final String SPECIFIC_PROJECT_PAGE = "project_details";
	public static final String ERROR_PAGE = "error";
	public static final String SORT_BY_ID = "id";
	public static final String SORT_BY_NAME = "name";
	public static final String SORT_BY_CREATED_DATE = "createdDate";
	public static final String SORT_BY_MODIFIED_DATE = "modifiedDate";
	public static final String SORT_ASCENDING = "asc";
	public static final String COLUMN_PROJECT_ID = "0";
	public static final String COLUMN_NAME = "1";
	public static final String COLUMN_DATE_CREATED = "5";
	public static final String COLUMN_DATE_MODIFIED = "6";
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);
	private ProjectService projectService;
	private SampleService sampleService;
	private UserService userService;

	@Autowired
	public ProjectsController(ProjectService projectService, SampleService sampleService, UserService userService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
	}

	/**
	 * Request for the page to display a list of all projects available to the
	 * currently logged in user.
	 * 
	 * @return The name of the page.
	 */
	@RequestMapping
	public String getProjectsPage() {
		return PROJECTS_PAGE;
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
	public String getProjectSpecificPage(@PathVariable Long projectId, final Model model) {
		logger.debug("Getting project information for [Project " + projectId + "]");
		String page;
		try {
			Project project = projectService.read(projectId);
			model.addAttribute("project", project);

			Join<Project, User> ownerJoin = Iterables.getFirst(
					userService.getUsersForProjectByRole(project, ProjectRole.PROJECT_OWNER), null);
			User owner = null;
			if (ownerJoin != null) {
				owner = ownerJoin.getObject();
			}
			model.addAttribute("owner", owner);

			int sampleSize = sampleService.getSamplesForProject(project).size();
			model.addAttribute("samples", sampleSize);

			int userSize = userService.getUsersForProject(project).size();
			model.addAttribute("users", userSize);

			// TODO: (Josh - 14-06-23) Get list of recent activities on project.
			// TODO: (Josh - 14-06-23) Get associated projects.
			page = SPECIFIC_PROJECT_PAGE;
		} catch (EntityNotFoundException e) {
			// TODO: (Josh - 2014-06-24) Format error page if project is not
			// found. These should probably be redirects.
			page = ERROR_PAGE;
		} catch (AccessDeniedException e) {
			// TODO: (Josh - 2014-06-24) Format error page if user does not have
			// access. These should probably be redirects.
			page = ERROR_PAGE;
		}
		return page;
	}

	/**
	 * Handles AJAX request for getting a list of projects available to the
	 * logged in user. Produces JSON.
	 * 
	 * @param principal
	 *            The currently logged in user.
	 * @param request
	 *            Contains the parameters for the datatable.
	 * @return JSON value of the projects.
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Map<String, Object> getAjaxProjectList(final Principal principal, WebRequest request) {
		User user = userService.getUserByUsername(principal.getName());

		int start = Integer.parseInt(request.getParameter(DataTable.REQUEST_PARAM_START));
		int length = Integer.parseInt(request.getParameter(DataTable.REQUEST_PARAM_LENGTH));
		int draw = Integer.parseInt(request.getParameter(DataTable.REQUEST_PARAM_DRAW));
		String sortColumn = request.getParameter(DataTable.REQUEST_PARAM_SORT_COLUMN);
		String sortString;

		// If there is a sort by column set the correct name to sort by.
		// The default should be by date modified since that is what the
		// user would have most recently used.
		switch (sortColumn) {
		case COLUMN_PROJECT_ID:
			sortString = SORT_BY_ID;
			break;
		case COLUMN_NAME:
			sortString = SORT_BY_NAME;
			break;
		case COLUMN_DATE_CREATED:
			sortString = SORT_BY_CREATED_DATE;
			break;
		case COLUMN_DATE_MODIFIED:
			sortString = SORT_BY_MODIFIED_DATE;
			break;
		default:
			sortString = SORT_BY_MODIFIED_DATE;
		}
		Sort.Direction sortDirection = request.getParameter(DataTable.REQUEST_PARAM_SORT_DIRECTION).equals(
				SORT_ASCENDING) ? Sort.Direction.ASC : Sort.Direction.DESC;
		String searchValue = request.getParameter(DataTable.REQUEST_PARAM_SEARCH_VALUE);

		int pageNum = (int) Math.floor(start / length);
		Page<ProjectUserJoin> page = projectService.searchProjectsByNameForUser(user, searchValue, pageNum, length,
				sortDirection, sortString);
		List<ProjectUserJoin> projectList = page.getContent();

		Map<String, Object> map = new HashMap<>();
		map.put(DataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(DataTable.RESPONSE_PARAM_RECORDS_TOTAL, page.getTotalElements());
		map.put(DataTable.RESPONSE_PARAM_RECORDS_FILTERED, page.getTotalElements());

		// Create the format required by DataTable
		List<List<String>> projectsData = new ArrayList<>();
		for (ProjectUserJoin projectUserJoin : projectList) {
			Project p = projectUserJoin.getSubject();
			ProjectRole role = projectUserJoin.getProjectRole();
			List<String> l = new ArrayList<>();
			l.add(p.getId().toString());
			l.add(p.getName());
			l.add(role.toString());
			l.add(String.valueOf(sampleService.getSamplesForProject(p).size()));
			l.add(String.valueOf(userService.getUsersForProject(p).size()));
			l.add(Formats.DATE.format(p.getTimestamp()));
			l.add(Formats.DATE.format(p.getModifiedDate()));
			projectsData.add(l);
		}
		map.put(DataTable.RESPONSE_PARAM_DATA, projectsData);
		return map;
	}

}
