package ca.corefacility.bioinformatics.irida.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisTypes;
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

		allTypesMap.put(AnalysisTypes.PHYLOGENOMICS.getType(), AnalysisTypes.PHYLOGENOMICS);
		allTypesMap.put(AnalysisTypes.SISTR_TYPING.getType(), AnalysisTypes.SISTR_TYPING);
		allTypesMap.put(AnalysisTypes.ASSEMBLY_ANNOTATION.getType(), AnalysisTypes.ASSEMBLY_ANNOTATION);
		allTypesMap.put(AnalysisTypes.BIO_HANSEL.getType(), AnalysisTypes.BIO_HANSEL);
		allTypesMap.put(AnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION.getType(),
				AnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION);
		allTypesMap.put(AnalysisTypes.REFSEQ_MASHER.getType(), AnalysisTypes.REFSEQ_MASHER);
		allTypesMap.put(AnalysisTypes.FASTQC.getType(), AnalysisTypes.FASTQC);
		allTypesMap.put(AnalysisTypes.MLST_MENTALIST.getType(), AnalysisTypes.MLST_MENTALIST);
		allTypesMap.put(AnalysisTypes.DEFAULT.getType(), AnalysisTypes.DEFAULT);

		runnableTypesMap.put(AnalysisTypes.PHYLOGENOMICS.getType(), AnalysisTypes.PHYLOGENOMICS);
		runnableTypesMap.put(AnalysisTypes.SISTR_TYPING.getType(), AnalysisTypes.SISTR_TYPING);
		runnableTypesMap.put(AnalysisTypes.ASSEMBLY_ANNOTATION.getType(), AnalysisTypes.ASSEMBLY_ANNOTATION);
		runnableTypesMap.put(AnalysisTypes.BIO_HANSEL.getType(), AnalysisTypes.BIO_HANSEL);
		runnableTypesMap.put(AnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION.getType(),
				AnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION);
		runnableTypesMap.put(AnalysisTypes.REFSEQ_MASHER.getType(), AnalysisTypes.REFSEQ_MASHER);
		runnableTypesMap.put(AnalysisTypes.MLST_MENTALIST.getType(), AnalysisTypes.MLST_MENTALIST);
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
