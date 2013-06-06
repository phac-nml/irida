package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;

import java.util.Collection;

/**
 * Service for managing {@link SequenceFile} entities.
 */
public interface SequenceFileService extends CRUDService<Identifier, SequenceFile> {

    /**
     * Get a the collection of {@link SequenceFile} that belong to a particular {@link Sample}.
     *
     * @param s the {@link Sample} to get {@link SequenceFile} entities for.
     * @return the collection of {@link SequenceFile} entities for the {@link Sample}.
     */
    public Collection<SequenceFile> getSequenceFilesForSample(Sample s);
}
