package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.service.SampleService;

import javax.validation.Validator;

/**
 * Service class for managing {@link Sample}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImpl extends CRUDServiceImpl<Identifier, Sample> implements SampleService {

    /**
     * Reference to {@link RelationshipRepository}.
     */
    private RelationshipRepository relationshipRepository;
    /**
     * Reference to {@link CRUDRepository} for managing {@link Sample}.
     */
    private CRUDRepository<Identifier, Sample> sampleRepository;

    /**
     * Constructor.
     *
     * @param sampleRepository the sample repository.
     * @param validator        validator.
     */
    public SampleServiceImpl(CRUDRepository<Identifier, Sample> sampleRepository,
                             RelationshipRepository relationshipRepository, Validator validator) {
        super(sampleRepository, validator, Sample.class);
        this.sampleRepository = sampleRepository;
        this.relationshipRepository = relationshipRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship addSequenceFileToSample(Sample sample, SequenceFile sampleFile) {
        throw new UnsupportedOperationException("not implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample getSampleForProject(Project project, Identifier identifier) throws EntityNotFoundException {
        // confirm that the link between project and this identifier exists
        relationshipRepository.getLinks(project.getIdentifier(), RdfPredicate.ANY, identifier);
        // load the sample from the database
        Sample s = sampleRepository.read(identifier);
        // return sample to the caller
        return s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship removeSequenceFileFromSample(Project project, Sample sample, SequenceFile sequenceFile) {
        throw new UnsupportedOperationException("not implemented.");
    }
}
