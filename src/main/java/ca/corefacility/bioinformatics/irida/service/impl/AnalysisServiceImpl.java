package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;

/**
 * Implementation of {@link AnalysisService}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Service
public class AnalysisServiceImpl extends CRUDServiceImpl<Long, Analysis> implements AnalysisService {

	private final AnalysisRepository analysisRepository;
	private final AnalysisOutputFileRepository analysisOutputFileRepository;

	@Autowired
	public AnalysisServiceImpl(AnalysisRepository analysisRepository,
			AnalysisOutputFileRepository analysisOutputFileRepository, Validator validator) {
		super(analysisRepository, validator, Analysis.class);
		this.analysisRepository = analysisRepository;
		this.analysisOutputFileRepository = analysisOutputFileRepository;
	}

	@Override
	@Transactional
	public Analysis create(Analysis analysis) {
		for (AnalysisOutputFile a : analysis.getAnalysisOutputFiles()) {
			analysisOutputFileRepository.save(a);
		}
		return analysisRepository.save(analysis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Analysis> getAnalysesForSequenceFile(SequenceFile file) {
		return analysisRepository.findAnalysesForSequenceFile(file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Analysis> Set<T> getAnalysesForSequenceFile(SequenceFile file, Class<T> analysisType) {
		return analysisRepository.findAnalysesForSequenceFile(file, analysisType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<Analysis> list(int page, int size, Direction order, String... sortProperty)
			throws IllegalArgumentException {
		return super.list(page, size, order, sortProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<Analysis> list(int page, int size, Direction order) throws IllegalArgumentException {
		return super.list(page, size, order);
	}
}
