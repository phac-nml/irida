package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
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
	public DatatablesResponse getSamplesForProject(@PathVariable Long projectId,
			@DatatablesRequest DatatablesParams datatablesParams) {
		Project project = projectService.read(projectId);
		List<Project> projects = ImmutableList.of(project);
		List<String> sampleNames = ImmutableList.of();

		final Page<ProjectSampleJoin> page = sampleService
				.getFilteredSamplesForProjects(projects, sampleNames, null, datatablesParams.getSearchValue(),
						null, null, null, datatablesParams.getCurrentPage(), datatablesParams.getLength(),
						datatablesParams.getSort());

		// Create a more usable Map of the sample data.
		List<Object> data = page.getContent().stream().map(this::buildProjectSampleModel)
				.collect(Collectors.toList());
		return new DatatablesResponse(datatablesParams, page, data);
	}

	private ProjectSampleModel buildProjectSampleModel(ProjectSampleJoin sso) {
		Project p = sso.getSubject();

		List<QCEntry> qcEntriesForSample = sampleService.getQCEntriesForSample(sso.getObject());

		//add the project settings for the qc entries
		qcEntriesForSample.forEach(q -> q.addProjectSettings(p));
		return new ProjectSampleModel(sso, qcEntriesForSample);
	}
}
