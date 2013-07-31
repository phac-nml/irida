package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SampleService;

/**
 * Service class for managing {@link Sample}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImpl extends CRUDServiceImpl<Long, Sample> implements SampleService {
    
    /**
     * Reference to {@link CRUDRepository} for managing {@link Sample}.
     */
    private SampleRepository sampleRepository;
    /**
     * Reference to {@link SequenceFileRepository} for managing {@link SequenceFile}.
     */
    private SequenceFileRepository sequenceFileRepository;
        
    /**
     * Constructor.
     *
     * @param sampleRepository the sample repository.
     * @param validator        validator.
     */
    public SampleServiceImpl(SampleRepository sampleRepository,
                             SequenceFileRepository sequenceFileRepository, Validator validator) {
        super(sampleRepository, validator, Sample.class);
        this.sampleRepository = sampleRepository;
        this.sequenceFileRepository = sequenceFileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleSequenceFileJoin addSequenceFileToSample(Sample sample, SequenceFile sampleFile) {
        // confirm that both the sample and sequence file exist already, fail fast if either don't exist
        if (!sampleRepository.exists(sample.getId())) {
            throw new IllegalArgumentException("Sample must be persisted before adding a sequence file.");
        }

        if (!sequenceFileRepository.exists(sampleFile.getId())) {
            throw new IllegalArgumentException("Sequence file must be persisted before adding to sample.");
        }

        // call the relationship repository to create the relationship between the two entities.
        SampleSequenceFileJoin addFileToSample = sequenceFileRepository.addFileToSample(sample, sampleFile);

        return addFileToSample;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample getSampleForProject(Project project, Long identifier) throws EntityNotFoundException {

        Sample s = null;
        
        // confirm that the link between project and this identifier exists
        List<ProjectSampleJoin> samplesForProject = sampleRepository.getSamplesForProject(project);
        for(ProjectSampleJoin join : samplesForProject){
            if(join.getObject().getId().equals(identifier)){
                // load the sample from the database
                s=read(identifier);
            }
        }
        
        if(s == null){
            throw new EntityNotFoundException("Join between the project and this identifier doesn't exist");
        }

        // return sample to the caller
        return s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSequenceFileFromSample(Sample sample, SequenceFile sequenceFile) {
        sequenceFileRepository.removeFileFromSample(sample, sequenceFile);
    }
    /**
     * {@inheritDoc}
     */
    public List<Join<Project, Sample>> getSamplesForProject(Project p) {
    	return new ArrayList<Join<Project, Sample>>(sampleRepository.getSamplesForProject(p));
    }
}
