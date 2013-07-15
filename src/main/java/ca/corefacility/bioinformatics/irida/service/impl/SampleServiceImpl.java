package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileProjectJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileSampleJoin;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.ProjectRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.service.SampleService;

import javax.validation.Validator;
import java.util.List;

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
    public SequenceFileSampleJoin addSequenceFileToSample(Project project, Sample sample, SequenceFile sampleFile) {
        // confirm that both the sample and sequence file exist already, fail fast if either don't exist
        if (!sampleRepository.exists(sample.getId())) {
            throw new IllegalArgumentException("Sample must be persisted before adding a sequence file.");
        }

        if (!sequenceFileRepository.exists(sampleFile.getId())) {
            throw new IllegalArgumentException("Sequence file must be persisted before adding to sample.");
        }
        
        // get the existing relationship between the project and sequence file
        boolean projectHasFile = false;
        List<SequenceFileProjectJoin> filesForProject = sequenceFileRepository.getFilesForProject(project);
        for(SequenceFileProjectJoin join : filesForProject){
            if(join.getSubject().equals(sampleFile)){
                projectHasFile = true;
            }
        }
        
        // remove the existing relationship
        if(projectHasFile){
            sequenceFileRepository.removeFileFromProject(project, sampleFile);
        }
        // call the relationship repository to create the relationship between the two entities.

        SequenceFileSampleJoin addFileToSample = sequenceFileRepository.addFileToSample(sample, sampleFile);

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
    public SequenceFileProjectJoin removeSequenceFileFromSample(Project project, Sample sample, SequenceFile sequenceFile) {
        // get the existing relationship between the sample and sequence file
        boolean sampleHasFile = false;
        List<SequenceFileSampleJoin> filesForSample = sequenceFileRepository.getFilesForSample(sample);
        for(SequenceFileSampleJoin join : filesForSample){
            if(join.getSubject().equals(sequenceFile)){
                sampleHasFile = true;
            }
        }

        // remove the existing relationship
        if(sampleHasFile){
            sequenceFileRepository.removeFileFromSample(sample, sequenceFile);
        }
        else{
            throw new EntityNotFoundException("This file is not associated with this project");
        }
        // call the relationship repository to create the relationship between the two entities.        
        // confirm that project and sample have a relationship
        // add a new relationship between project and sequence file
        SequenceFileProjectJoin created = sequenceFileRepository.addFileToProject(project, sequenceFile);

        // return the relationship
        return created;
    }
            
}
