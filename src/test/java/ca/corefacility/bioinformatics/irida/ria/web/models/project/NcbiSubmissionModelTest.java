package ca.corefacility.bioinformatics.irida.ria.web.models.project;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.*;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiSubmissionModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NcbiSubmissionModelTest {
	@Test
	public void testNcbiSubmissionModel() {
		Long id = 1L;
		Project project = new Project("Project Name");
		User user = new User(1L, "username", "no-one@nowher.ca", "password", "first", "last", "888");
		String bioProjectId = "12345";
		String organization = "BioProject";
		String ncbiNamespace = "NCBI";
		Date releaseDate = new Date(1655309656L);

		NcbiBioSampleFiles ncbiBioSampleFiles = mock(NcbiBioSampleFiles.class);
		when(ncbiBioSampleFiles.getId()).thenReturn("12345");
		when(ncbiBioSampleFiles.getBioSample()).thenReturn("BioSample");
		when(ncbiBioSampleFiles.getInstrumentModel()).thenReturn(NcbiInstrumentModel.AB_SOLID_SYSTEM);
		when(ncbiBioSampleFiles.getLibraryName()).thenReturn("Library Name");
		when(ncbiBioSampleFiles.getLibrarySelection()).thenReturn(NcbiLibrarySelection.CDNA);
		when(ncbiBioSampleFiles.getLibrarySource()).thenReturn(NcbiLibrarySource.GENOMIC);
		when(ncbiBioSampleFiles.getLibraryStrategy()).thenReturn(NcbiLibraryStrategy.AMPLICON);
		when(ncbiBioSampleFiles.getLibraryConstructionProtocol()).thenReturn("Library Construction Protocol");
		when(ncbiBioSampleFiles.getSubmissionStatus()).thenReturn(ExportUploadState.CREATED);
		when(ncbiBioSampleFiles.getAccession()).thenReturn("Accession");
		when(ncbiBioSampleFiles.getFiles()).thenReturn(ImmutableSet.of());
		when(ncbiBioSampleFiles.getPairs()).thenReturn(ImmutableSet.of());
		List<NcbiBioSampleFiles> files = ImmutableList.of(ncbiBioSampleFiles);

		NcbiExportSubmission submission = mock(NcbiExportSubmission.class);
		when(submission.getId()).thenReturn(id);
		when(submission.getProject()).thenReturn(project);
		when(submission.getSubmitter()).thenReturn(user);
		when(submission.getBioProjectId()).thenReturn(bioProjectId);
		when(submission.getOrganization()).thenReturn(organization);
		when(submission.getNcbiNamespace()).thenReturn(ncbiNamespace);
		when(submission.getReleaseDate()).thenReturn(releaseDate);
		when(submission.getBioSampleFiles()).thenReturn(files);
		when(submission.getUploadState()).thenReturn(ExportUploadState.CREATED);

		NcbiSubmissionModel model = new NcbiSubmissionModel(submission);

		assertEquals(1L, model.getId(), "Id should not be changed");
		assertEquals(project.getName(), model.getProject().getName(), "Project should not be changed");
		assertThat(model.getProject()).isInstanceOf(MinimalModel.class);
		assertEquals(user.getFirstName() + " " + user.getLastName(), model.getSubmitter().getName(),
				"Submitter should have their full name");
		assertThat(model.getSubmitter()).isInstanceOf(MinimalModel.class);
		assertEquals(bioProjectId, model.getBioProject(), "BioProjectId should not be changed");
		assertEquals(organization, model.getOrganization(), "Organization should not be changed");
		assertEquals(ncbiNamespace, model.getNcbiNamespace(), "NcbiNamespace should not be changed");
		assertEquals(releaseDate, model.getReleaseDate(), "ReleaseDate should not be changed");
	}
}