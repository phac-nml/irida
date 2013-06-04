package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.SampleService;

import javax.validation.Validator;

/**
 * Service class for managing {@link Sample}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImpl extends CRUDServiceImpl<Identifier, Sample> implements SampleService {

    /**
     * Constructor.
     *
     * @param sampleRepository the sample repository.
     * @param validator        validator.
     */
    public SampleServiceImpl(
            CRUDRepository<Identifier, Sample> sampleRepository,
            Validator validator) {
        super(sampleRepository, validator, Sample.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSampleFileToSample(Sample sample, SequenceFile sampleFile) {
        throw new UnsupportedOperationException("not implemented.");
    }
}
