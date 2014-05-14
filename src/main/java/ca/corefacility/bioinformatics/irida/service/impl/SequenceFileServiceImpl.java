package ca.corefacility.bioinformatics.irida.service.impl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.processing.annotations.ModifiesSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Implementation for managing {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Service
public class SequenceFileServiceImpl extends CRUDServiceImpl<Long, SequenceFile> implements SequenceFileService {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImpl.class);

	private static final String FILE_PROPERTY = "file";

	/**
	 * A reference to the file system repository.
	 */
	private SequenceFileFilesystem fileRepository;
	/**
	 * Reference to {@link SampleSequenceFileJoinRepository}.
	 */
	private SampleSequenceFileJoinRepository ssfRepository;

	/**
	 * Reference to {@link MiseqRunRepository}
	 */
	private MiseqRunRepository miseqRunRepository;

	private OverrepresentedSequenceRepository overrepresentedSequenceRepository;

	protected SequenceFileServiceImpl() {
		super(null, null, SequenceFile.class);
	}

	/**
	 * Constructor.
	 * 
	 * @param sequenceFileRepository
	 *            the sequence file repository.
	 * @param validator
	 *            validator.
	 */
	@Autowired
	public SequenceFileServiceImpl(SequenceFileRepository sequenceFileRepository,
			SequenceFileFilesystem fileRepository, SampleSequenceFileJoinRepository ssfRepository,
			OverrepresentedSequenceRepository overrepresentedSequenceRepository, MiseqRunRepository miseqRunRepository,
			Validator validator) {
		super(sequenceFileRepository, validator, SequenceFile.class);
		this.fileRepository = fileRepository;
		this.ssfRepository = ssfRepository;
		this.miseqRunRepository = miseqRunRepository;
		this.overrepresentedSequenceRepository = overrepresentedSequenceRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@ModifiesSequenceFile
	public SequenceFile create(SequenceFile sequenceFile) {
		// Send the file to the database repository to be stored (in super)
		logger.trace("Calling super.create");
		sequenceFile = super.create(sequenceFile);
		// Then store the file in an appropriate directory
		logger.trace("About to write file to disk.");
		sequenceFile = fileRepository.writeSequenceFileToDisk(sequenceFile);
		// And finally, update the database with the stored file location

		Map<String, Object> changed = new HashMap<>();
		changed.put(FILE_PROPERTY, sequenceFile.getFile());
		logger.trace("Calling this.update");
		final SequenceFile updatedSequenceFile = super.update(sequenceFile.getId(), changed);
		return updatedSequenceFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public SequenceFile updateWithoutProcessors(Long id, Map<String, Object> updatedFields) {
		return update(id, updatedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@ModifiesSequenceFile
	public SequenceFile update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException {
		if (updatedFields.containsKey("fileRevisionNumber")) {
			throw new InvalidPropertyException("File revision number cannot be updated manually.", SequenceFile.class);
		}

		ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

		SequenceFile toUpdate = read(id);

		if (updatedFields.containsKey(FILE_PROPERTY)) {
			logger.trace("Sequence file [" + toUpdate.getId() + "] has file location to be updated.");
			Path fileLocation = (Path) updatedFields.get(FILE_PROPERTY);
			Long updatedRevision = toUpdate.getFileRevisionNumber() + 1;
			// write the file to a new location on disk
			Path updatedLocation = fileRepository.updateSequenceFileOnDisk(id, fileLocation, updatedRevision);
			// put the new location into the map to be constructed
			builder.put(FILE_PROPERTY, (Object) updatedLocation);
			builder.put("fileRevisionNumber", (Object) updatedRevision);
			// add all keys from the updatedFields map that are NOT equal to
			// "file"
			builder.putAll(Maps.filterKeys(updatedFields, input -> !input.equals(FILE_PROPERTY)));
		} else {
			// the file isn't to be updated, so just keep all the keys that were
			// originally supplied to the method.
			builder.putAll(updatedFields);
		}

		logger.trace("Calling super.update");
		SequenceFile updated = super.update(id, builder.build());
		logger.trace("Finished calling super.update");
		return updated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@ModifiesSequenceFile
	public Join<Sample, SequenceFile> createSequenceFileInSample(SequenceFile sequenceFile, Sample sample) {
		SequenceFile created = create(sequenceFile);
		SampleSequenceFileJoin join = new SampleSequenceFileJoin(sample, created);
		return ssfRepository.save(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Join<Sample, SequenceFile>> getSequenceFilesForSample(Sample sample) {
		return ssfRepository.getFilesForSample(sample);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<SequenceFile> getSequenceFilesForMiseqRun(MiseqRun miseqRun) {
		MiseqRun loaded = miseqRunRepository.findOne(miseqRun.getId());
		// force hibernate to eager load the collection
		loaded.getSequenceFiles().forEach(f -> f.getId());
		return loaded.getSequenceFiles();
	}

	@Override
	@Transactional
	public void addOverrepresentedSequenceToSequenceFile(SequenceFile sequenceFile, OverrepresentedSequence sequence) {
		sequence.setSequenceFile(sequenceFile);
		overrepresentedSequenceRepository.save(sequence);
	}
}
