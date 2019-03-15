package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.IridaWebTestScopeConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.web.IridaUIWebConfig;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigWebContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class, IridaApiServicesConfig.class,
		IridaUIWebConfig.class, IridaWebTestScopeConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@ActiveProfiles("it")
@WebAppConfiguration
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/CartControllerIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class CartControllerIT {

	@Autowired
	CartController controller;

	@Autowired
	ProjectService projectService;

	@Autowired
	SampleService sampleService;

	@Before
	public void setUp() {
		controller.addProject(1L);
	}

	@After
	public void teardown() {
		controller.clearCart();
	}

	@Test
	@WithMockUser(username = "mrtest", roles = "ADMIN")
	public void testAddProjectSample() {
		Long projectId = 2L;
		Project project = projectService.read(projectId);
		Set<Long> sampleIds = Sets.newHashSet(4L);
		Map<String, Object> addProjectSample = controller.addProjectSample(projectId, sampleIds, Locale.US);
		assertEquals("Should be 1 sample in the cart", "1 sample was added to the cart from project2.",  addProjectSample.get("message"));

		Set<Sample> selectedSamplesForProject = controller.getSelected().get(project);
		for (Sample s : selectedSamplesForProject) {
			assertTrue(sampleIds.contains(s.getId()));
		}
	}

	@Test
	@WithMockUser(username = "mrtest", roles = "ADMIN")
	public void testAddProject() {
		Long projectId = 2L;
		Project project = projectService.read(projectId);
		Map<String, Object> addProjectSample = controller.addProject(projectId);
		assertTrue((boolean) addProjectSample.get("success"));

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);

		Set<Sample> selectedSamplesForProject = controller.getSelected().get(project);
		for (Join<Project, Sample> j : samplesForProject) {
			assertTrue(selectedSamplesForProject.contains(j.getObject()));
		}
	}

	@Test
	@WithMockUser(username = "mrtest", roles = "ADMIN")
	public void testClearCart(){
		Map<String, Object> clearCart = controller.clearCart();
		assertTrue((boolean) clearCart.get("success"));
	}
}
