package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.assembly.GenomeAssemblyRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;

/**
 * Service implementation for storing and retrieving {@link GenomeAssembly}
 */
@Service
public class GenomeAssemblyServiceImpl extends CRUDServiceImpl<Long, GenomeAssembly> implements GenomeAssemblyService {
	private static final Logger logger = LoggerFactory.getLogger(GenomeAssemblyServiceImpl.class);
	private final SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	private final GenomeAssemblyRepository repository;

	@Autowired
	public GenomeAssemblyServiceImpl(GenomeAssemblyRepository repository,
			SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository, Validator validator) {
		super(repository, validator, GenomeAssembly.class);
		this.repository = repository;
		this.sampleGenomeAssemblyJoinRepository = sampleGenomeAssemblyJoinRepository;
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
			throw new EntityNotFoundException("No join found between sample [" + sample.getId()
					+ "] and genome assembly [" + genomeAssemblyId + "]");
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
			sampleGenomeAssemblyJoinRepository.deleteById(join.getId());
		} else {
			logger.trace("Genome assembly [" + genomeAssemblyId + "] is not associated with sample [" + sample.getId()
					+ "]. Ignoring.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#genomeAssemblies, 'canReadGenomeAssembly')")
	@Override
	public Map<Sample, GenomeAssembly> getUniqueSamplesForGenomeAssemblies(Set<GenomeAssembly> genomeAssemblies)
			throws DuplicateSampleException {
		Map<Sample, GenomeAssembly> genomeAssembliesSampleMap = new HashMap<>();

		for (GenomeAssembly genomeAssembly : genomeAssemblies) {
			SampleGenomeAssemblyJoin join = sampleGenomeAssemblyJoinRepository
					.getSampleForGenomeAssembly(genomeAssembly);

			if (join == null) {
				throw new EntityNotFoundException("No sample associated with genome assembly "
						+ genomeAssembly.getClass() + "[id=" + genomeAssembly.getId() + "]");
			} else {
				Sample sample = join.getSubject();
				if (genomeAssembliesSampleMap.containsKey(sample)) {
					GenomeAssembly prevGenomeAssembly = genomeAssembliesSampleMap.get(sample);
					throw new DuplicateSampleException("Genome Assemblies " + genomeAssembly + ", " + prevGenomeAssembly
							+ " have the sample sample " + sample);
				} else {
					genomeAssembliesSampleMap.put(sample, genomeAssembly);
				}
			}
		}

		return genomeAssembliesSampleMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#submission, 'canReadAnalysisSubmission')")
	public Set<GenomeAssembly> getGenomeAssembliesForAnalysisSubmission(AnalysisSubmission submission) {
		return repository.findGenomeAssembliesForAnalysisSubmission(submission);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#idents, 'canReadGenomeAssembly')")
	public Iterable<GenomeAssembly> readMultiple(Iterable<Long> idents) {
		return super.readMultiple(idents);
	}
}
