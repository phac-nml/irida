package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import javax.transaction.Transactional;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
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
        private final SequenceFileRepository sequenceFileRepository;

	@Autowired
	public AnalysisServiceImpl(AnalysisRepository analysisRepository,
			AnalysisOutputFileRepository analysisOutputFileRepository,
                        final SequenceFileRepository sequenceFileRepository, Validator validator) {
		super(analysisRepository, validator, Analysis.class);
		this.analysisRepository = analysisRepository;
		this.analysisOutputFileRepository = analysisOutputFileRepository;
                this.sequenceFileRepository = sequenceFileRepository;
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
	public AnalysisFastQC getFastQCAnalysisForSequenceFile(final SequenceFile sequenceFile) {
		return sequenceFileRepository.findFastqcAnalysisForSequenceFile(sequenceFile);
	}
}
