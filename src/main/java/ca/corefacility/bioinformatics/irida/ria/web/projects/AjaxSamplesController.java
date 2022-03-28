package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

@RestController
@RequestMapping("/ajax/project-samples")
public class AjaxSamplesController {
	private ProjectService projectService;
	private SampleService sampleService;

	@Autowired
	public AjaxSamplesController(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	@PostMapping("")
	public ResponseEntity<AntTableResponse> getProjectSamples(@RequestParam List<Long> projectIds,
			@RequestBody AntTableRequest request) {
		List<Project> projects = (List<Project>) projectService.readMultiple(projectIds);

		Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(projects, ImmutableList.of(), null,
				null, null, null, null, request.getCurrent(), request.getPageSize(), request.getSort());
		List<ProjectSampleTableItem> content = page.getContent()
				.stream()
				.map(ProjectSampleTableItem::new)
				.collect(Collectors.toList());

		AntTableResponse body = new AntTableResponse(content, page.getTotalElements());
		return ResponseEntity.ok(body);
	}
}
