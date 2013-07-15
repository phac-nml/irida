package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileProjectJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileSampleJoin;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
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
    private RelationshipRepository relationshipRepository;
    private SequenceFileRepository sequenceFileRepository;
    private Validator validator;

    @Before
    public void setUp() {
        sampleRepository = mock(SampleRepository.class);
        relationshipRepository = mock(RelationshipRepository.class);
        sequenceFileRepository = mock(SequenceFileRepository.class);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        sampleService = new SampleServiceImpl(sampleRepository, relationshipRepository, sequenceFileRepository, validator);
    }

    /*
     * TODO: Reimplement this test
     */
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

        List<SequenceFileProjectJoin> filesForProject = Lists.newArrayList(new SequenceFileProjectJoin(sf, p));

        when(sampleRepository.exists(s.getId())).thenReturn(Boolean.TRUE);
        when(sequenceFileRepository.exists(sf.getId())).thenReturn(Boolean.TRUE);
        when(sequenceFileRepository.addFileToSample(s, sf)).thenReturn(new SequenceFileSampleJoin(sf, s));
        when(sequenceFileRepository.getFilesForProject(p)).thenReturn(filesForProject);

        
        SequenceFileSampleJoin addSequenceFileToSample = sampleService.addSequenceFileToSample(p, s, sf);

        verify(sampleRepository).exists(s.getId());
        verify(sequenceFileRepository).exists(sf.getId());
        verify(sequenceFileRepository).getFilesForProject(p);
        verify(sequenceFileRepository).addFileToSample(s, sf);
        verify(sequenceFileRepository).removeFileFromProject(p, sf);

        assertNotNull(addSequenceFileToSample);
        assertEquals(addSequenceFileToSample.getSubject(), sf);
        assertEquals(addSequenceFileToSample.getObject(), s);
    }
    

    /*
     * TODO: Reimplement this test
    @Test
    public void testRemoveSequenceFileFromSample() {
        Sample s = new Sample();
        s.setIdentifier(new Identifier());
        SequenceFile sf = new SequenceFile();
        sf.setIdentifier(new Identifier());
        Project p = new Project();
        p.setIdentifier(new Identifier());
        Relationship r = new Relationship(p.getIdentifier(), sf.getIdentifier());
        Relationship sampleSequenceFile = new Relationship(s.getIdentifier(), sf.getIdentifier());
        sampleSequenceFile.setIdentifier(new Identifier());
        List<Relationship> relationships = new ArrayList<>();
        relationships.add(sampleSequenceFile);

        when(relationshipRepository.getLinks(p.getIdentifier(), RdfPredicate.ANY, s.getIdentifier()))
                .thenReturn(new ArrayList<Relationship>());
        when(relationshipRepository.getLinks(s.getIdentifier(), RdfPredicate.ANY, sf.getIdentifier()))
                .thenReturn(relationships);
        when(relationshipRepository.create(p, sf)).thenReturn(r);

        Relationship created = sampleService.removeSequenceFileFromSample(p, s, sf);

        verify(relationshipRepository).getLinks(p.getIdentifier(), RdfPredicate.ANY, s.getIdentifier());
        verify(relationshipRepository).getLinks(s.getIdentifier(), RdfPredicate.ANY, sf.getIdentifier());
        verify(relationshipRepository).delete(sampleSequenceFile.getIdentifier());
        verify(relationshipRepository).create(p, sf);

        assertEquals(created, r);
    }
    */ 
}
