package ca.corefacility.bioinformatics.irida.service.impl;

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
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.processing.annotations.ModifiesSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

/**
 * Implementation for managing {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Service
public class SequenceFileServiceImpl extends CRUDServiceImpl<Long, SequenceFile> implements SequenceFileService {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImpl.class);

	/**
	 * Reference to {@link SampleSequenceFileJoinRepository}.
	 */
	private SampleSequenceFileJoinRepository ssfRepository;

	/**
	 * Reference to {@link SequenceFileRepository}.
	 */
	private SequenceFileRepository sequenceFileRepository;

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
			SampleSequenceFileJoinRepository ssfRepository, Validator validator) {
		super(sequenceFileRepository, validator, SequenceFile.class);
		this.sequenceFileRepository = sequenceFileRepository;
		this.ssfRepository = ssfRepository;
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
		return super.create(sequenceFile);
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
		return super.update(id, updatedFields);
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
	public Set<SequenceFile> getSequenceFilesForSequencingRun(SequencingRun miseqRun) {
		return sequenceFileRepository.findSequenceFilesForSequencingRun(miseqRun);
	}
}
