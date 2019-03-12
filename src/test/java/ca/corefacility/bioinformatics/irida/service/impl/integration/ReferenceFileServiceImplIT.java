package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
		assertEquals("Wrong number of reference files for project.", 1, prs.size());
		ReferenceFile rf = prs.iterator().next().getObject();
		assertEquals("Wrong reference file attached to project.", Long.valueOf(1), rf.getId());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", roles = "USER")
	public void testReadNotAllowed() {
		referenceFileService.read(1L);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testRead() {
		ReferenceFile read = referenceFileService.read(1L);
		assertNotNull(read);
	}
	
	@Test
	@WithMockUser(username = "fbristow", roles= "USER")
	public void testDeleteReferenceFile() {
		Project p = projectService.read(1L);
		List<Join<Project, ReferenceFile>> prs = referenceFileService.getReferenceFilesForProject(p);
		assertEquals("Wrong number of reference files for project.", 1, prs.size());
		ReferenceFile rf = prs.iterator().next().getObject();
		assertEquals("Wrong reference file attached to project.", Long.valueOf(1), rf.getId());
		
		referenceFileService.delete(rf.getId());
		p = projectService.read(1L);
		prs = referenceFileService.getReferenceFilesForProject(p);
		assertEquals("No more reference files should be in the project.", 0, prs.size());
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", roles = "USER")
	public void testDeleteRefereneFilePermissionDenied() {
		referenceFileService.delete(1L);
	}
	
	@Test(expected = UnsupportedReferenceFileContentError.class)
	@WithMockUser(username = "user", roles = "USER")
	public void testAddReferenceFileWithAmbiguousBases() throws URISyntaxException {
		final ReferenceFile rf = new ReferenceFile();
		final Path ambiguousBasesRefFile = Paths.get(getClass().getResource(
				"/ca/corefacility/bioinformatics/irida/service/testReferenceAmbiguous.fasta").toURI());
		rf.setFile(ambiguousBasesRefFile);
		referenceFileService.create(rf);
	}
}
