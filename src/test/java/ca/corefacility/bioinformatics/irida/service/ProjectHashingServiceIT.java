package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectHashingService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/ProjectServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectHashingServiceIT {

	@Autowired
	ProjectService projectService;
	@Autowired
	SampleService sampleService;
	@Autowired
	SequencingObjectService sequencingObjectService;
	@Autowired
	MetadataTemplateService metadataTemplateService;

	@Autowired
	ProjectHashingService hashingService;

	@WithMockUser(username = "admin", roles = "ADMIN")
	@Test
	public void canGenerateHash() {
		Project project = projectService.read(2L);
		Integer expectedHash = 1654242358;

		Integer projectHash = hashingService.getProjectHash(project);

		assertEquals(expectedHash, projectHash);
	}

	@WithMockUser(username = "admin", roles = "ADMIN")
	@Test
	public void hashChangesWithNewSample() {
		Project project = projectService.read(2L);

		Integer originalHash = hashingService.getProjectHash(project);

		Sample sample = new Sample("testSample");
		projectService.addSampleToProject(project, sample, true);

		Integer newHash = hashingService.getProjectHash(project);

		assertNotEquals(originalHash, newHash);
	}

	@WithMockUser(username = "admin", roles = "ADMIN")
	@Test
	public void hashChangesWithRemovedSample() {
		Project project = projectService.read(2L);

		Integer originalHash = hashingService.getProjectHash(project);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		Sample sample = samplesForProject.iterator()
				.next()
				.getObject();

		projectService.removeSampleFromProject(project, sample);

		Integer newHash = hashingService.getProjectHash(project);

		assertNotEquals(originalHash, newHash);
	}

	@WithMockUser(username = "admin", roles = "ADMIN")
	@Test
	public void hashChangesWithRemovedSequencingObject() {
		Project project = projectService.read(2L);

		Integer originalHash = hashingService.getProjectHash(project);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		Sample sample = samplesForProject.iterator()
				.next()
				.getObject();

		Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = sequencingObjectService.getSequencingObjectsForSample(
				sample);
		SequencingObject sequencingObject = sequencingObjectsForSample.iterator()
				.next()
				.getObject();

		sampleService.removeSequencingObjectFromSample(sample, sequencingObject);

		Integer newHash = hashingService.getProjectHash(project);

		assertNotEquals(originalHash, newHash);
	}

	@WithMockUser(username = "admin", roles = "ADMIN")
	@Test
	public void hashChangesWithMetadataSequencingObject() {
		Project project = projectService.read(2L);

		Integer originalHash = hashingService.getProjectHash(project);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);
		Sample sample = samplesForProject.iterator()
				.next()
				.getObject();

		MetadataTemplateField field = new MetadataTemplateField("test", "text");
		field = metadataTemplateService.saveMetadataField(field);
		MetadataEntry entry = new MetadataEntry("value", "text", field);

		HashSet<MetadataEntry> metadataEntries = Sets.newHashSet(entry);

		sampleService.mergeSampleMetadata(sample, metadataEntries);

		Integer newHash = hashingService.getProjectHash(project);

		assertNotEquals(originalHash, newHash);
	}

}
