package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.export.*;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles.Builder;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

/**
 * Controller managing requests to export project data to external sources.
 */
@Controller
public class ProjectExportController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectExportController.class);

	public static final String NCBI_EXPORT_VIEW = "projects/export/ncbi";
	public static final String EXPORT_DETAILS_VIEW = "projects/export/details";
	public static final String EXPORT_LIST_VIEW = "projects/export/list";
	public static final String EXPORT_ADMIN_VIEW = "projects/export/admin";

	@Value("${ncbi.upload.namespace}")
	private String namespace = "";

	private final ProjectControllerUtils projectControllerUtils;
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final SequencingObjectService sequencingObjectService;
	private final NcbiExportSubmissionService exportSubmissionService;
	private final UserService userService;

	@Autowired
	public ProjectExportController(ProjectService projectService, SampleService sampleService,
			ProjectControllerUtils projectControllerUtils, SequencingObjectService sequencingObjectService,
			NcbiExportSubmissionService exportSubmissionService, UserService userService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.projectControllerUtils = projectControllerUtils;
		this.sequencingObjectService = sequencingObjectService;
		this.exportSubmissionService = exportSubmissionService;
		this.userService = userService;
	}

	/**
	 * Get the page for exporting a given {@link Project} and selected {@link Sample}s
	 *
	 * @param projectId The ID of the project to export
	 * @param sampleIds A List of sample ids to export
	 * @param model     model for the view to render
	 * @param principal {@link Principal} currently logged in user
	 * @return Name of the NCBI export page
	 */
	@RequestMapping(value = "/projects/{projectId}/export/ncbi", method = RequestMethod.GET)
	public String getUploadNcbiPage(@PathVariable Long projectId, @RequestParam("ids") List<Long> sampleIds,
			Model model, Principal principal) {
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

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

			Collection<SampleSequencingObjectJoin> singleEndFiles = sequencingObjectService
					.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class);
			Collection<SampleSequencingObjectJoin> pairedEndFiles = sequencingObjectService
					.getSequencesForSampleOfType(sample, SequenceFilePair.class);

			List<SingleEndSequenceFile> singleEndFilesForSample = singleEndFiles.stream()
					.map(j -> (SingleEndSequenceFile) j.getObject())
					.collect(Collectors.toList());
			List<SequenceFilePair> sequenceFilePairsForSample = pairedEndFiles.stream()
					.map(j -> (SequenceFilePair) j.getObject())
					.collect(Collectors.toList());

			Optional<SequenceFilePair> newestPair = sequenceFilePairsForSample.stream()
					.sorted((f1, f2) -> f2.getCreatedDate().compareTo(f1.getCreatedDate()))
					.findFirst();

			Optional<SingleEndSequenceFile> newestSingle = singleEndFilesForSample.stream()
					.sorted((f1, f2) -> f2.getCreatedDate().compareTo(f1.getCreatedDate()))
					.findFirst();

			if (newestPair.isPresent() && newestSingle.isPresent()) {
				SequenceFilePair sequenceFilePair = newestPair.get();
				SingleEndSequenceFile join = newestSingle.get();

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
			files.put("single_end", singleEndFilesForSample);

			sampleMap.put("files", files);
			sampleList.add(sampleMap);
		}

		sampleList.sort(new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String s1Name = (String) o1.get("name");
				String s2Name = (String) o2.get("name");
				return s1Name.compareTo(s2Name);
			}
		});

		model.addAttribute("project", project);
		model.addAttribute("samples", sampleList);

		model.addAttribute("newestSingles", checkedSingles);
		model.addAttribute("newestPairs", checkedPairs);

		model.addAttribute("instrument_model", NcbiInstrumentModel.values());
		model.addAttribute("library_selection", NcbiLibrarySelection.values());
		model.addAttribute("library_source", NcbiLibrarySource.values());
		model.addAttribute("library_strategy", NcbiLibraryStrategy.values());
		model.addAttribute("defaultNamespace", namespace);
		model.addAttribute("activeNav", "export");

		return NCBI_EXPORT_VIEW;
	}

	/**
	 * Save an NCBI submission to the database
	 *
	 * @param projectId  the ID of the {@link Project} for the submission
	 * @param submission A {@link SubmissionBody} describing the files to upload
	 * @param principal  the user submitting the upload
	 * @return ID of the submission if successful
	 * @throws InterruptedException if thread was not successfully put to sleep
	 */
	@RequestMapping(value = "/projects/{projectId}/export/ncbi", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> submitToNcbi(@PathVariable Long projectId, @RequestBody SubmissionBody submission,
			Principal principal) throws InterruptedException {
		Project project = projectService.read(projectId);
		User submitter = userService.getUserByUsername(principal.getName());

		List<NcbiBioSampleFiles> bioSampleFiles = new ArrayList<>();

		for (BioSampleBody sample : submission.getSamples()) {
			Set<SingleEndSequenceFile> singleFiles = new HashSet<>();
			sequencingObjectService.readMultiple(sample.getSingle())
					.forEach(f -> singleFiles.add((SingleEndSequenceFile) f));

			HashSet<SequenceFilePair> paired = new HashSet<>();
			sequencingObjectService.readMultiple(sample.getPaired()).forEach(f -> paired.add((SequenceFilePair) f));

			Builder sampleBuilder = new NcbiBioSampleFiles.Builder();
			sampleBuilder.bioSample(sample.getBioSample())
					.files(singleFiles)
					.pairs(paired)
					.instrumentModel(sample.getInstrumentModel())
					.libraryConstructionProtocol(sample.getLibraryConstructionProtocol())
					.libraryName(sample.getLibraryName())
					.librarySelection(sample.getLibrarySelection())
					.librarySource(sample.getLibrarySource())
					.libraryStrategy(sample.getLibraryStrategy())
					.namespace(submission.getNamespace());
			NcbiBioSampleFiles build = sampleBuilder.build();
			bioSampleFiles.add(build);
		}

		NcbiExportSubmission ncbiExportSubmission = new NcbiExportSubmission(project, submitter,
				submission.getBioProject(), submission.getOrganization(), submission.getNamespace(),
				submission.getReleaseDate(), bioSampleFiles);

		ncbiExportSubmission = exportSubmissionService.create(ncbiExportSubmission);

		return ImmutableMap.of("submissionId", ncbiExportSubmission.getId());
	}

	/**
	 * Get the details view of a given {@link NcbiExportSubmission}
	 *
	 * @param projectId    ID of the {@link Project} the export is for
	 * @param submissionId the {@link NcbiExportSubmission} id
	 * @param model        model for the view
	 * @param principal    {@link Principal} currently logged in user
	 * @return name of the details view
	 */
	@RequestMapping("/projects/{projectId}/export/{submissionId}")
	public String getDetailsView(@PathVariable Long projectId, @PathVariable Long submissionId, Model model,
			Principal principal) {
		NcbiExportSubmission submission = exportSubmissionService.read(submissionId);
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("submission", submission);
		model.addAttribute("activeNav", "export");
		return EXPORT_DETAILS_VIEW;
	}

	/**
	 * Get the project export list view
	 *
	 * @param projectId which {@link Project} to get exports for
	 * @param model     model for the view
	 * @param principal The currently logged in user
	 * @return name of the exports list view
	 */
	@RequestMapping("/projects/{projectId}/export")
	public String getExportsPage(@PathVariable Long projectId, Model model, Principal principal) {
		Project project = projectService.read(projectId);
		// Set up the template information
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("project", project);
		model.addAttribute("activeNav", "export");
		return EXPORT_LIST_VIEW;
	}

	/**
	 * Get the view to see all ncbi exports
	 *
	 * @param model model for the view
	 * @return name of the exports list view
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("/export/ncbi")
	public String getExportsPage(Model model) {
		return EXPORT_ADMIN_VIEW;
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
		String organization;

		@JsonProperty("release_date")
		Date releaseDate;

		@JsonProperty
		List<BioSampleBody> samples;

		public SubmissionBody() {
		}

		public String getBioProject() {
			return bioProject;
		}

		public String getNamespace() {
			return namespace;
		}

		public List<BioSampleBody> getSamples() {
			return samples;
		}

		public Date getReleaseDate() {
			return releaseDate;
		}

		public String getOrganization() {
			return organization;
		}

	}

	/**
	 * Properties of a NCBI BioSample being exported
	 */
	protected static class BioSampleBody {
		@JsonProperty
		String bioSample;

		@JsonProperty("library_name")
		String libraryName;

		@JsonProperty("library_selection")
		NcbiLibrarySelection librarySelection;

		@JsonProperty("library_source")
		NcbiLibrarySource librarySource;

		@JsonProperty("library_strategy")
		NcbiLibraryStrategy libraryStrategy;

		@JsonProperty("library_construction_protocol")
		String libraryConstructionProtocol;

		@JsonProperty("instrument_model")
		NcbiInstrumentModel instrumentModel;

		@JsonProperty
		List<Long> single;

		@JsonProperty
		List<Long> paired;

		public String getBioSample() {
			return bioSample;
		}

		public List<Long> getPaired() {
			return paired;
		}

		public List<Long> getSingle() {
			return single;
		}

		public NcbiInstrumentModel getInstrumentModel() {
			return instrumentModel;
		}

		public String getLibraryConstructionProtocol() {
			return libraryConstructionProtocol;
		}

		public String getLibraryName() {
			return libraryName;
		}

		public NcbiLibrarySelection getLibrarySelection() {
			return librarySelection;
		}

		public NcbiLibrarySource getLibrarySource() {
			return librarySource;
		}

		public NcbiLibraryStrategy getLibraryStrategy() {
			return libraryStrategy;
		}
	}
}
