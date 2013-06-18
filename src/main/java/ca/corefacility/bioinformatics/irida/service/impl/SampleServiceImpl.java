package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.service.SampleService;

import javax.validation.Validator;
import java.util.List;

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
     * Reference to {@link SequenceFileRepository} for managing {@link SequenceFile}.
     */
    private SequenceFileRepository sequenceFileRepository;

    /**
     * Constructor.
     *
     * @param sampleRepository the sample repository.
     * @param validator        validator.
     */
    public SampleServiceImpl(CRUDRepository<Identifier, Sample> sampleRepository,
                             RelationshipRepository relationshipRepository,
                             SequenceFileRepository sequenceFileRepository, Validator validator) {
        super(sampleRepository, validator, Sample.class);
        this.sampleRepository = sampleRepository;
        this.relationshipRepository = relationshipRepository;
        this.sequenceFileRepository = sequenceFileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship addSequenceFileToSample(Project project, Sample sample, SequenceFile sampleFile) {
        // confirm that both the sample and sequence file exist already, fail fast if either don't exist
        if (!sampleRepository.exists(sample.getIdentifier())) {
            throw new IllegalArgumentException("Sample must be persisted before adding a sequence file.");
        }

        if (!sequenceFileRepository.exists(sampleFile.getIdentifier())) {
            throw new IllegalArgumentException("Sequence file must be persisted before adding to sample.");
        }

        // get the existing relationship between the project and sequence file
        List<Relationship> projectSequenceFileRelationships = relationshipRepository.getLinks(project.getIdentifier(),
                RdfPredicate.ANY, sampleFile.getIdentifier());
        if (projectSequenceFileRelationships.size() != 1) {
            throw new IllegalArgumentException("Project and SequenceFile must be related.");
        }
        Relationship projectSequenceFileRelationship = projectSequenceFileRelationships.iterator().next();
        // remove the existing relationship
        relationshipRepository.delete(projectSequenceFileRelationship.getIdentifier());

        Relationship r = relationshipRepository.create(Sample.class, sample.getIdentifier(),
                SequenceFile.class, sampleFile.getIdentifier());

        // call the relationship repository to create the relationship between the two entities.
        return r;
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
        // confirm that project and sample have a relationship
        relationshipRepository.getLinks(project.getIdentifier(), RdfPredicate.ANY, sample.getIdentifier());

        // confirm that sample and sequence file have a relationship
        List<Relationship> relationships = relationshipRepository.getLinks(sample.getIdentifier(), RdfPredicate.ANY,
                sequenceFile.getIdentifier());
        if (relationships.size() > 1) {
            throw new IllegalStateException("More than one type of relationship between sample [" +
                    sample.getIdentifier() + "] and sequenceFile [" + sequenceFile.getIdentifier() + "]");
        }
        Relationship r = relationships.iterator().next();

        // delete the relationship between sample and sequence file
        relationshipRepository.delete(r.getIdentifier());

        // add a new relationship between project and sequence file
        Relationship created = relationshipRepository.create(project, sequenceFile);

        // return the relationship
        return created;
    }
}
