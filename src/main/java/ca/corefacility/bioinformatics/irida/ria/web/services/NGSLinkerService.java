package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.LinkerCmdRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Component
public class NGSLinkerService {
	private @Value("${ngsarchive.linker.script}") String LINKER_SCRIPT;

	private ProjectService projectService;
	private SampleService sampleService;

	@Autowired
	public NGSLinkerService(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	public String generateLinkerCommand(LinkerCmdRequest request) {
		Project project = projectService.read(request.getProjectId());
		int totalSamples = sampleService.getSamplesForProject(project)
				.size();

		/*
		Generate basic command, this is the same if it is for the whole
		project or just a couple samples.
		 */
		StringBuilder command = new StringBuilder(LINKER_SCRIPT);
		command.append(" -p");

		/*
		Determine if we need to add specific sample id's. This means the
		whole project is not being linked.
		 */
		List<Long> ids = request.getSampleIds();
		if (ids.size() != 0 && ids.size() != totalSamples) {
			ids.forEach(id -> command.append(" -s ").append(id));
		}

		return command.toString();
	}
}
