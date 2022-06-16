package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionAdminTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UINcbiServiceTest {
	private UINcbiService uiNcbiService;

	private final Long projectId = 1L;
	private TableRequest request;

	@BeforeEach
	void setUp() {
		ProjectService projectService = mock(ProjectService.class);
		NcbiExportSubmissionService ncbiExportSubmissionService = mock(NcbiExportSubmissionService.class);
		this.uiNcbiService = new UINcbiService(projectService, ncbiExportSubmissionService);

		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		when(user.getEmail()).thenReturn("test@no-where.ca");
		when(user.getFirstName()).thenReturn("Test");
		when(user.getLastName()).thenReturn("User");

		Project project = mock(Project.class);
		when(project.getId()).thenReturn(projectId);
		when(project.getName()).thenReturn("Test Project");
		when(projectService.read(projectId)).thenReturn(project);

		NcbiExportSubmission submission1 = mock(NcbiExportSubmission.class);
		when(submission1.getUploadState()).thenReturn(ExportUploadState.CREATED);
		when(submission1.getProject()).thenReturn(project);
		when(submission1.getSubmitter()).thenReturn(user);

		NcbiExportSubmission submission2 = mock(NcbiExportSubmission.class);
		when(submission2.getUploadState()).thenReturn(ExportUploadState.UPLOADED);
		when(submission2.getProject()).thenReturn(project);
		when(submission2.getSubmitter()).thenReturn(user);

		NcbiExportSubmission submission3 = mock(NcbiExportSubmission.class);
		when(submission3.getUploadState()).thenReturn(ExportUploadState.PROCESSED_OK);
		when(submission3.getProject()).thenReturn(project);
		when(submission3.getSubmitter()).thenReturn(user);

		when(ncbiExportSubmissionService.getSubmissionsForProject(project)).thenReturn(
				List.of(submission1, submission2, submission3));

		request = mock(TableRequest.class);
		when(request.getCurrent()).thenReturn(1);
		when(request.getPageSize()).thenReturn(10);
		when(request.getSort()).thenReturn(Sort.by(Sort.Direction.ASC, "id"));
		Page<NcbiExportSubmission> page = mock(Page.class);
		when(ncbiExportSubmissionService.list(request.getCurrent(), request.getPageSize(), request.getSort())).thenReturn()
	}

	@Test
	void getNCBIExportsForProject() {
		List<NcbiExportSubmissionTableModel> models = uiNcbiService.getNCBIExportsForProject(projectId);
		assertEquals(3, models.size(), "There should be 3 NCBI export submissions");
	}

	@Test
	void getNCBIExportsForAdmin() {
		TableRequest tableRequest = mock(TableRequest.class);

		TableResponse<NcbiExportSubmissionAdminTableModel> response = uiNcbiService.getNCBIExportsForAdmin(
				tableRequest);

	}

	@Test
	void getExportDetails() {
	}
}