package ca.corefacility.bioinformatics.irida.service.impl.integration.sample;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/MetadataTemplateServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class MetadataTemplateServiceImplIT {

	@Autowired
	MetadataTemplateService metadataTemplateService;

	@Autowired
	ProjectService projectService;

	@Test
	@WithMockUser(username = "mrtest", roles = "ADMIN")
	public void testGetMetadataHeadersForProject() {
		//check project with 6 fields
		Project project = projectService.read(1L);

		List<MetadataTemplateField> metadataFieldsForProject = metadataTemplateService
				.getMetadataFieldsForProject(project);

		assertEquals(5, metadataFieldsForProject.size());

		Set<String> fields = Sets.newHashSet();
		fields.add("firstName");
		fields.add("lastName");
		fields.add("healthAuthority");
		fields.add("firstSymptom");
		fields.add("serotype");

		for (MetadataTemplateField metadataTemplateField : metadataFieldsForProject) {
			assertTrue("should contain field", fields.contains(metadataTemplateField.getLabel()));

			fields.remove(metadataTemplateField.getLabel());
		}

		assertTrue("should have found all fields", fields.isEmpty());

		//check a project with 1 field
		project = projectService.read(2L);
		metadataFieldsForProject = metadataTemplateService.getMetadataFieldsForProject(project);

		assertEquals(1, metadataFieldsForProject.size());
		MetadataTemplateField field = metadataFieldsForProject.iterator().next();

		assertEquals("firstName", field.getLabel());
	}
}
