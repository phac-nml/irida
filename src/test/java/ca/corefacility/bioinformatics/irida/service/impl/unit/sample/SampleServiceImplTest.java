package ca.corefacility.bioinformatics.irida.service.impl.unit.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.service.impl.sample.SampleServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Unit tests for {@link SampleServiceImpl}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImplTest {
	
	private SampleService sampleService;
	private SampleRepository sampleRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleSequenceFileJoinRepository ssfRepository;
	private Validator validator;

	@Before
	public void setUp() {
		sampleRepository = mock(SampleRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		ssfRepository = mock(SampleSequenceFileJoinRepository.class);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		sampleService = new SampleServiceImpl(sampleRepository, psjRepository, ssfRepository, validator);
	}

	@Test
	public void testGetSampleForProject() {
		Project p = new Project();
		p.setId(1111l);
		Sample s = new Sample();
		s.setId(2222l);

		ProjectSampleJoin join = new ProjectSampleJoin(p, s);
		List<Join<Project, Sample>> joins = new ArrayList<>();
		joins.add(join);
		when(psjRepository.getSamplesForProject(p)).thenReturn(joins);

		sampleService.getSampleForProject(p, s.getId());

		verify(psjRepository).getSamplesForProject(p);
	}

	@Test
	public void testAddExistingSequenceFileToSample() {
		Sample s = new Sample();
		s.setId(1111l);
		SequenceFile sf = new SequenceFile();
		sf.setId(2222l);

		Project p = new Project();
		p.setId(3333l);
		SampleSequenceFileJoin join = new SampleSequenceFileJoin(s, sf);

		when(sampleRepository.exists(s.getId())).thenReturn(Boolean.TRUE);
		when(ssfRepository.save(join)).thenReturn(join);

		Join<Sample, SequenceFile> addSequenceFileToSample = sampleService.addSequenceFileToSample(s, sf);
		verify(ssfRepository).save(join);

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

		verify(ssfRepository).removeFileFromSample(s, sf);
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
		SampleSequenceFileJoin[] s_sf_joins = new SampleSequenceFileJoin[SIZE];
		ProjectSampleJoin[] p_s_joins = new ProjectSampleJoin[SIZE];
		for (long i = 0; i < SIZE; i++) {
			int p = (int) i;
			toMerge[p] = s(i + 2);
			toMerge_sf[p] = sf(i + 2);
			s_sf_joins[p] = new SampleSequenceFileJoin(s, toMerge_sf[p]);
			p_s_joins[p] = new ProjectSampleJoin(project, toMerge[p]);
			List<Join<Project, Sample>> projectSampleJoins = new ArrayList<>();
			projectSampleJoins.add(p_s_joins[p]);
			List<Join<Sample, SequenceFile>> sampleSequenceFileJoins = new ArrayList<>();
			sampleSequenceFileJoins.add(new SampleSequenceFileJoin(toMerge[p], toMerge_sf[p]));

			when(ssfRepository.getFilesForSample(toMerge[p])).thenReturn(sampleSequenceFileJoins);
			when(ssfRepository.save(s_sf_joins[p])).thenReturn(s_sf_joins[p]);
			when(psjRepository.getProjectForSample(toMerge[p])).thenReturn(projectSampleJoins);
		}
		List<Join<Project, Sample>> joins = new ArrayList<>();
		joins.add(new ProjectSampleJoin(project, s));
		when(psjRepository.getProjectForSample(s)).thenReturn(joins);

		Sample saved = sampleService.mergeSamples(project, s, toMerge);

		verify(psjRepository).getProjectForSample(s);
		for (int i = 0; i < SIZE; i++) {
			verify(ssfRepository).getFilesForSample(toMerge[i]);
			verify(ssfRepository).save(s_sf_joins[i]);
			verify(ssfRepository).removeFileFromSample(toMerge[i], toMerge_sf[i]);
			verify(sampleRepository).delete(toMerge[i].getId());
			verify(psjRepository).getProjectForSample(toMerge[i]);
			verify(psjRepository).removeSampleFromProject(project, toMerge[i]);
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
		p1.setName("project 1");
		Project p2 = new Project();
		p2.setId(2l);
		p2.setName("project 2");

		List<Join<Project, Sample>> p1_s1 = new ArrayList<>();
		p1_s1.add(new ProjectSampleJoin(p1, s1));
		List<Join<Project, Sample>> p2_s2 = new ArrayList<>();
		p2_s2.add(new ProjectSampleJoin(p2, s2));

		when(psjRepository.getProjectForSample(s1)).thenReturn(p1_s1);
		when(psjRepository.getProjectForSample(s2)).thenReturn(p2_s2);

		try {
			sampleService.mergeSamples(p1, s1, s2);
			fail("Samples from different projects were allowed to be merged.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed for an unknown reason; stack trace preceded.");
		}

		verify(psjRepository).getProjectForSample(s1);
		verify(psjRepository).getProjectForSample(s2);
	}
	
	@Test
	public void testSearchProjectSamples(){
		Project project = new Project();
		int page = 0;
		int size = 1;
		Direction order = Direction.ASC;
		String sortProperties = "createdDate";
		Specification<ProjectSampleJoin> specification = ProjectSampleJoinSpecification.searchSampleWithNameInProject("", project);
		
		sampleService.searchProjectSamples(specification, page, size, order, sortProperties);
		
		ArgumentCaptor<PageRequest> pageRequest = ArgumentCaptor.forClass(PageRequest.class);
		verify(psjRepository).findAll(eq(specification), pageRequest.capture());
		
		assertNotNull(pageRequest.getValue().getSort().getOrderFor(sortProperties));
	}
	
	@Test
	public void testSearchProjectSamplesWithoutProperty(){
		Project project = new Project();
		int page = 0;
		int size = 1;
		Direction order = Direction.ASC;
		String sortProperties = "createdDate";
		Specification<ProjectSampleJoin> specification = ProjectSampleJoinSpecification.searchSampleWithNameInProject("", project);
		
		sampleService.searchProjectSamples(specification, page, size, order);
		
		ArgumentCaptor<PageRequest> pageRequest = ArgumentCaptor.forClass(PageRequest.class);
		verify(psjRepository).findAll(eq(specification), pageRequest.capture());
		
		assertNotNull(pageRequest.getValue().getSort().getOrderFor(sortProperties));
	}

	private Sample s(Long id) {
		Sample s = new Sample();
		s.setId(id);
		return s;
	}

	private SequenceFile sf(Long id) {
		SequenceFile sf = new SequenceFile();
		sf.setId(id);
		try {
			sf.setFile(Files.createTempFile(null, null));
		} catch (IOException e) {
			
		}
		return sf;
	}

	private Project p(Long id) {
		Project p = new Project();
		p.setId(id);
		return p;
	}
}
