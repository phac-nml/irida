package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.assembly.GenomeAssemblyRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Service implementation for storing and retrieving {@link GenomeAssembly}
 */
@Service
public class GenomeAssemblyServiceImpl extends CRUDServiceImpl<Long, GenomeAssembly> implements GenomeAssemblyService {
	private static final Logger logger = LoggerFactory.getLogger(GenomeAssemblyServiceImpl.class);
	private final SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	private final SampleService sampleService;

	private SampleRepository sampleRepository;

	@Autowired
	public GenomeAssemblyServiceImpl(GenomeAssemblyRepository repository,
			SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository, Validator validator,
			SampleService sampleService, SampleRepository sampleRepository) {
		super(repository, validator, GenomeAssembly.class);
		this.sampleGenomeAssemblyJoinRepository = sampleGenomeAssemblyJoinRepository;
		this.sampleService = sampleService;
		this.sampleRepository = sampleRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@PreAuthorize("hasPermission(#sample, 'canUpdateSample')")
	public SampleGenomeAssemblyJoin createAssemblyInSample(Sample sample, GenomeAssembly assembly) {
		assembly = create(assembly);

		SampleGenomeAssemblyJoin sampleGenomeAssemblyJoin = new SampleGenomeAssemblyJoin(sample, assembly);
		sampleGenomeAssemblyJoin = sampleGenomeAssemblyJoinRepository.save(sampleGenomeAssemblyJoin);

		return sampleGenomeAssemblyJoin;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#sample, 'canReadSample')")
	@Override
	public Collection<SampleGenomeAssemblyJoin> getAssembliesForSample(Sample sample) {
		return sampleGenomeAssemblyJoinRepository.findBySample(sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#sample, 'canReadSample')")
	@Override
	public GenomeAssembly getGenomeAssemblyForSample(Sample sample, Long genomeAssemblyId) {
		SampleGenomeAssemblyJoin join = sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(sample.getId(),
				genomeAssemblyId);
		if (join == null) {
			throw new EntityNotFoundException(
					"No join found between sample [" + sample.getId() + "] and genome assembly [" + genomeAssemblyId
							+ "]");
		}

		return join.getObject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@PreAuthorize("hasPermission(#sample, 'canUpdateSample')")
	@Override
	public void removeGenomeAssemblyFromSample(Sample sample, Long genomeAssemblyId) {
		SampleGenomeAssemblyJoin join = sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(sample.getId(),
				genomeAssemblyId);
		if (join != null) {
			logger.debug("Removing genome assembly [" + genomeAssemblyId + "] from sample [" + sample.getId() + "]");
			sampleGenomeAssemblyJoinRepository.delete(join);
			if (sample.getDefaultGenomeAssembly() != null
					&& sample.getDefaultGenomeAssembly().getId().equals(genomeAssemblyId)) {
				sampleRepository.removeDefaultGenomeAssembly(sample);
			}
		} else {
			logger.trace("Genome assembly [" + genomeAssemblyId + "] is not associated with sample [" + sample.getId()
					+ "]. Ignoring.");
		}
	}
}
