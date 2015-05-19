package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;

/**
 * Implementation for managing {@link SequenceFilePair}
 *
 */
@Service
public class SequenceFilePairServiceImpl extends CRUDServiceImpl<Long, SequenceFilePair> implements
		SequenceFilePairService {

	/**
	 * Reference to {@link SampleSequenceFileJoinRepository}.
	 */
	private final SampleSequenceFileJoinRepository ssfRepository;

	private final SequenceFilePairRepository pairRepository;

	@Autowired
	public SequenceFilePairServiceImpl(
			SampleSequenceFileJoinRepository ssfRepository,
			SequenceFilePairRepository repository,
			Validator validator) {
		super(repository, validator, SequenceFilePair.class);
		this.ssfRepository = ssfRepository;
		this.pairRepository = repository;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#idents, 'canReadSequenceFilePair')")
	public Iterable<SequenceFilePair> readMultiple(Iterable<Long> idents) {
		return super.readMultiple(idents);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#file, 'canReadSequenceFile')")
	public SequenceFile getPairedFileForSequenceFile(SequenceFile file) throws EntityNotFoundException {
		SequenceFilePair pairForSequenceFile = pairRepository.getPairForSequenceFile(file);
		if (pairForSequenceFile != null) {
			for (SequenceFile pair : pairForSequenceFile.getFiles()) {
				if (!pair.equals(file)) {
					return pair;
				}
			}
		}

		throw new EntityNotFoundException("Pair cannot be found for this sequence file");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or (hasPermission(#file1, 'canReadSequenceFile') and hasPermission(#file2, 'canReadSequenceFile'))")
	public SequenceFilePair createSequenceFilePair(SequenceFile file1, SequenceFile file2) {
		return pairRepository.save(new SequenceFilePair(file1, file2));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample) {
		return pairRepository.getSequenceFilePairsForSample(sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#pairedInputFiles, 'canReadSequenceFilePair')")
	public Map<Sample, SequenceFilePair> getUniqueSamplesForSequenceFilePairs(Set<SequenceFilePair> pairedInputFiles)
			throws DuplicateSampleException {
		Map<Sample, SequenceFilePair> sequenceFilePairsSampleMap = new HashMap<>();

		for (SequenceFilePair filePair : pairedInputFiles) {
			SequenceFile pair1 = filePair.getFiles().iterator().next();
			Join<Sample, SequenceFile> pair1Join = ssfRepository.getSampleForSequenceFile(pair1);
			Sample sample = pair1Join.getSubject();
			if (sequenceFilePairsSampleMap.containsKey(sample)) {
				SequenceFilePair previousPair = sequenceFilePairsSampleMap.get(sample);
				throw new DuplicateSampleException("Sequence file pairs " + pair1 + ", " + previousPair
						+ " have the same sample " + sample);
			} else {
				sequenceFilePairsSampleMap.put(sample, filePair);
			}
		}

		return sequenceFilePairsSampleMap;
	}
}
