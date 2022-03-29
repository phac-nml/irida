package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.SampleIdsRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAssociatedProjectsService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

@RestController
@RequestMapping("/ajax/project-samples/{projectId}")
public class AjaxSamplesController {
	private ProjectService projectService;
	private SampleService sampleService;
	private UIAssociatedProjectsService uiAssociatedProjectsService;
	private UISampleService uiSampleService;

	@Autowired
	public AjaxSamplesController(ProjectService projectService, SampleService sampleService,
			UIAssociatedProjectsService uiAssociatedProjectsService, UISampleService uiSampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.uiAssociatedProjectsService = uiAssociatedProjectsService;
		this.uiSampleService = uiSampleService;
	}

	@PostMapping("")
	public ResponseEntity<AntTableResponse> getProjectSamples(@PathVariable Long projectId,
			@RequestBody ProjectSamplesTableRequest request) {
		List<Long> projectIds = new ArrayList<>();
		projectIds.add(projectId);
		if (request.getAssociated() != null) {
			projectIds.addAll(request.getAssociated());
		}
		List<Project> projects = (List<Project>) projectService.readMultiple(projectIds);

		Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(projects, ImmutableList.of(), null,
				null, null, null, null, request.getCurrent(), request.getPageSize(), request.getSort());
		List<ProjectSampleTableItem> content = page.getContent()
				.stream()
				.map(ProjectSampleTableItem::new).collect(Collectors.toList());

		AntTableResponse body = new AntTableResponse(content, page.getTotalElements());
		return ResponseEntity.ok(body);
	}

	@GetMapping("/associated")
	public List<AssociatedProject> getAssociatedProjectsForProject(@PathVariable Long projectId) {
		return uiAssociatedProjectsService.getAssociatedProjectsForProject(projectId);
	}

	@PostMapping("/sampleIds")
	public List<Long> getProjectSamplesIds(@PathVariable Long projectId, @RequestBody SampleIdsRequest request) {
		List<Long> ids = request.getAssociated();
		ids.add(projectId);
		// I HAVE NO IDEA WHAT TO DO NOW!!!
		return uiSampleService.getSampleIdsForProject(ids);
	}
}
