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

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Service
public class MiseqRunServiceImpl extends CRUDServiceImpl<Long, MiseqRun> implements MiseqRunService {
	private static final Logger logger = LoggerFactory.getLogger(MiseqRunServiceImpl.class);

	private SampleSequenceFileJoinRepository ssfRepository;
	private SampleRepository sampleRepository;
	private SequenceFileRepository sequenceFileRepository;

	protected MiseqRunServiceImpl() {
		super(null, null, MiseqRun.class);
	}

	@Autowired
	public MiseqRunServiceImpl(MiseqRunRepository repository, SequenceFileRepository sequenceFileRepository,
			SampleSequenceFileJoinRepository ssfRepository, SampleRepository sampleRepository, Validator validator) {
		super(repository, validator, MiseqRun.class);
		this.ssfRepository = ssfRepository;
		this.sampleRepository = sampleRepository;
		this.sequenceFileRepository = sequenceFileRepository;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional
	public void addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) {
		file.setMiseqRun(run);
		sequenceFileRepository.save(file);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional(readOnly = true)
	public MiseqRun getMiseqRunForSequenceFile(SequenceFile file) {
		SequenceFile loaded = sequenceFileRepository.findOne(file.getId());
		return loaded.getMiseqRun();
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
		Set<Sample> referencedSamples = new HashSet<>();

		logger.trace("Getting samples for miseq run " + id);
		// Get the Files from the MiSeqRun to delete
		MiseqRun read = read(id);
		Set<SequenceFile> filesForMiseqRun = read.getSequenceFiles();

		//Get the Samples used in the MiSeqRun that is going to be deleted
		for(SequenceFile file : filesForMiseqRun){
			Join<Sample, SequenceFile> sampleForSequenceFile = ssfRepository.getSampleForSequenceFile(file);
			logger.trace("Sample " + sampleForSequenceFile.getSubject().getId() + " is used in this run");
			referencedSamples.add(sampleForSequenceFile.getSubject());
		}
		

		// Delete the run
		logger.trace("Deleting MiSeq run");
		super.delete(id);

		//Search if samples are empty.  If they are, delete the sample.
		for(Sample sample : referencedSamples){
			List<Join<Sample, SequenceFile>> filesForSample = ssfRepository.getFilesForSample(sample);
			if(filesForSample.isEmpty()){
				logger.trace("Sample " + sample.getId() +" is empty.  Deleting sample");
				sampleRepository.delete(sample.getId());
			}
		}
	}
}
