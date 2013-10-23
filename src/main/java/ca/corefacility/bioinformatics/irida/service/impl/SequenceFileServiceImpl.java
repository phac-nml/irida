package ca.corefacility.bioinformatics.irida.service.impl;

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
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.MiseqRunSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.SequenceFileOverrepresentedSequenceJoinRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation for managing {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImpl extends CRUDServiceImpl<Long, SequenceFile> implements SequenceFileService {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImpl.class);

	/**
	 * A reference to the file system repository.
	 */
	private CRUDRepository<Long, SequenceFile> fileRepository;
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
			CRUDRepository<Long, SequenceFile> fileRepository, SampleSequenceFileJoinRepository ssfRepository,
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
	public SequenceFile create(SequenceFile sequenceFile) {
		// Send the file to the database repository to be stored (in super)
		sequenceFile = super.create(sequenceFile);
		// Then store the file in an appropriate directory
		sequenceFile = fileRepository.create(sequenceFile);
		// And finally, update the database with the stored file location

		Map<String, Object> changed = new HashMap<>();
		changed.put("file", sequenceFile.getFile());
		final SequenceFile updatedSequenceFile = super.update(sequenceFile.getId(), changed);

		logger.debug("Outside thread: " + Thread.currentThread().toString() + " with sequence file ["
				+ updatedSequenceFile.getId() + "]");

		return updatedSequenceFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadSequenceFile')")
	public SequenceFile update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException {
		if(updatedFields.containsKey("fileRevisionNumber")){
			throw new InvalidPropertyException("File revision number cannot be updated manually.");
		}
		
		SequenceFile updated = super.update(id, updatedFields);
		
		if (updatedFields.containsKey("file")) {
			//need to read the sequence file to get the current file revision number
			Long fileRevisionNumber = updated.getFileRevisionNumber();
			fileRevisionNumber++;
			updatedFields.put("fileRevisionNumber", fileRevisionNumber);
			
			updated = fileRepository.update(id, updatedFields);
			super.update(id, ImmutableMap.of("file", (Object) updated.getFile(),"fileRevisionNumber", fileRevisionNumber));
		}

		return updated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT') or hasPermission(#sample, 'canReadSample')")
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
