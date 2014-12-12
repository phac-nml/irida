package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.library.Layout;
import ca.corefacility.bioinformatics.irida.model.project.library.Layout.LayoutType;
import ca.corefacility.bioinformatics.irida.model.project.library.LibraryDescription;
import ca.corefacility.bioinformatics.irida.model.project.library.LibraryDescription.Source;
import ca.corefacility.bioinformatics.irida.model.project.library.Strategy;
import ca.corefacility.bioinformatics.irida.service.LibraryDescriptionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/LibraryDescriptionServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class LibraryDescriptionServiceImplIT {
	@Autowired
	public LibraryDescriptionService libraryDescriptionService;
	@Autowired
	public ProjectService projectService;

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "manager", roles = "MANAGER")
	public void testCreateLibraryDescriptionNotAllowed() {
		createLibraryDescription();
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateLibraryDescriptionAsAdmin() {
		final LibraryDescription ld = createLibraryDescription();
		assertNotNull("Should have created the library description with a new id.", ld.getId());
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testCreateLibraryDescriptionAsProjectOwner() {
		final LibraryDescription ld = createLibraryDescription();
		assertNotNull("Should have created the library description with a new id.", ld.getId());
	}

	private LibraryDescription createLibraryDescription() {
		final Project p = projectService.read(1L);
		final Strategy s = new Strategy(1, 1, 1, "protocol");
		final Layout l = new Layout(1, LayoutType.PAIRED_END);
		final LibraryDescription ld = new LibraryDescription(p, Source.AMPLICON, s, l);
		ld.setComment("This is a comment.");

		return libraryDescriptionService.create(ld);
	}
}
