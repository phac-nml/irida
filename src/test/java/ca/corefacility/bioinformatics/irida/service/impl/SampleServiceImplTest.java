package ca.corefacility.bioinformatics.irida.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SampleService;

import com.google.common.collect.Lists;

/**
 * Unit tests for {@link SampleServiceImpl}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImplTest {

	private SampleService sampleService;
	private SampleRepository sampleRepository;
	private SequenceFileRepository sequenceFileRepository;
	private ProjectRepository projectRepository;
	private Validator validator;

	@Before
	public void setUp() {
		sampleRepository = mock(SampleRepository.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		projectRepository = mock(ProjectRepository.class);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		sampleService = new SampleServiceImpl(sampleRepository, sequenceFileRepository, projectRepository, validator);
	}

	@Test
	public void testGetSampleForProject() {
		Project p = new Project();
		p.setId(1111l);
		Sample s = new Sample();
		s.setId(2222l);

		ProjectSampleJoin join = new ProjectSampleJoin(p, s);
		when(sampleRepository.getSamplesForProject(p)).thenReturn(Lists.newArrayList(join));
		when(sampleRepository.findOne(s.getId())).thenReturn(s);

		sampleService.getSampleForProject(p, s.getId());

		verify(sampleRepository).getSamplesForProject(p);
		verify(sampleRepository).findOne(s.getId());
	}

	@Test
	public void testAddExistingSequenceFileToSample() {
		Sample s = new Sample();
		s.setId(1111l);
		SequenceFile sf = new SequenceFile();
		sf.setId(2222l);

		Project p = new Project();
		p.setId(3333l);
		// Relationship projectSequenceFile = new
		// Relationship(p.getIdentifier(), sf.getIdentifier());

		when(sampleRepository.exists(s.getId())).thenReturn(Boolean.TRUE);
		when(sequenceFileRepository.exists(sf.getId())).thenReturn(Boolean.TRUE);
		when(sequenceFileRepository.addFileToSample(s, sf)).thenReturn(new SampleSequenceFileJoin(s, sf));

		Join<Sample, SequenceFile> addSequenceFileToSample = sampleService.addSequenceFileToSample(s, sf);
		verify(sequenceFileRepository).addFileToSample(s, sf);

		assertNotNull(addSequenceFileToSample);
		assertEquals(addSequenceFileToSample.getSubject(), s);
		assertEquals(addSequenceFileToSample.getObject(), sf);
	}

	@Test
	public void testRemoveSequenceFileFromSample() {
		Sample s = new Sample();
		s.setId(1111l);
		SequenceFile sf = new SequenceFile();
		sf.setId(2222l);

		sampleService.removeSequenceFileFromSample(s, sf);

		verify(sequenceFileRepository).removeFileFromSample(s, sf);
	}

	@Test
	public void testMergeSamples() {
		// For every sample in toMerge, the service should:
		// 1. call SequenceFileRepository to get the sequence files in that
		// sample,
		// 2. call SequenceFileRepository to add the sequence files to
		// mergeInto,
		// 3. call SampleRepository to persist the sample as deleted.

		final int SIZE = 3;

		Sample s = s(1l);
		Project project = p(1l);

		Sample[] toMerge = new Sample[SIZE];
		SequenceFile[] toMerge_sf = new SequenceFile[SIZE];
		SampleSequenceFileJoin[] joins = new SampleSequenceFileJoin[SIZE];
		ProjectSampleJoin[] p_s_joins = new ProjectSampleJoin[SIZE];
		for (long i = 0; i < SIZE; i++) {
			int p = (int) i;
			toMerge[p] = s(i + 2);
			toMerge_sf[p] = sf(i + 2);
			joins[p] = new SampleSequenceFileJoin(toMerge[p], toMerge_sf[p]);
			p_s_joins[p] = new ProjectSampleJoin(project, toMerge[p]);

			when(sequenceFileRepository.getFilesForSample(toMerge[p])).thenReturn(Lists.newArrayList(joins[p]));
			when(sequenceFileRepository.addFileToSample(s, toMerge_sf[p])).thenReturn(joins[p]);
			when(projectRepository.getProjectForSample(toMerge[p])).thenReturn(Lists.newArrayList(p_s_joins[p]));
		}
		when(projectRepository.getProjectForSample(s))
				.thenReturn(Lists.newArrayList(new ProjectSampleJoin(project, s)));

		Sample saved = sampleService.mergeSamples(project, s, toMerge);

		verify(projectRepository).getProjectForSample(s);
		for (int i = 0; i < SIZE; i++) {
			verify(sequenceFileRepository).getFilesForSample(toMerge[i]);
			verify(sequenceFileRepository).addFileToSample(s, toMerge_sf[i]);
			verify(sequenceFileRepository).removeFileFromSample(toMerge[i], toMerge_sf[i]);
			verify(sampleRepository).delete(toMerge[i].getId());
			verify(projectRepository).getProjectForSample(toMerge[i]);
		}
		assertEquals("The saved sample should be the same as the sample to merge into.", s, saved);
	}

	@Test
	public void testRejectSampleMergeDifferentProjects() {
		Sample s1 = new Sample();
		s1.setId(1l);
		Sample s2 = new Sample();
		s2.setId(2l);
		Project p1 = new Project();
		p1.setId(1l);
		Project p2 = new Project();
		p2.setId(2l);

		when(projectRepository.getProjectForSample(s1)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p1, s1)));
		when(projectRepository.getProjectForSample(s2)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p2, s2)));

		try {
			sampleService.mergeSamples(p1, s1, s2);
			fail("Samples from different projects were allowed to be merged.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed for an unknown reason; stack trace preceded.");
		}

		verify(projectRepository).getProjectForSample(s1);
		verify(projectRepository).getProjectForSample(s2);
	}

	private Sample s(Long id) {
		Sample s = new Sample();
		s.setId(id);
		return s;
	}

	private SequenceFile sf(Long id) {
		SequenceFile sf = new SequenceFile();
		sf.setId(id);
		return sf;
	}

	private Project p(Long id) {
		Project p = new Project();
		p.setId(id);
		return p;
	}
}
