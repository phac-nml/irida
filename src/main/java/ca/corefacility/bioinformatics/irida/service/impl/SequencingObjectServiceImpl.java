package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

/**
 * Implementation of {@link SequencingObjectService} using a
 * {@link SequencingObjectRepository} and
 * {@link SampleSequencingObjectJoinRepository} to persist and load objects.
 */
@Service
public class SequencingObjectServiceImpl extends CRUDServiceImpl<Long, SequencingObject> implements
		SequencingObjectService {

	private final SampleSequencingObjectJoinRepository ssoRepository;

	@Autowired
	public SequencingObjectServiceImpl(SequencingObjectRepository repository,
			SampleSequencingObjectJoinRepository ssoRepository, Validator validator) {
		super(repository, validator, SequencingObject.class);
		this.ssoRepository = ssoRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER')")
	public SequencingObject create(SequencingObject object) throws ConstraintViolationException, EntityExistsException {
		return super.create(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	public SampleSequencingObjectJoin createSequencingObjectInSample(SequencingObject seqObject, Sample sample) {
		// create the sequencing object
		seqObject = create(seqObject);

		/*
		 * TODO:Verify that the SequencingRun matches the type of
		 * sequencingobject being created
		 */

		// save the new join
		SampleSequencingObjectJoin sampleSequencingObjectJoin = new SampleSequencingObjectJoin(sample, seqObject);
		return ssoRepository.save(sampleSequencingObjectJoin);
	}

}
