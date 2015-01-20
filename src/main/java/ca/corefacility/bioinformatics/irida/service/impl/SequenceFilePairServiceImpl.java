package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;

/**
 * Implementation for managing {@link SequenceFilePair}
 *
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
@Service
public class SequenceFilePairServiceImpl extends CRUDServiceImpl<Long, SequenceFilePair> implements
		SequenceFilePairService {

	private final SequenceFilePairRepository pairRepository;

	@Autowired
	public SequenceFilePairServiceImpl(
			SequenceFilePairRepository repository,
			Validator validator) {
		super(repository, validator, SequenceFilePair.class);
		this.pairRepository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	public SequenceFilePair createSequenceFilePair(SequenceFile file1, SequenceFile file2) {
		return pairRepository.save(new SequenceFilePair(file1, file2));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample) {
		return pairRepository.getSequenceFilePairsForSample(sample);
	}
}
