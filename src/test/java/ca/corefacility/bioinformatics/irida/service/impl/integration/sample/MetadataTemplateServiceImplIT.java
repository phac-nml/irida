package ca.corefacility.bioinformatics.irida.service.impl.integration.sample;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ServiceIntegrationTest
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
		// check project with 6 fields
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
			assertTrue(fields.contains(metadataTemplateField.getLabel()), "should contain field");

			fields.remove(metadataTemplateField.getLabel());
		}

		assertTrue(fields.isEmpty(), "should have found all fields");

		// check a project with 1 field
		project = projectService.read(2L);
		metadataFieldsForProject = metadataTemplateService.getMetadataFieldsForProject(project);

		assertEquals(1, metadataFieldsForProject.size());
		MetadataTemplateField field = metadataFieldsForProject.iterator().next();

		assertEquals(field.getLabel(), "firstName");
	}
}
