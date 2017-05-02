package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DatatablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.ProjectSampleModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping("/ajax/projects/{projectId}/samples")
public class ProjectsSamplesAjaxController {
	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public ProjectsSamplesAjaxController(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	@RequestMapping("")
	@ResponseBody
	public DatatablesResponse getSamplesForProject(@PathVariable Long projectId, @RequestParam int draw, @RequestParam int start, @RequestParam int length, @DatatablesRequest DatatablesParams DatatablesParams) {
		Project project = projectService.read(projectId);
		int currentPage = (int) Math.floor(start / length);
		List projects = ImmutableList.of(project);
		final Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(projects, ImmutableList.of(), null, null, null, null, null, currentPage, 10, Sort.Direction.ASC, "sample.sampleName");

		// Create a more usable Map of the sample data.
		List<Object> models = page.getContent().stream().map(j -> buildProjectSampleModel(j))
				.collect(Collectors.toList());
		return new DatatablesResponse(draw, page.getTotalElements(), page.getTotalElements(), models);
	}

	private ProjectSampleModel buildProjectSampleModel(ProjectSampleJoin sso) {
		Project p = sso.getSubject();

		List<QCEntry> qcEntriesForSample = sampleService.getQCEntriesForSample(sso.getObject());

		//add the project settings for the qc entries
		qcEntriesForSample.forEach(q -> q.addProjectSettings(p));
		return new ProjectSampleModel(sso, qcEntriesForSample);
	}
}
