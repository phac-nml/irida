package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectHashingService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ProjectHashingServiceTest {
	@Mock
	private SampleService sampleService;
	@Mock
	private SequencingObjectService objectService;
	@Mock
	private GenomeAssemblyService assemblyService;

	ProjectHashingService service;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		service = new ProjectHashingService(sampleService, objectService, assemblyService);
	}

	@Test
	public void testLoneProject() {
		Project p = new Project("test");
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(p);

		Integer projectHash = service.getProjectHash(p);

		assertEquals((Integer) builder.toHashCode(), projectHash);
	}

	@Test
	public void testProjectWithSample() {
		Project p = new Project("test");
		Sample s = new Sample("sample");

		when(sampleService.getSamplesForProject(p)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s, true)));

		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(p);
		builder.append(s);

		Integer projectHash = service.getProjectHash(p);

		assertEquals((Integer) builder.toHashCode(), projectHash);
	}

	@Test
	public void testAddSample() {
		Project p = new Project("test");
		Sample s = new Sample("sample");

		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(p);

		Integer projectHash = service.getProjectHash(p);

		assertEquals((Integer) builder.toHashCode(), projectHash);

		when(sampleService.getSamplesForProject(p)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s, true)));

		builder.append(s);
		projectHash = service.getProjectHash(p);

		assertEquals((Integer) builder.toHashCode(), projectHash);
	}

	@Test
	public void testProjectWithSequenceFile() {
		Project p = new Project("test");
		Sample s = new Sample("sample");
		SingleEndSequenceFile file = new SingleEndSequenceFile(new SequenceFile());

		when(sampleService.getSamplesForProject(p)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s, true)));
		when(objectService.getSequencingObjectsForSample(s))
				.thenReturn(Lists.newArrayList(new SampleSequencingObjectJoin(s, file)));

		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(p);
		builder.append(s);
		builder.append(file);

		Integer projectHash = service.getProjectHash(p);

		assertEquals((Integer) builder.toHashCode(), projectHash);
	}

	@Test
	public void testProjectAddFile() {
		Project p = new Project("test");
		Sample s = new Sample("sample");
		SingleEndSequenceFile file = new SingleEndSequenceFile(new SequenceFile());

		when(sampleService.getSamplesForProject(p)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s, true)));

		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(p);
		builder.append(s);

		Integer projectHash = service.getProjectHash(p);

		assertEquals((Integer) builder.toHashCode(), projectHash);

		when(objectService.getSequencingObjectsForSample(s))
				.thenReturn(Lists.newArrayList(new SampleSequencingObjectJoin(s, file)));

		builder.append(file);
		projectHash = service.getProjectHash(p);

		assertEquals((Integer) builder.toHashCode(), projectHash);
	}

}
