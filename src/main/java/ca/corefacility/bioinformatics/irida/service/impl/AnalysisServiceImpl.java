package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Set;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.repositories.AnalysisRepository;
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

	@Autowired
	public AnalysisServiceImpl(AnalysisRepository analysisRepository, Validator validator) {
		super(analysisRepository, validator, Analysis.class);
		this.analysisRepository = analysisRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Analysis> getAnalysesForSequenceFile(SequenceFile file) {
		return analysisRepository.findAnalysesForSequenceFile(file);
	}

}
