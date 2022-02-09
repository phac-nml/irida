package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/ReferenceFileServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ReferenceFileServiceImplIT {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ReferenceFileService referenceFileService;

	@Autowired
	@Qualifier("referenceFileBaseDirectory")
	private Path referenceFileBaseDirectory;

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetReferenceFilesForProject() {
		Project p = projectService.read(1L);
		List<Join<Project, ReferenceFile>> prs = referenceFileService.getReferenceFilesForProject(p);
		assertEquals(1, prs.size(), "Wrong number of reference files for project.");
		ReferenceFile rf = prs.iterator().next().getObject();
		assertEquals(Long.valueOf(1), rf.getId(), "Wrong reference file attached to project.");
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testReadNotAllowed() {
		assertThrows(AccessDeniedException.class, () -> {
			referenceFileService.read(1L);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testRead() {
		ReferenceFile read = referenceFileService.read(1L);
		assertNotNull(read);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testDeleteReferenceFile() {
		Project p = projectService.read(1L);
		List<Join<Project, ReferenceFile>> prs = referenceFileService.getReferenceFilesForProject(p);
		assertEquals(1, prs.size(), "Wrong number of reference files for project.");
		ReferenceFile rf = prs.iterator().next().getObject();
		assertEquals(Long.valueOf(1), rf.getId(), "Wrong reference file attached to project.");

		referenceFileService.delete(rf.getId());
		p = projectService.read(1L);
		prs = referenceFileService.getReferenceFilesForProject(p);
		assertEquals(0, prs.size(), "No more reference files should be in the project.");
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testDeleteRefereneFilePermissionDenied() {
		assertThrows(AccessDeniedException.class, () -> {
			referenceFileService.delete(1L);
		});
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testAddReferenceFileWithAmbiguousBases() throws URISyntaxException {
		final ReferenceFile rf = new ReferenceFile();
		final Path ambiguousBasesRefFile = Paths.get(getClass()
				.getResource("/ca/corefacility/bioinformatics/irida/service/testReferenceAmbiguous.fasta").toURI());
		rf.setFile(ambiguousBasesRefFile);
		assertThrows(UnsupportedReferenceFileContentError.class, () -> {
			referenceFileService.create(rf);
		});
	}
}
