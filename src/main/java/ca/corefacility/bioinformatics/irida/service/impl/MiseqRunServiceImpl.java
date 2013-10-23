package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.MiseqRunSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Service
public class MiseqRunServiceImpl extends CRUDServiceImpl<Long, MiseqRun> implements MiseqRunService {

	private MiseqRunRepository miseqRepo;
	private MiseqRunSequenceFileJoinRepository mrsfRepository;

	protected MiseqRunServiceImpl() {
		super(null, null, MiseqRun.class);
	}

	public MiseqRunServiceImpl(MiseqRunRepository repository, MiseqRunSequenceFileJoinRepository mrsfRepository,
			Validator validator) {
		super(repository, validator, MiseqRun.class);
		this.miseqRepo = repository;
		this.mrsfRepository = mrsfRepository;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional
	public Join<MiseqRun, SequenceFile> addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) {
		MiseqRunSequenceFileJoin join = new MiseqRunSequenceFileJoin(run, file);
		return mrsfRepository.save(join);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional(readOnly = true)
	public Join<MiseqRun, SequenceFile> getMiseqRunForSequenceFile(SequenceFile file) {
		return miseqRepo.getMiseqRunForSequenceFile(file);
	}

}
