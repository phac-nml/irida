package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;

/**
 * Implementation of {@link AnalysisService}.
 * 
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#analysisId, 'canReadAnalysis')")
	public Analysis read(final Long analysisId) {
		return super.read(analysisId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_USER')")
	public Analysis create(@Valid Analysis analysis) {
		for (AnalysisOutputFile a : analysis.getAnalysisOutputFiles()) {
			analysisOutputFileRepository.save(a);
		}
		return analysisRepository.save(analysis);
	}
        
        
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisFastQC getFastQCAnalysisForSequenceFile(final SequenceFile sequenceFile) {
		return analysisRepository.findFastqcAnalysisForSequenceFile(sequenceFile);
	}
}
