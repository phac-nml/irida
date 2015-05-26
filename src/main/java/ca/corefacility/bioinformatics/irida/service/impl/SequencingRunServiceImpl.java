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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.SequencingRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * 
 */
@Service
public class SequencingRunServiceImpl extends CRUDServiceImpl<Long, SequencingRun> implements SequencingRunService {
	private static final Logger logger = LoggerFactory.getLogger(SequencingRunServiceImpl.class);

	private SampleSequenceFileJoinRepository ssfRepository;
	private SampleRepository sampleRepository;
	private SequenceFileRepository sequenceFileRepository;
	private SequenceFilePairRepository pairRepository;
	private AnalysisSubmissionRepository submissionRepository;

	@Autowired
	public SequencingRunServiceImpl(SequencingRunRepository repository, SequenceFileRepository sequenceFileRepository,
			SampleSequenceFileJoinRepository ssfRepository, SampleRepository sampleRepository,
			SequenceFilePairRepository pairRepository, AnalysisSubmissionRepository submissionRepository,
			Validator validator) {
		super(repository, validator, SequencingRun.class);
		this.ssfRepository = ssfRepository;
		this.sampleRepository = sampleRepository;
		this.sequenceFileRepository = sequenceFileRepository;
		this.pairRepository = pairRepository;
		this.submissionRepository = submissionRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER')")
	public SequencingRun read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public Iterable<SequencingRun> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_SEQUENCER')")
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#file, 'canReadSequenceFile')")
	public SequencingRun getSequencingRunForSequenceFile(SequenceFile file) {
		SequenceFile loaded = sequenceFileRepository.findOne(file.getId());
		return loaded.getSequencingRun();
	}

	@Override
	@PreAuthorize("hasRole('ROLE_SEQUENCER')")
	public SequencingRun create(SequencingRun o) {
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
		Set<SequenceFile> filesForSequencingRun = sequenceFileRepository.findSequenceFilesForSequencingRun(read);

		// For each file in the run
		for (SequenceFile file : filesForSequencingRun) {

			// get the sample the file is in. If the sample is empty when this
			// is complete it will be removed
			Join<Sample, SequenceFile> sampleForSequenceFile = ssfRepository.getSampleForSequenceFile(file);
			if (sampleForSequenceFile != null) {
				logger.trace("Sample " + sampleForSequenceFile.getSubject().getId() + " is used in this run");
				referencedSamples.add(sampleForSequenceFile.getSubject());
			}

			// Get the SequenceFilePair for this object
			SequenceFilePair pair = pairRepository.getPairForSequenceFile(file);

			// Get the analysis submissions this file is included in
			Set<AnalysisSubmission> submissions = submissionRepository.findAnalysisSubmissionForSequenceFile(file);
			Set<AnalysisSubmission> pairSubmissions = submissionRepository
					.findAnalysisSubmissionForSequenceFilePair(pair);

			// If there are no submissions, we can delete the pair and file
			if (submissions.isEmpty() && pairSubmissions.isEmpty()) {
				logger.trace("Deleting file " + file.getId());
				if (pair != null) {
					pairRepository.delete(pair);
				}
				sequenceFileRepository.delete(file);
			} else {
				logger.trace("Keeping file " + file.getId() + " because it's used in an analysis");
				if (sampleForSequenceFile != null) {
					// otherwise we'll just remove it from the sample
					ssfRepository.delete((SampleSequenceFileJoin) sampleForSequenceFile);
				}
				
				file.setSequencingRun(null);
				sequenceFileRepository.updateWithoutFileRevision(file);
			}
		}

		// Delete the run
		logger.trace("Deleting SequencingRun " + id);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_SEQUENCER')")
	public SequencingRun update(Long id, Map<String, Object> updatedFields) throws ConstraintViolationException,
			EntityExistsException, InvalidPropertyException {
		return super.update(id, updatedFields);
	}
}
