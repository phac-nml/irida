package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;

/**
 * Service for managing {@link SequenceFile} entities.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SequenceFileService extends CRUDService<Long, SequenceFile> {
    /**
     * Persist the {@link SequenceFile} to the database and create a new relationship between the {@link SequenceFile}
     * and a {@link Sample}
     *
     * @param sequenceFile the {@link SequenceFile} to be persisted.
     * @param sample The sample to add the file to
     * @return the {@link Join} between the {@link SequenceFile} and its {@link Sample}.
     */
    public Join<Sample, SequenceFile> createSequenceFileInSample(SequenceFile sequenceFile, Sample sample);

    /**
     * Get a {@link SequenceFile} that is associated with a specific {@Link Project}. If the {@link SequenceFile} is
     * not associated with the {@link Project}, or no {@link SequenceFile} with the specified {@link Identifier} exists,
     * then an {@link EntityNotFoundException} will be thrown.
     * @deprecated 
     *
     * @param project        the {@link Project} to get the {@link SequenceFile} from.
     * @param sequenceFileId the {@link Identifier} of the {@link SequenceFile}.
     * @return the {@link SequenceFile} belonging to the {@link Identifier}.
     * @throws EntityNotFoundException when the {@link Identifier} specified does not exist, or is not associated with
     *                                 the supplied {@link Project}.
     */
    public SequenceFile getSequenceFileFromProject(Project project, Long sequenceFileId) throws EntityNotFoundException;

    /**
     * Get a {@link SequenceFile} that is associated with a specific {@link Sample}. If the {@link SequenceFile} is not
     * associated with the {@link Sample}, or no {@link SequenceFile} with the specified {@link Identifier} exists, then
     * an {@link EntityNotFoundException} will be thrown.
     * @deprecated 
     *
     * @param project        the {@link Project} to get the {@link Sample} from.
     * @param sample         the {@link Sample} to get the {@link SequenceFile} from.
     * @param sequenceFileId the {@link Identifier} of the {@link SequenceFile}.
     * @return the {@link SequenceFile} belonging to the {@link Identifier}.
     * @throws EntityNotFoundException when the {@link Identifier} specified does not exist, or is not associated with
     *                                 the supplied {@link Sample}.
     */
    public SequenceFile getSequenceFileFromSample(Project project, Sample sample, Long sequenceFileId) throws EntityNotFoundException;
    
    /**
     * Get a {@link List} of {@link SequenceFile} references for a specific {@link Sample}.
     * 
     * @param sample the {@link Sample} to get the {@link SequenceFile} references from.
     * @return the references to {@link SequenceFile}.
     */
    public List<Join<Sample, SequenceFile>> getSequenceFilesForSample(Sample sample);
}
