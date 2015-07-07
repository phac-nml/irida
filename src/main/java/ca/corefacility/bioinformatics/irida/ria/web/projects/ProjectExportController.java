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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.NcbiExportSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

@Controller
public class ProjectExportController {

	public static final String NCBI_EXPORT_VIEW = "export/ncbi";
	ProjectService projectService;
	SampleService sampleService;
	SequenceFileService sequenceFileService;
	SequenceFilePairService sequenceFilePairService;
	NcbiExportSubmissionService exportSubmissionService;

	@Autowired
	public ProjectExportController(ProjectService projectService, SampleService sampleService,
			SequenceFileService sequenceFileService, SequenceFilePairService sequenceFilePairService,
			NcbiExportSubmissionService exportSubmissionService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
		this.exportSubmissionService = exportSubmissionService;
	}

	@RequestMapping(value = "/projects/{projectId}/export/ncbi", method = RequestMethod.GET)
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

	@RequestMapping(value = "/projects/{projectId}/export/ncbi", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> submitToNcbi(@PathVariable Long projectId, @RequestBody SubmissionBody submission) {
		Project project = projectService.read(projectId);

		List<SequenceFile> singleFiles = submission.getSingle().stream().map(sequenceFileService::read)
				.collect(Collectors.toList());

		List<SequenceFilePair> pairFiles = submission.getPaired().stream().map(sequenceFilePairService::read)
				.collect(Collectors.toList());

		NcbiExportSubmission ncbiExportSubmission = new NcbiExportSubmission(project, singleFiles, pairFiles);
		ncbiExportSubmission = exportSubmissionService.create(ncbiExportSubmission);

		return ImmutableMap.of("submissionId", ncbiExportSubmission.getId());
	}

	static class SubmissionBody {
		@JsonProperty
		List<Long> single;

		@JsonProperty
		List<Long> paired;

		public SubmissionBody() {
		}

		public SubmissionBody(List<Long> single, List<Long> paired) {
			this.single = single;
			this.paired = paired;
		}

		public List<Long> getSingle() {
			return single;
		}

		public List<Long> getPaired() {
			return paired;
		}

	}
}
