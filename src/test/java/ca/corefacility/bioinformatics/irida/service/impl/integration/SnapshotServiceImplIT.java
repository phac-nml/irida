package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.snapshot.ProjectSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.snapshot.Snapshot;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SnapshotService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SnapshotServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SnapshotServiceImplIT {
	@Autowired
	private SnapshotService snapshotService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private SequenceFileService sequenceFileService;

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testTakeSnapshot() {
		Project project = projectService.read(1l);
		Iterable<Sample> samples = sampleService.readMultiple(Lists.newArrayList(2l, 3l));
		SequenceFile file = sequenceFileService.read(4l);

		Snapshot snapshot = snapshotService.takeSnapshot(Lists.newArrayList(project), Lists.newArrayList(samples),
				Lists.newArrayList(file));

		assertNotNull(snapshot);

		ProjectSnapshot projectSnapshot = snapshot.getProjects().iterator().next();
		assertEquals(project.getName(), projectSnapshot.getName());

		assertEquals(2, snapshot.getSamples().size());

		SequenceFileSnapshot fileSnapshot = snapshot.getSequenceFiles().iterator().next();
		assertEquals(file.getFile(), fileSnapshot.getFile());
	}

	// TODO: Add remote testing here
}
