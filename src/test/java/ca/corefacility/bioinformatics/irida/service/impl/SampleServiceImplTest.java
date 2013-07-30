package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SampleService;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SampleServiceImpl}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImplTest {

    private SampleService sampleService;
    private SampleRepository sampleRepository;
    private SequenceFileRepository sequenceFileRepository;
    private Validator validator;

    @Before
    public void setUp() {
        sampleRepository = mock(SampleRepository.class);
        sequenceFileRepository = mock(SequenceFileRepository.class);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        sampleService = new SampleServiceImpl(sampleRepository, sequenceFileRepository, validator);
    }

    @Test
    public void testGetSampleForProject() {
        Project p = new Project();
        p.setId(new Long(1111));
        Sample s = new Sample();
        s.setId(new Long(2222));
        
        ProjectSampleJoin join = new ProjectSampleJoin(p, s);
        when(sampleRepository.getSamplesForProject(p)).thenReturn(Lists.newArrayList(join));
        when(sampleRepository.read(s.getId())).thenReturn(s);

        sampleService.getSampleForProject(p, s.getId());

        verify(sampleRepository).getSamplesForProject(p);
        verify(sampleRepository).read(s.getId());
    }

    @Test
    public void testAddExistingSequenceFileToSample() {
        Sample s = new Sample();
        s.setId(new Long(1111));
        SequenceFile sf = new SequenceFile();
        sf.setId(new Long(2222));
        
        Project p = new Project();
        p.setId(new Long(3333));
        //Relationship projectSequenceFile = new Relationship(p.getIdentifier(), sf.getIdentifier());

        when(sampleRepository.exists(s.getId())).thenReturn(Boolean.TRUE);
        when(sequenceFileRepository.exists(sf.getId())).thenReturn(Boolean.TRUE);
        when(sequenceFileRepository.addFileToSample(s, sf)).thenReturn(new SampleSequenceFileJoin(s, sf));

        Join<Sample, SequenceFile> addSequenceFileToSample = sampleService.addSequenceFileToSample(s, sf);

        verify(sampleRepository).exists(s.getId());
        verify(sequenceFileRepository).exists(sf.getId());
        verify(sequenceFileRepository).addFileToSample(s, sf);

        assertNotNull(addSequenceFileToSample);
        assertEquals(addSequenceFileToSample.getSubject(), s);
        assertEquals(addSequenceFileToSample.getObject(), sf);
    }
    

    @Test
    public void testRemoveSequenceFileFromSample() {
        Sample s = new Sample();
        s.setId(new Long(1111));
        SequenceFile sf = new SequenceFile();
        sf.setId(new Long(2222));

        sampleService.removeSequenceFileFromSample(s, sf);
        
        verify(sequenceFileRepository).removeFileFromSample(s, sf);
    }
     
}
