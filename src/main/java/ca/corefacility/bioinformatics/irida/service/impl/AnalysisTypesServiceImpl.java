package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.*;

import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;

import com.google.common.collect.Sets;

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

	/**
	 * {@inheritDoc}
	 */
	public void registerRunnableType(AnalysisType type) {
		runnableTypesMap.put(type.getType(), type);
		allTypesMap.put(type.getType(), type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerRunnableType(AnalysisType type, String viewer) {
		registerRunnableType(type);
		viewers.put(type, viewer);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerUnrunnableType(AnalysisType type) {
		allTypesMap.put(type.getType(), type);
	}

	/**
	 * {@inheritDoc}
	 */
	public Optional<String> getViewerForAnalysisType(AnalysisType analysisType) {
		if (viewers.containsKey(analysisType)) {
			return Optional.of(viewers.get(analysisType));
		}
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerDefaultTypes() {
		registerRunnableType(BuiltInAnalysisTypes.PHYLOGENOMICS, "tree");
		registerRunnableType(BuiltInAnalysisTypes.SISTR_TYPING, "sistr");
		registerRunnableType(BuiltInAnalysisTypes.PHANTASTIC_TYPING, "tree");
		registerRunnableType(BuiltInAnalysisTypes.RECOVERY_TYPING);
		registerRunnableType(BuiltInAnalysisTypes.ALLELE_OBSERVER, "tree");
		registerRunnableType(BuiltInAnalysisTypes.SNP_OBSERVER, "tree");
		registerRunnableType(BuiltInAnalysisTypes.VIRULOTYPER);
		registerRunnableType(BuiltInAnalysisTypes.SUMMARY);
		registerRunnableType(BuiltInAnalysisTypes.META_EXPORT);
		registerRunnableType(BuiltInAnalysisTypes.GISAID);
		registerRunnableType(BuiltInAnalysisTypes.CONSENSUS);
		registerRunnableType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
		registerRunnableType(BuiltInAnalysisTypes.BIO_HANSEL, "biohansel");
		registerRunnableType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION);
		registerRunnableType(BuiltInAnalysisTypes.REFSEQ_MASHER);
		registerRunnableType(BuiltInAnalysisTypes.MLST_MENTALIST, "tree");

		registerUnrunnableType(BuiltInAnalysisTypes.FASTQC);
		registerUnrunnableType(BuiltInAnalysisTypes.DEFAULT);
	}

}
