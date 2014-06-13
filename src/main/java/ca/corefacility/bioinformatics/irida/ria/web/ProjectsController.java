package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.ria.utilities.DataTable;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for all project related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects")
public class ProjectsController {

	private ProjectService projectService;
	private SampleService sampleService;
	private UserService userService;

	@Autowired
	public ProjectsController(ProjectService projectService, SampleService sampleService, UserService userService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.userService = userService;
	}

	@RequestMapping(value = "")
	public String getProjectsPage() {
		return "projects";
	}

	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Map<String, Object> getAjaxProjectList(final Principal principal, WebRequest request) {

		int start = Integer.parseInt(request.getParameter(DataTable.REQUEST_PARAM_START));
		int length = Integer.parseInt(request.getParameter(DataTable.REQUEST_PARAM_LENGTH));
		int draw = Integer.parseInt(request.getParameter(DataTable.REQUEST_PARAM_DRAW));
		String sortColumn = request.getParameter(DataTable.REQUEST_PARAM_SORT_COLUMN);
		String sortString;
		switch (sortColumn) {
			case "0":
				sortString = "name";
				break;
			case "3":
				sortString = "createdDate";
				break;
			case "4":
				sortString = "modifiedDate";
				break;
			default:
				sortString = "name";
		}
		Sort.Direction sortDirection = request.getParameter(DataTable.REQUEST_PARAM_SORT_DIRECTION).equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
		// TODO: implement search
		String searchValue = request.getParameter(DataTable.REQUEST_PARAM_SEARCH_VALUE);

		int pageNum = (int) Math.floor(start / length);
		Page<Project> page = projectService.searchProjects(searchValue, pageNum, length, sortDirection, sortString);
		List<Project> projectList = page.getContent();

		Map<String, Object> map = new HashMap<>();
		map.put(DataTable.RESPONSE_PARAM_DRAW, draw);
		map.put(DataTable.RESPONSE_PARAM_RECORDS_TOTAL, page.getTotalElements());
		map.put(DataTable.RESPONSE_PARAM_RECORDS_FILTERED, page.getTotalElements());

		// Create the format required by DataTable
		List<Map<String, String>> projectsData = new ArrayList<>();
		for (Project p : projectList) {
			Map<String, String> pList = new HashMap<>();
			pList.put("id", String.valueOf(p.getId()));
			pList.put("name", p.getName());
			pList.put("samples", String.valueOf(sampleService.getSamplesForProject(p).size()));
			pList.put("collaborators", String.valueOf(userService.getUsersForProject(p).size()));
			pList.put("createdDate", String.valueOf(Formats.DATE.format(p.getTimestamp())));
			pList.put("modifiedDate", String.valueOf(Formats.DATE.format(p.getModifiedDate())));

			projectsData.add(pList);
		}
		map.put(DataTable.RESPONSE_PARAM_DATA, projectsData);
		return map;
	}
}
