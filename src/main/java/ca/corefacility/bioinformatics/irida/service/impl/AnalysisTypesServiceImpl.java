package ca.corefacility.bioinformatics.irida.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;

/**
 * A service for managing registered {@link AnalysisType}s.
 *
 */
@Component
public class AnalysisTypesServiceImpl implements AnalysisTypesService {

	private Map<String, AnalysisType> allTypesMap;

	private Map<String, AnalysisType> runnableTypesMap;

	/**
	 * Builds a new {@link AnalysisTypesServiceImpl}.
	 * 
	 * @param runnableAnalysisTypes A {@link Set} of {@link AnalysisType}s that can
	 *                              be run as pipelines.
	 * @param otherAnalysisTypes    A {@link Set} of {@link AnalysisType}s that
	 *                              should exist in IRIDA but can't be run as
	 *                              pipelines.
	 */
	public AnalysisTypesServiceImpl(Set<AnalysisType> runnableAnalysisTypes, Set<AnalysisType> otherAnalysisTypes) {
		checkNotNull(runnableAnalysisTypes, "runnableAnalysisTypes is null");
		checkNotNull(otherAnalysisTypes, "otherAnalysisTypes is null");
		
		allTypesMap = new HashMap<>();
		runnableTypesMap = new HashMap<>();

		for (AnalysisType type : runnableAnalysisTypes) {
			allTypesMap.put(type.getType(), type);
			runnableTypesMap.put(type.getType(), type);
		}

		for (AnalysisType type : otherAnalysisTypes) {
			if (allTypesMap.containsKey(type.getType())) {
				throw new IllegalArgumentException("Error, set otherAnalysisTypes contains type " + type
						+ " already found in runnableAnalysisTypes");
			}
			allTypesMap.put(type.getType(), type);
		}
	}

	/**
	 * Builds a new default {@link AnalysisTypesServiceImpl}.
	 */
	public AnalysisTypesServiceImpl() {
		allTypesMap = new HashMap<>();
		runnableTypesMap = new HashMap<>();

		allTypesMap.put(BuiltInAnalysisTypes.PHYLOGENOMICS.getType(), BuiltInAnalysisTypes.PHYLOGENOMICS);
		allTypesMap.put(BuiltInAnalysisTypes.SISTR_TYPING.getType(), BuiltInAnalysisTypes.SISTR_TYPING);
		allTypesMap.put(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION.getType(), BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
		allTypesMap.put(BuiltInAnalysisTypes.BIO_HANSEL.getType(), BuiltInAnalysisTypes.BIO_HANSEL);
		allTypesMap.put(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION.getType(),
				BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION);
		allTypesMap.put(BuiltInAnalysisTypes.REFSEQ_MASHER.getType(), BuiltInAnalysisTypes.REFSEQ_MASHER);
		allTypesMap.put(BuiltInAnalysisTypes.FASTQC.getType(), BuiltInAnalysisTypes.FASTQC);
		allTypesMap.put(BuiltInAnalysisTypes.MLST_MENTALIST.getType(), BuiltInAnalysisTypes.MLST_MENTALIST);
		allTypesMap.put(BuiltInAnalysisTypes.DEFAULT.getType(), BuiltInAnalysisTypes.DEFAULT);

		runnableTypesMap.put(BuiltInAnalysisTypes.PHYLOGENOMICS.getType(), BuiltInAnalysisTypes.PHYLOGENOMICS);
		runnableTypesMap.put(BuiltInAnalysisTypes.SISTR_TYPING.getType(), BuiltInAnalysisTypes.SISTR_TYPING);
		runnableTypesMap.put(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION.getType(), BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
		runnableTypesMap.put(BuiltInAnalysisTypes.BIO_HANSEL.getType(), BuiltInAnalysisTypes.BIO_HANSEL);
		runnableTypesMap.put(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION.getType(),
				BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION);
		runnableTypesMap.put(BuiltInAnalysisTypes.REFSEQ_MASHER.getType(), BuiltInAnalysisTypes.REFSEQ_MASHER);
		runnableTypesMap.put(BuiltInAnalysisTypes.MLST_MENTALIST.getType(), BuiltInAnalysisTypes.MLST_MENTALIST);
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
}
