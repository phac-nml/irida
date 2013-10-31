package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
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

	private MiseqRunSequenceFileJoinRepository mrsfRepository;

	protected MiseqRunServiceImpl() {
		super(null, null, MiseqRun.class);
	}

	public MiseqRunServiceImpl(MiseqRunRepository repository, MiseqRunSequenceFileJoinRepository mrsfRepository,
			Validator validator) {
		super(repository, validator, MiseqRun.class);
		this.mrsfRepository = mrsfRepository;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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

}
