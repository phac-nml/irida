package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Controller managing requests to export project data to external sources.
 */
@Controller
public class ProjectExportController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectExportController.class);

	public static final String NCBI_EXPORT_VIEW = "export/ncbi";

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final SequenceFileService sequenceFileService;
	private final SequenceFilePairService sequenceFilePairService;
	private final NcbiExportSubmissionService exportSubmissionService;

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

	/**
	 * Get the page for exporting a given {@link Project} and selected
	 * {@link Sample}s
	 * 
	 * @param projectId
	 *            The ID of the project to export
	 * @param sampleIds
	 *            A List of sample ids to export
	 * @param model
	 *            model for the view to render
	 * @return Name of the NCBI export page
	 */
	@RequestMapping(value = "/projects/{projectId}/export/ncbi", method = RequestMethod.GET)
	public String getUploadNcbiPage(@PathVariable Long projectId, @RequestParam("s") List<Long> sampleIds,
			Model model) {
		Project project = projectService.read(projectId);

		logger.trace("Reading " + sampleIds.size() + " samples");
		Iterable<Sample> samples = sampleService.readMultiple(sampleIds);

		logger.trace("Got samples");
		Set<Long> checkedSingles = new HashSet<>();
		Set<Long> checkedPairs = new HashSet<>();

		List<Map<String, Object>> sampleList = new ArrayList<>();
		for (Sample sample : samples) {
			Map<String, Object> sampleMap = new HashMap<>();
			sampleMap.put("name", sample.getLabel());
			sampleMap.put("id", sample.getId().toString());

			logger.trace("Doing sample " + sample.getId());

			Map<String, List<? extends Object>> files = new HashMap<>();
			List<SequenceFilePair> sequenceFilePairsForSample = sequenceFilePairService
					.getSequenceFilePairsForSample(sample);
			List<Join<Sample, SequenceFile>> unpairedSequenceFilesForSample = sequenceFileService
					.getUnpairedSequenceFilesForSample(sample);

			Optional<SequenceFilePair> newestPair = sequenceFilePairsForSample.stream()
					.sorted((f1, f2) -> f2.getCreatedDate().compareTo(f1.getCreatedDate())).findFirst();

			Optional<Join<Sample, SequenceFile>> newestSingle = unpairedSequenceFilesForSample.stream()
					.sorted((f1, f2) -> f2.getCreatedDate().compareTo(f1.getCreatedDate())).findFirst();

			if (newestPair.isPresent() && newestSingle.isPresent()) {
				SequenceFilePair sequenceFilePair = newestPair.get();
				Join<Sample, SequenceFile> join = newestSingle.get();

				if (sequenceFilePair.getCreatedDate().after(join.getCreatedDate())) {
					checkedPairs.add(newestPair.get().getId());
				} else {
					checkedSingles.add(newestSingle.get().getId());
				}
			} else {
				if (newestPair.isPresent()) {
					checkedPairs.add(newestPair.get().getId());
				} else if (newestSingle.isPresent()) {
					checkedSingles.add(newestSingle.get().getId());
				}
			}

			files.put("paired_end", sequenceFilePairsForSample);
			files.put("single_end", unpairedSequenceFilesForSample);

			sampleMap.put("files", files);
			sampleList.add(sampleMap);
		}

		model.addAttribute("project", project);
		model.addAttribute("samples", sampleList);

		model.addAttribute("newestSingles", checkedSingles);
		model.addAttribute("newestPairs", checkedPairs);

		return NCBI_EXPORT_VIEW;
	}

	/**
	 * Save an NCBI submission to the database
	 * 
	 * @param projectId
	 *            the ID of the {@link Project} for the submission
	 * @param submission
	 *            A {@link SubmissionBody} describing the files to upload
	 * @return ID of the submission if successful
	 */
	@RequestMapping(value = "/projects/{projectId}/export/ncbi", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> submitToNcbi(@PathVariable Long projectId, @RequestBody SubmissionBody submission) {
		Project project = projectService.read(projectId);

		List<SequenceFile> singleFiles = Lists.newArrayList(sequenceFileService.readMultiple(submission.getSingle()));
		List<SequenceFilePair> pairFiles = Lists
				.newArrayList(sequenceFilePairService.readMultiple(submission.getPaired()));

		NcbiExportSubmission ncbiExportSubmission = new NcbiExportSubmission(project, submission.getBioProject(),
				submission.getNamespace(), singleFiles, pairFiles);
		ncbiExportSubmission = exportSubmissionService.create(ncbiExportSubmission);

		return ImmutableMap.of("submissionId", ncbiExportSubmission.getId());
	}

	/**
	 * Class storing IDs of single and paired end files submitted for upload
	 */
	protected static class SubmissionBody {

		@JsonProperty
		String bioProject;

		@JsonProperty
		String namespace;

		@JsonProperty
		List<Long> single;

		@JsonProperty
		List<Long> paired;

		public SubmissionBody() {
		}

		public List<Long> getSingle() {
			return single;
		}

		public List<Long> getPaired() {
			return paired;
		}

		public String getBioProject() {
			return bioProject;
		}

		public String getNamespace() {
			return namespace;
		}

	}
}
