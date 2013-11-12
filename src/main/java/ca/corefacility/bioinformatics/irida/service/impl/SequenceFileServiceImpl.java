package ca.corefacility.bioinformatics.irida.service.impl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileOverrepresentedSequenceJoin;
import ca.corefacility.bioinformatics.irida.processing.annotations.EnablePostProcessing;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.MiseqRunSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.SequenceFileOverrepresentedSequenceJoinRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Implementation for managing {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
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
	 * Reference to {@link SequenceFileOverrepresentedSequenceJoinRepository}.
	 */
	private SequenceFileOverrepresentedSequenceJoinRepository sfosRepository;
	/**
	 * Reference to {@link MiseqRunSequenceFileJoinRepository}.
	 */
	private MiseqRunSequenceFileJoinRepository mrsfRepository;

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
	public SequenceFileServiceImpl(SequenceFileRepository sequenceFileRepository,
			SequenceFileFilesystem fileRepository, SampleSequenceFileJoinRepository ssfRepository,
			SequenceFileOverrepresentedSequenceJoinRepository sfosRepository,
			MiseqRunSequenceFileJoinRepository mrsfRepository, Validator validator) {
		super(sequenceFileRepository, validator, SequenceFile.class);
		this.fileRepository = fileRepository;
		this.ssfRepository = ssfRepository;
		this.sfosRepository = sfosRepository;
		this.mrsfRepository = mrsfRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
	@Transactional
	@EnablePostProcessing
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
		final SequenceFile updatedSequenceFile = update(sequenceFile.getId(), changed);
		return updatedSequenceFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadSequenceFile')")
	@EnablePostProcessing
	public SequenceFile update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException {
		if (updatedFields.containsKey("fileRevisionNumber")) {
			throw new InvalidPropertyException("File revision number cannot be updated manually.");
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
			builder.putAll(Maps.filterKeys(updatedFields, new Predicate<String>() {
				@Override
				public boolean apply(String input) {
					return !input.equals(FILE_PROPERTY);
				}
			}));
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
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT') or hasPermission(#sample, 'canReadSample')")
	@EnablePostProcessing
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<Join<Sample, SequenceFile>> getSequenceFilesForSample(Sample sample) {
		return ssfRepository.getFilesForSample(sample);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Join<MiseqRun, SequenceFile>> getSequenceFilesForMiseqRun(MiseqRun miseqRun) {
		return mrsfRepository.getFilesForMiseqRun(miseqRun);
	}

	@Override
	@Transactional(readOnly = true)
	public Join<SequenceFile, OverrepresentedSequence> addOverrepresentedSequenceToSequenceFile(
			SequenceFile sequenceFile, OverrepresentedSequence sequence) {
		return sfosRepository.save(new SequenceFileOverrepresentedSequenceJoin(sequenceFile, sequence));
	}
}
