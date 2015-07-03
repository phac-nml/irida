package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller
public class ProjectExportController {

	public static final String NCBI_EXPORT_VIEW = "export/ncbi";
	ProjectService projectService;
	SampleService sampleService;
	SequenceFileService sequenceFileService;
	SequenceFilePairService sequenceFilePairService;

	@Autowired
	public ProjectExportController(ProjectService projectService, SampleService sampleService,
			SequenceFileService sequenceFileService, SequenceFilePairService sequenceFilePairService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
	}

	@RequestMapping("/projects/{projectId}/export/ncbi")
	public String getUploadNcbiPage(@PathVariable Long projectId, @RequestParam List<Long> sampleId, Model model) {
		Project project = projectService.read(projectId);
		List<Sample> samples = sampleId.stream().map((i) -> sampleService.getSampleForProject(project, i))
				.collect(Collectors.toList());

		List<Map<String, Object>> sampleList = new ArrayList<>();
		for (Sample sample : samples) {
			Map<String, Object> sampleMap = new HashMap<>();
			sampleMap.put("name", sample.getLabel());
			sampleMap.put("id", sample.getId().toString());
			Map<String, List<? extends Object>> files = new HashMap<>();

			files.put("paired_end", sequenceFilePairService.getSequenceFilePairsForSample(sample));

			files.put("single_end", sequenceFileService.getUnpairedSequenceFilesForSample(sample));

			sampleMap.put("files", files);
			sampleList.add(sampleMap);
		}
		
		model.addAttribute("project", project);
		model.addAttribute("samples", sampleList);

		return NCBI_EXPORT_VIEW;
	}

}
