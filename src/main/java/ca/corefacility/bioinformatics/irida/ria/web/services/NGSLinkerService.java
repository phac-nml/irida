package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.NGSLinkerCmdRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Service for converting data for creating the ngs-linker command.
 */
@Component
public class NGSLinkerService {
	private @Value("${ngsarchive.linker.script}")
	String LINKER_SCRIPT;

	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public NGSLinkerService(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	/**
	 * Generate the full ngs-linker command.
	 * If just the project identifier, or identifier for all samples in the project, are passed,
	 * then we want to export the entire project.  If sample ids are passed then we need to add
	 * the specific identifiers to export.
	 *
	 * @param request {@link NGSLinkerCmdRequest}
	 * @return the actual ngs-linker command.
	 */
	public String generateLinkerCommand(NGSLinkerCmdRequest request) {
		Project project = projectService.read(request.getProjectId());
		Long totalSamples = sampleService.getNumberOfSamplesForProject(project);

		/*
		Generate basic command, this is the same if it is for the whole
		project or just a couple samples.
		 */
		StringBuilder command = new StringBuilder(LINKER_SCRIPT);
		command.append(" -p ")
				.append(request.getProjectId());

		/*
		Determine if we need to add specific sample id's. This means the
		whole project is not being linked.
		 */
		List<Long> ids = request.getSampleIds();
		if (ids.size() != 0 && ids.size() != totalSamples) {
			ids.forEach(id -> command.append(" -s ")
					.append(id));
		}

		return command.toString();
	}
}
