package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.SequencingRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Service
public class SequencingRunServiceImpl extends CRUDServiceImpl<Long, SequencingRun> implements SequencingRunService {
	private static final Logger logger = LoggerFactory.getLogger(SequencingRunServiceImpl.class);

	private SampleSequenceFileJoinRepository ssfRepository;
	private SampleRepository sampleRepository;
	private SequenceFileRepository sequenceFileRepository;

	@Autowired
	public SequencingRunServiceImpl(SequencingRunRepository repository, SequenceFileRepository sequenceFileRepository,
			SampleSequenceFileJoinRepository ssfRepository, SampleRepository sampleRepository, Validator validator) {
		super(repository, validator, SequencingRun.class);
		this.ssfRepository = ssfRepository;
		this.sampleRepository = sampleRepository;
		this.sequenceFileRepository = sequenceFileRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	public SequencingRun read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<SequencingRun> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional
	public void addSequenceFileToSequencingRun(SequencingRun run, SequenceFile file) {
		// attach a copy of the file to the current transaction.
		file = sequenceFileRepository.findOne(file.getId());
		file.setSequencingRun(run);
		sequenceFileRepository.save(file);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional(readOnly = true)
	public SequencingRun getSequencingRunForSequenceFile(SequenceFile file) {
		SequenceFile loaded = sequenceFileRepository.findOne(file.getId());
		return loaded.getSequencingRun();
	}

	@Override
	public SequencingRun create(SequencingRun o) {
		return super.create(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long id) {
		Set<Sample> referencedSamples = new HashSet<>();

		logger.trace("Getting samples for SequencingRun " + id);
		// Get the Files from the SequencingRun to delete
		SequencingRun read = read(id);
		Set<SequenceFile> filesForSequencingRun = sequenceFileRepository.findSequenceFilesForSequencingRun(read);

		// Get the Samples used in the SequencingRun that is going to be deleted
		for (SequenceFile file : filesForSequencingRun) {
			Join<Sample, SequenceFile> sampleForSequenceFile = ssfRepository.getSampleForSequenceFile(file);
			logger.trace("Sample " + sampleForSequenceFile.getSubject().getId() + " is used in this run");
			referencedSamples.add(sampleForSequenceFile.getSubject());
		}

		// Delete the run
		logger.trace("Deleting SequencingRun");
		super.delete(id);

		// Search if samples are empty. If they are, delete the sample.
		for (Sample sample : referencedSamples) {
			List<Join<Sample, SequenceFile>> filesForSample = ssfRepository.getFilesForSample(sample);
			if (filesForSample.isEmpty()) {
				logger.trace("Sample " + sample.getId() + " is empty.  Deleting sample");
				sampleRepository.delete(sample.getId());
			}
		}
	}
}
