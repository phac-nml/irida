package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.MiseqRunSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Service
public class MiseqRunServiceImpl extends CRUDServiceImpl<Long, MiseqRun> implements MiseqRunService {
	private static final Logger logger = LoggerFactory.getLogger(MiseqRunServiceImpl.class);

	private MiseqRunSequenceFileJoinRepository mrsfRepository;
	private SampleSequenceFileJoinRepository ssfRepository;
	private SampleRepository sampleRepository;

	protected MiseqRunServiceImpl() {
		super(null, null, MiseqRun.class);
	}

	@Autowired
	public MiseqRunServiceImpl(MiseqRunRepository repository, MiseqRunSequenceFileJoinRepository mrsfRepository,
			SampleSequenceFileJoinRepository ssfRepository, SampleRepository sampleRepository, Validator validator) {
		super(repository, validator, MiseqRun.class);
		this.mrsfRepository = mrsfRepository;
		this.ssfRepository = ssfRepository;
		this.sampleRepository = sampleRepository;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional
	public Join<MiseqRun, SequenceFile> addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) {
		try {
			MiseqRunSequenceFileJoin join = new MiseqRunSequenceFileJoin(run, file);
			return mrsfRepository.save(join);
		} catch (DataIntegrityViolationException e) {
			throw new EntityExistsException("Sequence file [" + file.getId() + "] has already been added to MiseqRun ["
					+ run.getId() + "]");
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional(readOnly = true)
	public Join<MiseqRun, SequenceFile> getMiseqRunForSequenceFile(SequenceFile file) {
		return mrsfRepository.getMiseqRunForSequenceFile(file);
	}

	@Override
	public MiseqRun create(MiseqRun o) {
		return super.create(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long id) {
		logger.trace("Getting samples for miseq run " + id);
		// Get the Files from the MiSeqRun to delete
		MiseqRun read = read(id);
		List<Join<MiseqRun, SequenceFile>> filesForMiseqRun = mrsfRepository.getFilesForMiseqRun(read);

		// Get the Samples used in the MiSeqRun that is going to be deleted
		Set<Sample> referencedSamples = filesForMiseqRun.stream()
		// get a stream of samples related to the files
				.map(j -> ssfRepository.getSampleForSequenceFile(j.getObject()).getSubject())
				// convert the stream into a set of unique samples
				.collect(Collectors.toSet());

		// Delete the run
		logger.trace("Deleting MiSeq run");
		super.delete(id);

		// Search if samples are empty. If they are, delete the sample.
		referencedSamples.stream().filter(s -> ssfRepository.getFilesForSample(s).isEmpty())
				.forEach(s -> sampleRepository.delete(s.getId()));
	}
}
