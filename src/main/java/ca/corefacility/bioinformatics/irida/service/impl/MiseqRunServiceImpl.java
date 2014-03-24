package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
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
	public MiseqRunServiceImpl(MiseqRunRepository repository, MiseqRunSequenceFileJoinRepository mrsfRepository, SampleSequenceFileJoinRepository ssfRepository, SampleRepository sampleRepository,
			Validator validator) {
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
	public void delete(Long id){
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteCascadeToSample(Long id) throws EntityNotFoundException {
		List<Sample> referencedSamples = new ArrayList<>();
		
		logger.trace("Getting samples for miseq run " + id);
		MiseqRun read = read(id);
		List<Join<MiseqRun, SequenceFile>> filesForMiseqRun = mrsfRepository.getFilesForMiseqRun(read);
		
		for(Join<MiseqRun,SequenceFile> join : filesForMiseqRun){
			Join<Sample, SequenceFile> sampleForSequenceFile = ssfRepository.getSampleForSequenceFile(join.getObject());
			logger.trace("Sample " + sampleForSequenceFile.getSubject().getId() + " is used in this run");
			referencedSamples.add(sampleForSequenceFile.getSubject());
		}
		
		logger.trace("Deleting MiSeq run");
		delete(id);
		
		for(Sample sample: referencedSamples){
			logger.trace("Testing if sample " + sample.getId() +" is empty");
			List<Join<Sample, SequenceFile>> filesForSample = ssfRepository.getFilesForSample(sample);
			if(filesForSample.isEmpty()){
				logger.trace("Sample " + sample.getId() +" is empty.  Deleting sample");
				sampleRepository.delete(sample.getId());
			}
		}
		
	}
}
