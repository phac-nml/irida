package ca.corefacility.bioinformatics.irida.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;

/**
 * A service for managing registered {@link AnalysisType}s.
 */
@Component
public class AnalysisTypesServiceImpl implements AnalysisTypesService {

	private Map<String, AnalysisType> allTypesMap;

	private Map<String, AnalysisType> runnableTypesMap;

	private Map<AnalysisType, String> viewers;

	/**
	 * Builds a new default {@link AnalysisTypesServiceImpl}.
	 */
	public AnalysisTypesServiceImpl() {
		allTypesMap = new HashMap<>();
		runnableTypesMap = new HashMap<>();
		viewers = new HashMap<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<AnalysisType> executableAnalysisTypes() {
		return Sets.newHashSet(runnableTypesMap.values());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisType fromString(String string) {
		return allTypesMap.get(string);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<AnalysisType> values() {
		return runnableTypesMap.values();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(AnalysisType analysisType) {
		return analysisType != null && allTypesMap.containsValue(analysisType);
	}

	public void registerRunnableType(AnalysisType type) {
		runnableTypesMap.put(type.getType(), type);
		allTypesMap.put(type.getType(), type);
	}

	public void registerRunnableType(AnalysisType type, String viewer) {
		registerRunnableType(type);
		viewers.put(type, viewer);
	}

	public void registerUnrunnableType(AnalysisType type) {
		allTypesMap.put(type.getType(), type);
	}

	public Optional<String> getViewerForAnalysisType(AnalysisType analysisType) {
		if (viewers.containsKey(analysisType)) {
			return Optional.of(viewers.get(analysisType));
		}
		return Optional.empty();
	}

	public void registerDefaultTypes() {
		registerRunnableType(BuiltInAnalysisTypes.PHYLOGENOMICS);
		registerRunnableType(BuiltInAnalysisTypes.SISTR_TYPING);
		registerRunnableType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
		registerRunnableType(BuiltInAnalysisTypes.BIO_HANSEL);
		registerRunnableType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION);
		registerRunnableType(BuiltInAnalysisTypes.REFSEQ_MASHER);
		registerRunnableType(BuiltInAnalysisTypes.MLST_MENTALIST);

		registerUnrunnableType(BuiltInAnalysisTypes.FASTQC);
		registerUnrunnableType(BuiltInAnalysisTypes.DEFAULT);
	}

}
