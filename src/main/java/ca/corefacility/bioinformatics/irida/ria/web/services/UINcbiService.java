package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiBioSampleModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiExportSubmissionAdminTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiSubmissionBody;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiSubmissionModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Utility class for formatting responses for NCBI Export Listing page UI.
 */
@Component
public class UINcbiService {
	private final ProjectService projectService;
	private final NcbiExportSubmissionService ncbiService;
	private final SequencingObjectService sequencingObjectService;
	private final UserService userService;
	private final UISampleService uiSampleService;

	@Autowired
	public UINcbiService(ProjectService projectService, NcbiExportSubmissionService ncbiService,
			SequencingObjectService sequencingObjectService, UserService userService, UISampleService uiSampleService) {
		this.projectService = projectService;
		this.ncbiService = ncbiService;
		this.sequencingObjectService = sequencingObjectService;
		this.userService = userService;
		this.uiSampleService = uiSampleService;
	}

	/**
	 * Get a {@link List} of all {@link NcbiExportSubmission} that have occurred on a {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project} for the {@link NcbiExportSubmission}
	 * @return {@link List} of {@link NcbiExportSubmissionTableModel}
	 */
	public List<NcbiExportSubmissionTableModel> getNCBIExportsForProject(Long projectId) {
		Project project = projectService.read(projectId);
		List<NcbiExportSubmission> submissions = ncbiService.getSubmissionsForProject(project);
		return submissions.stream()
				.map(NcbiExportSubmissionTableModel::new)
				.collect(Collectors.toList());
	}

	/**
	 * Get a {@link Page} of {@link NcbiExportSubmission}
	 *
	 * @param request {@link TableRequest} containing the details about the specific {@link Page} of {@link NcbiExportSubmission}
	 *                wanted
	 * @return {@link TableResponse} of {@link NcbiExportSubmissionAdminTableModel}
	 */
	public TableResponse<NcbiExportSubmissionAdminTableModel> getNCBIExportsForAdmin(TableRequest request) {
		Page<NcbiExportSubmission> page = ncbiService.list(request.getCurrent(), request.getPageSize(),
				request.getSort());
		List<NcbiExportSubmissionAdminTableModel> submissions = page.getContent()
				.stream()
				.map(NcbiExportSubmissionAdminTableModel::new)
				.collect(Collectors.toList());
		return new TableResponse<>(submissions, page.getTotalElements());
	}

	/**
	 * Get the details for an {@link NcbiExportSubmission} for the UI
	 *
	 * @param exportId Identifier for the submission
	 * @return Submission details
	 */
	public NcbiSubmissionModel getExportDetails(Long exportId) {
		NcbiExportSubmission submission = ncbiService.read(exportId);
		Project project = projectService.read(submission.getProject().getId());

		List<NcbiBioSampleModel> bioSamples = submission.getBioSampleFiles().stream().map(bioSampleFile -> {
			List<SequencingObject> pairs = bioSampleFile.getPairs().stream()
					.peek(pair -> uiSampleService.enhanceQcEntries(pair, project))
					.collect(Collectors.toList());
			List<SequencingObject> singles = bioSampleFile.getFiles().stream()
					.peek(single -> uiSampleService.enhanceQcEntries(single, project))
					.collect(Collectors.toList());
			return new NcbiBioSampleModel(bioSampleFile, pairs, singles);
		}).collect(Collectors.toList());

		return new NcbiSubmissionModel(submission, bioSamples);
	}

	public void submitNcbiExport(NcbiSubmissionBody submission) {
		Project project = projectService.read(submission.getProjectId());
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userService.getUserByUsername(username);

		List<NcbiBioSampleFiles> files = submission.getSamples().stream()
				.map(sample -> {
					Set<SingleEndSequenceFile> singleFiles = new HashSet<>();
					sequencingObjectService.readMultiple(sample.getSingle())
							.forEach(f -> singleFiles.add((SingleEndSequenceFile) f));

					HashSet<SequenceFilePair> paired = new HashSet<>();
					sequencingObjectService.readMultiple(sample.getPaired()).forEach(f -> paired.add((SequenceFilePair) f));

					NcbiBioSampleFiles.Builder sampleBuilder = new NcbiBioSampleFiles.Builder();
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
					return sampleBuilder.build();
				})
				.collect(Collectors.toList());

		NcbiExportSubmission submissionModel = new NcbiExportSubmission(project, user, submission.getBioProject(),
				submission.getOrganization(), submission.getNamespace(), submission.getReleaseDate(), files);
		ncbiService.create(submissionModel);
	}
}
