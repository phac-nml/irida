package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.SampleSequencingObjectSpecification;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.impl.processor.SequenceFileProcessorLauncher;

/**
 * Implementation of {@link SequencingObjectService} using a
 * {@link SequencingObjectRepository} and
 * {@link SampleSequencingObjectJoinRepository} to persist and load objects.
 */
@Service
public class SequencingObjectServiceImpl extends CRUDServiceImpl<Long, SequencingObject> implements
		SequencingObjectService {

	private final SampleSequencingObjectJoinRepository ssoRepository;
	private final SequenceFileRepository sequenceFileRepository;
	private TaskExecutor fileProcessingChainExecutor;
	private FileProcessingChain fileProcessingChain;
	private final SequencingObjectRepository repository;

	@Autowired
	public SequencingObjectServiceImpl(SequencingObjectRepository repository, SequenceFileRepository sequenceFileRepository,
			SampleSequencingObjectJoinRepository ssoRepository,
			@Qualifier("fileProcessingChainExecutor") TaskExecutor executor, FileProcessingChain fileProcessingChain,
			Validator validator) {
		super(repository, validator, SequencingObject.class);
		this.repository = repository;
		this.ssoRepository = ssoRepository;
		this.fileProcessingChainExecutor = executor;
		this.fileProcessingChain = fileProcessingChain;
		this.sequenceFileRepository = sequenceFileRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_SEQUENCER', 'ROLE_USER')")
	public SequencingObject create(SequencingObject object) throws ConstraintViolationException, EntityExistsException {
		for(SequenceFile file : object.getFiles()){
			file = sequenceFileRepository.save(file);
		}

		SequencingObject so = super.create(object);
		fileProcessingChainExecutor.execute(new SequenceFileProcessorLauncher(fileProcessingChain, so.getId(),
				SecurityContextHolder.getContext()));
		return so;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#sample, 'canReadSample')")
	@LaunchesProjectEvent(DataAddedToSampleProjectEvent.class)
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
	public Map<Sample, SequencingObject> getUniqueSamplesForSequenceFiles(Set<SequencingObject> sequenceFiles)
			throws DuplicateSampleException {
		Map<Sample, SequencingObject> sequenceFilePairsSampleMap = new HashMap<>();

		for (SequencingObject filePair : sequenceFiles) {
			SequenceFile pair1 = filePair.getFiles().iterator().next();

			SampleSequencingObjectJoin join = ssoRepository.getSampleForSequencingObject(filePair);

			Sample sample = join.getSubject();
			if (sequenceFilePairsSampleMap.containsKey(sample)) {
				SequencingObject previousPair = sequenceFilePairsSampleMap.get(sample);
				throw new DuplicateSampleException("Sequence file pairs " + pair1 + ", " + previousPair
						+ " have the same sample " + sample);
			} else {
				sequenceFilePairsSampleMap.put(sample, filePair);
			}
		}

		return sequenceFilePairsSampleMap;
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER')")
	public Set<SequencingObject> getSequencingObjectsForSequencingRun(SequencingRun sequencingRun) {
		return repository.findSequencingObjectsForSequencingRun(sequencingRun);
	}

}
