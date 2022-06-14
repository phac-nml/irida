package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionAdminTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiSubmissionModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;

/**
 * Utility class for formatting responses for NCBI Export Listing page UI.
 */
@Component
public class UINcbiService {
	private final ProjectService projectService;
	private final NcbiExportSubmissionService ncbiService;

	@Autowired
	public UINcbiService(ProjectService projectService, NcbiExportSubmissionService ncbiService) {
		this.projectService = projectService;
		this.ncbiService = ncbiService;
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
		return new NcbiSubmissionModel(submission);
	}
}
