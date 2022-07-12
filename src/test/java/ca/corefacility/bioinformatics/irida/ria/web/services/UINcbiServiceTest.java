package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiExportSubmissionAdminTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiSubmissionModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.export.NcbiExportSubmissionService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UINcbiServiceTest {
	private UINcbiService uiNcbiService;
	private ProjectService projectService;
	private NcbiExportSubmissionService ncbiExportSubmissionService;

	// DATA // ---------------------------------------------------------------------------------------------------------

	private final Long projectId = 1L;
	private TableRequest request;
	private final Date createdDate = new Date(1655389918L);
	private final Date modifiedDate = new Date(1655389918L);
	private NcbiExportSubmission submission1;
	private NcbiExportSubmission submission2;
	private NcbiExportSubmission submission3;

	// END DATA // -----------------------------------------------------------------------------------------------------

	@BeforeEach
	void setUp() {
		projectService = mock(ProjectService.class);
		ncbiExportSubmissionService = mock(NcbiExportSubmissionService.class);
		this.uiNcbiService = new UINcbiService(projectService, ncbiExportSubmissionService, sequencingObjectService,
				userService);

		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		when(user.getEmail()).thenReturn("test@no-where.ca");
		when(user.getFirstName()).thenReturn("Test");
		when(user.getLastName()).thenReturn("User");

		Project project = mock(Project.class);
		when(project.getId()).thenReturn(projectId);
		when(project.getName()).thenReturn("Test Project");
		when(projectService.read(projectId)).thenReturn(project);

		submission1 = mock(NcbiExportSubmission.class);
		when(submission1.getId()).thenReturn(1L);
		when(submission1.getUploadState()).thenReturn(ExportUploadState.CREATED);
		when(submission1.getProject()).thenReturn(project);
		when(submission1.getSubmitter()).thenReturn(user);
		when(submission1.getCreatedDate()).thenReturn(createdDate);
		when(submission1.getModifiedDate()).thenReturn(modifiedDate);
		when(submission1.getBioSampleFiles()).thenReturn(ImmutableList.of());
		when(submission1.getBioProjectId()).thenReturn("12345");
		when(ncbiExportSubmissionService.read(1L)).thenReturn(submission1);

		submission2 = mock(NcbiExportSubmission.class);
		when(submission2.getId()).thenReturn(2L);
		when(submission2.getUploadState()).thenReturn(ExportUploadState.UPLOADED);
		when(submission2.getProject()).thenReturn(project);
		when(submission2.getSubmitter()).thenReturn(user);
		when(submission2.getCreatedDate()).thenReturn(createdDate);
		when(submission2.getModifiedDate()).thenReturn(modifiedDate);
		when(submission1.getBioSampleFiles()).thenReturn(ImmutableList.of());
		when(submission1.getBioProjectId()).thenReturn("67890");
		when(ncbiExportSubmissionService.read(2L)).thenReturn(submission2);

		submission3 = mock(NcbiExportSubmission.class);
		when(submission3.getId()).thenReturn(3L);
		when(submission3.getUploadState()).thenReturn(ExportUploadState.PROCESSED_OK);
		when(submission3.getProject()).thenReturn(project);
		when(submission3.getSubmitter()).thenReturn(user);
		when(submission3.getCreatedDate()).thenReturn(createdDate);
		when(submission3.getModifiedDate()).thenReturn(modifiedDate);
		when(submission1.getBioSampleFiles()).thenReturn(ImmutableList.of());
		when(submission1.getBioProjectId()).thenReturn("98765");
		when(ncbiExportSubmissionService.read(3L)).thenReturn(submission3);

		when(ncbiExportSubmissionService.getSubmissionsForProject(project)).thenReturn(
				List.of(submission1, submission2, submission3));

		request = mock(TableRequest.class);
		when(request.getCurrent()).thenReturn(1);
		when(request.getPageSize()).thenReturn(10);
		when(request.getSort()).thenReturn(Sort.by(Sort.Direction.ASC, "id"));

		List<NcbiExportSubmission> adminTableModels = ImmutableList.of(submission1, submission2, submission3);
		Page<NcbiExportSubmission> page = new PageImpl<>(adminTableModels);
		when(ncbiExportSubmissionService.list(request.getCurrent(), request.getPageSize(),
				request.getSort())).thenReturn(page);
	}

	@Test
	void getNCBIExportsForProject() {
		List<NcbiExportSubmissionTableModel> models = uiNcbiService.getNCBIExportsForProject(projectId);
		assertEquals(3, models.size(), "There should be 3 NCBI export submissions");
	}

	@Test
	void getNCBIExportsForProject_noSubmissions() {
		long emptyProjectId = 4L;
		Project project = mock(Project.class);
		when(project.getId()).thenReturn(emptyProjectId);
		when(ncbiExportSubmissionService.getSubmissionsForProject(project)).thenReturn(Collections.emptyList());
		List<NcbiExportSubmissionTableModel> response = uiNcbiService.getNCBIExportsForProject(emptyProjectId);
		assertEquals(0, response.size(), "There should be 0 NCBI export submissions");
	}

	@Test
	void getNCBIExportsForProject_noValidProject() {
		long invalidProjectId = -1L;
		when(projectService.read(invalidProjectId)).thenThrow(new EntityNotFoundException("Project not found"));
		assertThrows(EntityNotFoundException.class, () -> uiNcbiService.getNCBIExportsForProject(invalidProjectId));
	}

	@Test
	void getNCBIExportsForAdmin() {
		TableResponse<NcbiExportSubmissionAdminTableModel> response = uiNcbiService.getNCBIExportsForAdmin(request);
		assertEquals(3, response.getDataSource().size(), "There should be 3 NCBI export submissions");
		assertEquals(3, response.getTotal(), "There should be 3 NCBI export submissions");
	}

	@Test
	void getNCBIExportsForAdmin_noSubmissions() {
		when(ncbiExportSubmissionService.list(request.getCurrent(), request.getPageSize(),
				request.getSort())).thenReturn(new PageImpl<>(Collections.emptyList()));
		TableResponse<NcbiExportSubmissionAdminTableModel> response = uiNcbiService.getNCBIExportsForAdmin(request);
		assertEquals(0, response.getDataSource().size(), "There should be 0 NCBI export submissions");
		assertEquals(0, response.getTotal(), "There should be 0 NCBI export submissions");
	}

	@Test
	void getExportDetails() {
		NcbiSubmissionModel model = uiNcbiService.getExportDetails(submission1.getId());
		assertEquals(submission1.getId(), model.getId(), "The ID should be 1");
		assertEquals(submission1.getBioProjectId(), model.getBioProject(), "The BioProject ID should be 12345");
		assertEquals(submission1.getUploadState().name(), model.getState(), "The upload state should be CREATED");
		assertEquals(submission1.getSubmitter().getId(), model.getSubmitter().getId(), "The submitter ID should be 1");
	}

	@Test
	void getExportDetails_invalidSubmission() {
		when(ncbiExportSubmissionService.read(1L)).thenThrow(new EntityNotFoundException("Submission not found"));
		assertThrows(EntityNotFoundException.class, () -> uiNcbiService.getExportDetails(1L));
	}
}