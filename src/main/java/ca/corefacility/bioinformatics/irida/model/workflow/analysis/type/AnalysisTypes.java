package ca.corefacility.bioinformatics.irida.model.workflow.analysis.type;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Stores a number of constants for individual {@link AnalysisType}s.
 *
 */
public class AnalysisTypes {

	public static final AnalysisType PHYLOGENOMICS = new AnalysisType("PHYLOGENOMICS");

	public static final AnalysisType SISTR_TYPING = new AnalysisType("SISTR_TYPING");

	public static final AnalysisType ASSEMBLY_ANNOTATION = new AnalysisType("ASSEMBLY_ANNOTATION");

	public static final AnalysisType BIO_HANSEL = new AnalysisType("BIO_HANSEL");

	public static final AnalysisType ASSEMBLY_ANNOTATION_COLLECTION = new AnalysisType(
			"ASSEMBLY_ANNOTATION_COLLECTION");

	public static final AnalysisType REFSEQ_MASHER = new AnalysisType("REFSEQ_MASHER");

	public static final AnalysisType FASTQC = new AnalysisType("FASTQC");

	public static final AnalysisType MLST_MENTALIST = new AnalysisType("MLST_MENTALIST");

	public static final AnalysisType DEFAULT = new AnalysisType("DEFAULT");
	
	// @formatter:off
	private static Map<String, AnalysisType> allTypesMap = ImmutableMap.<String, AnalysisType>builder()
			.put(PHYLOGENOMICS.getType(), PHYLOGENOMICS)
			.put(SISTR_TYPING.getType(), SISTR_TYPING)
			.put(ASSEMBLY_ANNOTATION.getType(), ASSEMBLY_ANNOTATION)
			.put(BIO_HANSEL.getType(), BIO_HANSEL)
			.put(ASSEMBLY_ANNOTATION_COLLECTION.getType(), ASSEMBLY_ANNOTATION_COLLECTION)
			.put(REFSEQ_MASHER.getType(), REFSEQ_MASHER)
			.put(FASTQC.getType(), FASTQC)
			.put(MLST_MENTALIST.getType(), MLST_MENTALIST)
			.put(DEFAULT.getType(), DEFAULT)
			.build();
	// @formatter:on
	
	// @formatter:off
	private static Map<String, AnalysisType> runnableTypesMap = ImmutableMap.<String, AnalysisType>builder()
			.put(PHYLOGENOMICS.getType(), PHYLOGENOMICS)
			.put(SISTR_TYPING.getType(), SISTR_TYPING)
			.put(ASSEMBLY_ANNOTATION.getType(), ASSEMBLY_ANNOTATION)
			.put(BIO_HANSEL.getType(), BIO_HANSEL)
			.put(ASSEMBLY_ANNOTATION_COLLECTION.getType(), ASSEMBLY_ANNOTATION_COLLECTION)
			.put(REFSEQ_MASHER.getType(), REFSEQ_MASHER)
			.put(MLST_MENTALIST.getType(), MLST_MENTALIST)
			.build();
	// @formatter:on

	/**
	 * Gets all executable {@link AnalysisType}s.
	 * 
	 * @return All executable {@link AnalysisType}s.
	 */
	public static Set<AnalysisType> executableAnalysisTypes() {
		return Sets.newHashSet(PHYLOGENOMICS, SISTR_TYPING, ASSEMBLY_ANNOTATION, BIO_HANSEL,
				ASSEMBLY_ANNOTATION_COLLECTION, REFSEQ_MASHER, MLST_MENTALIST);
	}

	/**
	 * Gets a {@link AnalysisType} from the given string.
	 * 
	 * @param string The string to match to the {@link AnalysisType}.
	 * @return The particular {@link AnalysisType}.
	 */
	public static AnalysisType fromString(String string) {
		return allTypesMap.get(string);
	}

	/**
	 * Gets all {@link AnalysisType}s as a {@link Collection}.
	 * 
	 * @return All {@link AnalysisType}s as a {@link Collection}.
	 */
	public static Collection<AnalysisType> values() {
		return runnableTypesMap.values();
	}

	/**
	 * Whether or not this analysis type is valid (has been registered).
	 * @param analysisType The {@link AnalysisType}.
	 * @return True if valid, false otherwise.
	 */
	public static boolean isValid(AnalysisType analysisType) {
		return analysisType != null && allTypesMap.containsValue(analysisType);
	}
}
