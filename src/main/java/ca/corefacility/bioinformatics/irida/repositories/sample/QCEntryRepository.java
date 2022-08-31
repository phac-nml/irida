package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for saving and retrieving {@link QCEntry} objects
 */
public interface QCEntryRepository extends IridaJpaRepository<QCEntry, Long> {
	/**
	 * find all the {@link QCEntry} associated with {@link SequencingObject}s in a given {@link Sample}
	 * 
	 * @param sample the {@link Sample} to get {@link QCEntry} for
	 * @return a list of {@link QCEntry}
	 */
	@Query("FROM QCEntry e WHERE e.sequencingObject.sample.sample = ?1")
	public List<QCEntry> getQCEntriesForSample(Sample sample);

	/**
	 * finall the {@link QCEntry associated with {@link SequencingObject}s in a given {@link Sample} for a list of {@link Sample}s.
	 * 
	 * @param samples the List of {@link Sample}s to get {@link QCEntry}s for
	 * @return a List of {@link Tuple}s containing the {@link Sample} id and {@link QCEntry}
	 */
	@Query("SELECT e.sequencingObject.sample.sample.id, e FROM QCEntry e WHERE e.sequencingObject.sample.sample in ?1")
	public List<Tuple> getQCEntriesForSamples(List<Sample> samples);
}