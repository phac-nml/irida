package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller @RequestMapping("/projects/{projectId}/linelist") public class ProjectLineListController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectLineListController.class);

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final ProjectControllerUtils projectControllerUtils;
	private MessageSource messageSource;

	@Autowired
	public ProjectLineListController(ProjectService projectService, SampleService sampleService,
			ProjectControllerUtils utils, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.projectControllerUtils = utils;
		this.messageSource = messageSource;
	}

	@RequestMapping("")
	public String getLineListPage(@PathVariable Long projectId, Model model, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("activeNav", "linelist");
		return "projects/project_linelist";
	}

	@RequestMapping("/samples")
	@ResponseBody
	public Map<String, Object> getListListData(@PathVariable Long projectId,
			@DatatablesParams DatatablesCriterias criterias) {
		List<Project> projectList = ImmutableList.of(projectService.read(projectId));
		ColumnDef sortedColumn = criterias.getSortedColumnDefs().get(0);

		// Get the currently displayed page.
		final Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(
				projectList,
				null,
				"",
				criterias.getSearch(),
				null,
				null,
				DatatablesUtils.getCurrentPage(criterias),
				criterias.getLength(),
				DatatablesUtils.generateSortDirection(sortedColumn),
				sortedColumn.getName()
				);

		List<Map<String, Object>> pagedMetadata = new ArrayList<>();
		for (ProjectSampleJoin join : page) {
			SampleMetadata sampleMetadata = sampleService.getMetadataForSample(join.getObject());
			// TODO: This is where the project template should be applied.  For now return everything.
			pagedMetadata.add(sampleMetadata.getMetadata());

		}

		return ImmutableMap.of(
				"metadata", pagedMetadata
		);
	}
}
