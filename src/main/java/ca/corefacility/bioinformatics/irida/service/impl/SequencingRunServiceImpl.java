package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.SequencingRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * 
 */
@Service
public class SequencingRunServiceImpl extends CRUDServiceImpl<Long, SequencingRun> implements SequencingRunService {
	private static final Logger logger = LoggerFactory.getLogger(SequencingRunServiceImpl.class);

	private SampleRepository sampleRepository;
	private SequencingObjectRepository objectRepository;
	private SampleSequencingObjectJoinRepository ssoRepository;
	private AnalysisSubmissionRepository submissionRepository;
	private UserRepository userRepository;

	@Autowired
	public SequencingRunServiceImpl(SequencingRunRepository repository, SequenceFileRepository sequenceFileRepository,
			SequencingObjectRepository objectRepository, SampleSequencingObjectJoinRepository ssoRepository,
			SampleRepository sampleRepository, AnalysisSubmissionRepository submissionRepository,
			UserRepository userRepository, Validator validator) {
		super(repository, validator, SequencingRun.class);
		this.sampleRepository = sampleRepository;
		this.objectRepository = objectRepository;
		this.submissionRepository = submissionRepository;
		this.ssoRepository = ssoRepository;
		this.userRepository = userRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER', 'ROLE_TECHNICIAN')")
	public SequencingRun read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER', 'ROLE_USER', 'ROLE_TECHNICIAN')")
	@PostFilter("hasPermission(filterObject, 'canReadSequencingRun')")
	public Iterable<SequencingRun> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER','ROLE_ADMIN')")
	public void addSequencingObjectToSequencingRun(SequencingRun run, SequencingObject seqobject) {
		// attach a copy of the file to the current transaction.
		seqobject = objectRepository.findById(seqobject.getId()).orElse(null);
		seqobject.setSequencingRun(run);
		objectRepository.save(seqobject);
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER','ROLE_ADMIN', 'ROLE_USER')")
	public SequencingRun create(SequencingRun o) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.loadUserByUsername(authentication.getName());
		o.setUser(user);

		return super.create(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void delete(Long id) {
		Set<Sample> referencedSamples = new HashSet<>();

		logger.trace("Getting samples for SequencingRun " + id);
		// Get the Files from the SequencingRun to delete
		SequencingRun read = read(id);

		Set<SequencingObject> findSequencingObjectsForSequencingRun = objectRepository
				.findSequencingObjectsForSequencingRun(read);

		// For each file in the run
		for (SequencingObject sequencingObject : findSequencingObjectsForSequencingRun) {

			// get the sample the file is in. If the sample is empty when this
			// is complete it will be removed
			SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository
					.getSampleForSequencingObject(sequencingObject);
			if (sampleForSequencingObject != null) {
				logger.trace("Sample " + sampleForSequencingObject.getSubject().getId() + " is used in this run");
				referencedSamples.add(sampleForSequencingObject.getSubject());
			}

			//Get the analysis submissions this file is included in
			Set<AnalysisSubmission> submissions = submissionRepository
					.findAnalysisSubmissionsForSequecingObject(sequencingObject);

			// If there are no submissions, we can delete the pair and file
			if (submissions.isEmpty()) {
				logger.trace("Deleting file " + sequencingObject.getId());

				objectRepository.delete(sequencingObject);
			} else {
				logger.trace("Keeping file " + sequencingObject.getId() + " because it's used in an analysis");
				if (sampleForSequencingObject != null) {
					// otherwise we'll just remove it from the sample
					ssoRepository.delete(sampleForSequencingObject);
				}

				sequencingObject.setSequencingRun(null);
				objectRepository.save(sequencingObject);
			}
		}

		// Delete the run
		logger.trace("Deleting SequencingRun " + id);
		super.delete(id);

		// Search if samples are empty. If they are, delete the sample.
		for (Sample sample : referencedSamples) {
			List<SampleSequencingObjectJoin> sequencesForSample = ssoRepository.getSequencesForSample(sample);
			if (sequencesForSample.isEmpty()) {
				logger.trace("Sample " + sample.getId() + " is empty.  Deleting sample");
				sampleRepository.deleteById(sample.getId());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER','ROLE_ADMIN') or hasPermission(#id, 'canUpdateSequencingRun')")
	@Override
	public SequencingRun updateFields(Long id, Map<String, Object> updatedFields)
			throws ConstraintViolationException, EntityExistsException, InvalidPropertyException {
		return super.updateFields(id, updatedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER','ROLE_ADMIN') or hasPermission(#object, 'canUpdateSequencingRun')")
	@Override
	public SequencingRun update(SequencingRun object) {
		return super.update(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER', 'ROLE_TECHNICIAN')")
	public Page<SequencingRun> list(int page, int size, Direction order, String... sortProperties)
			throws IllegalArgumentException {
		return super.list(page, size, order, sortProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER', 'ROLE_TECHNICIAN')")
	@Override
	public Page<SequencingRun> list(int page, int size, Sort sort) throws IllegalArgumentException {
		return super.list(page, size, sort);
	}
}
