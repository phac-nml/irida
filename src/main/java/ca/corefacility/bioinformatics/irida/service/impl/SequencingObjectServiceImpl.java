package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.exceptions.*;
import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.*;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;
import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenatorFactory;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceConcatenationRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.SampleSequencingObjectSpecification;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation of {@link SequencingObjectService} using a {@link SequencingObjectRepository} and
 * {@link SampleSequencingObjectJoinRepository} to persist and load objects.
 */
@Service
public class SequencingObjectServiceImpl extends CRUDServiceImpl<Long, SequencingObject>
		implements SequencingObjectService {

	private final SampleSequencingObjectJoinRepository ssoRepository;
	private final SequenceFileRepository sequenceFileRepository;

	private final SequencingObjectRepository repository;
	private final SequenceConcatenationRepository concatenationRepository;

	@Autowired
	public SequencingObjectServiceImpl(SequencingObjectRepository repository,
			SequenceFileRepository sequenceFileRepository, SampleSequencingObjectJoinRepository ssoRepository,
			SequenceConcatenationRepository concatenationRepository, Validator validator) {
		super(repository, validator, SequencingObject.class);
		this.repository = repository;
		this.ssoRepository = ssoRepository;

		this.sequenceFileRepository = sequenceFileRepository;
		this.concatenationRepository = concatenationRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize(
			"hasAnyRole('ROLE_ADMIN','ROLE_SEQUENCER', 'ROLE_TECHNICIAN') or hasPermission(#id, 'canReadSequencingObject')")
	@Override
	public SequencingObject read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER')")
	public SequencingObject create(SequencingObject object) throws ConstraintViolationException, EntityExistsException {

		SequencingRun sequencingRun = object.getSequencingRun();
		if (sequencingRun != null) {
			if (object instanceof SingleEndSequenceFile && sequencingRun.getLayoutType() != LayoutType.SINGLE_END) {
				throw new IllegalArgumentException("Attempting to add a single end file to a non single end run");
			} else if (object instanceof SequenceFilePair && sequencingRun.getLayoutType() != LayoutType.PAIRED_END) {
				throw new IllegalArgumentException("Attempting to add a paired end file to a non paired end run");
			}
		}

		for (SequenceFile file : object.getFiles()) {
			file = sequenceFileRepository.save(file);
		}

		return super.create(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#sample, 'canUpdateSample')")
	@LaunchesProjectEvent(DataAddedToSampleProjectEvent.class)
	public SampleSequencingObjectJoin createSequencingObjectInSample(SequencingObject seqObject, Sample sample) {
		// create the sequencing object
		seqObject = create(seqObject);

		// save the new join
		SampleSequencingObjectJoin sampleSequencingObjectJoin = new SampleSequencingObjectJoin(sample, seqObject);
		return ssoRepository.save(sampleSequencingObjectJoin);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	public Collection<SampleSequencingObjectJoin> getSequencingObjectsForSample(Sample sample) {
		return ssoRepository.getSequencesForSample(sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	@Override
	public Collection<SampleSequencingObjectJoin> getSequencesForSampleOfType(Sample sample,
			Class<? extends SequencingObject> type) {

		return ssoRepository.findAll(SampleSequencingObjectSpecification.getSequenceOfTypeForSample(sample, type));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	public SequencingObject readSequencingObjectForSample(Sample sample, Long objectId) {
		SampleSequencingObjectJoin readObjectForSample = ssoRepository.readObjectForSample(sample, objectId);

		return readObjectForSample.getObject();
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#sequenceFiles, 'canReadSequencingObject')")
	@Override
	public <T extends SequencingObject> Map<Sample, T> getUniqueSamplesForSequencingObjects(Set<T> sequenceFiles)
			throws DuplicateSampleException {
		Map<Sample, T> sequenceFilesSampleMap = new HashMap<>();

		for (T seqObj : sequenceFiles) {
			SequenceFile file = seqObj.getFiles().iterator().next();

			SampleSequencingObjectJoin join = ssoRepository.getSampleForSequencingObject(seqObj);

			if (join == null) {
				throw new EntityNotFoundException(
						"No sample associated with sequence file " + seqObj.getClass() + "[id=" + seqObj.getId() + "]");
			} else {
				Sample sample = join.getSubject();
				if (sequenceFilesSampleMap.containsKey(sample)) {
					SequencingObject previousFile = sequenceFilesSampleMap.get(sample);
					throw new DuplicateSampleException(
							"Sequence files " + file + ", " + previousFile + " have the same sample " + sample);
				} else {
					sequenceFilesSampleMap.put(sample, seqObj);
				}
			}
		}

		return sequenceFilesSampleMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER', 'ROLE_TECHNICIAN')")
	public Set<SequencingObject> getSequencingObjectsForSequencingRun(SequencingRun sequencingRun) {
		return repository.findSequencingObjectsForSequencingRun(sequencingRun);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#idents, 'canReadSequencingObject')")
	public Iterable<SequencingObject> readMultiple(Iterable<Long> idents) {
		return super.readMultiple(idents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadSequencingObject')")
	public Boolean exists(Long id) {
		return super.exists(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadSequencingObject')")
	public SequencingObject updateRemoteStatus(Long id, RemoteStatus remoteStatus)
			throws ConstraintViolationException, EntityExistsException, InvalidPropertyException {

		return super.updateFields(id, ImmutableMap.of("remoteStatus", remoteStatus));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#submission, 'canReadAnalysisSubmission')")
	public Set<SequencingObject> getSequencingObjectsForAnalysisSubmission(AnalysisSubmission submission) {
		return repository.findSequencingObjectsForAnalysisSubmission(submission);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#submission, 'canReadAnalysisSubmission')")
	@SuppressWarnings("unchecked")
	public <Type extends SequencingObject> Set<Type> getSequencingObjectsOfTypeForAnalysisSubmission(
			AnalysisSubmission submission, Class<Type> type) {
		Set<SequencingObject> findSequencingObjectsForAnalysisSubmission = getSequencingObjectsForAnalysisSubmission(
				submission);

		return findSequencingObjectsForAnalysisSubmission.stream().filter(f -> {
			return f.getClass().equals(type);
		}).map(f -> {
			return (Type) f;
		}).collect(Collectors.toSet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(
			"hasPermission(#toJoin, 'canReadSequencingObject') and hasPermission(#targetSample, 'canUpdateSample')")
	@Transactional
	public SampleSequencingObjectJoin concatenateSequences(List<SequencingObject> toJoin, String filename,
			Sample targetSample, boolean removeOriginals) throws ConcatenateException {

		SequencingObjectConcatenator<? extends SequencingObject> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(
				toJoin);

		SequencingObject concatenated = concatenator.concatenateFiles(toJoin, filename);

		SampleSequencingObjectJoin created = createSequencingObjectInSample(concatenated, targetSample);

		concatenationRepository.save(new SequenceConcatenation(created.getObject(), toJoin));

		if (removeOriginals) {
			for (SequencingObject obj : toJoin) {
				SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository.getSampleForSequencingObject(obj);
				ssoRepository.delete(sampleForSequencingObject);
			}
		}

		return created;
	}

}
