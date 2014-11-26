package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller for ajax request dealing with project reference files.
 *
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/projects/{projectId}/ajax/reference")
public class ProjectReferenceFileController {
	private final ProjectService projectService;
	private final ReferenceFileService referenceFileService;

	/*
	 * Converters
	 */
	FileSizeConverter fileSizeConverter;

	@Autowired
	public ProjectReferenceFileController(ProjectService projectService, ReferenceFileService referenceFileService) {
		this.projectService = projectService;
		this.referenceFileService = referenceFileService;
		this.fileSizeConverter = new FileSizeConverter();
	}

	@RequestMapping("/all")
	public @ResponseBody Map<String, Object> getReferenceFilesForProject(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		// Let's add the reference files
		List<Map<String, Object>> files = null;
		try {
			files = getReferenceFileData(project);
		} catch (IOException e) {
			// TODO: HANDLE THIS PROPERLY!
		}
		return ImmutableMap.of("files", files);
	}

	/**
	 * Get the information about a projects reference files in a format that can be used by the UI.
	 *
	 * @param project
	 * 		{@link Project} Currently viewed project.
	 *
	 * @return List of reference file info.
	 * @throws IOException
	 */
	private List<Map<String, Object>> getReferenceFileData(Project project) throws IOException {
		List<Join<Project, ReferenceFile>> joinList = referenceFileService.getReferenceFilesForProject(project);
		List<Map<String, Object>> mapList = new ArrayList<>();
		for (Join<Project, ReferenceFile> join : joinList) {
			ReferenceFile file = join.getObject();
			Map<String, Object> map = new HashMap<>();
			map.put("id", file.getId().toString());
			map.put("label", file.getLabel());
			map.put("createdDate", file.getCreatedDate());
			Path path = file.getFile();
			long size = 0;
			if (Files.exists(path)) {
				size = Files.size(path);
			}
			map.put("size", fileSizeConverter.convert(size));
			mapList.add(map);
		}
		return mapList;
	}
}
