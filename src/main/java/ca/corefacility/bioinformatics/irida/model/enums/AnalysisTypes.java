package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class AnalysisTypes {

	public static final AnalysisType PHYLOGENOMICS = new AnalysisType("PHYLOGENOMICS");

	public static final AnalysisType SISTR_TYPING = new AnalysisType("SISTR_TYPING");
	
	public static final AnalysisType ASSEMBLY_ANNOTATION = new AnalysisType("ASSEMBLY_ANNOTATION");

	public static final AnalysisType BIO_HANSEL = new AnalysisType("BIO_HANSEL");

	public static final AnalysisType ASSEMBLY_ANNOTATION_COLLECTION = new AnalysisType("ASSEMBLY_ANNOTATION_COLLECTION");

	public static final AnalysisType REFSEQ_MASHER = new AnalysisType("REFSEQ_MASHER");
	
	public static final AnalysisType FASTQC = new AnalysisType("FASTQC");

	public static final AnalysisType MLST_MENTALIST = new AnalysisType("MLST_MENTALIST");

	public static final AnalysisType DEFAULT = new AnalysisType("DEFAULT");

	public static Set<AnalysisType> executableAnalysisTypes() {
		return Sets.newHashSet(PHYLOGENOMICS, SISTR_TYPING, ASSEMBLY_ANNOTATION, BIO_HANSEL, ASSEMBLY_ANNOTATION_COLLECTION, REFSEQ_MASHER, MLST_MENTALIST);
	}
	
	private static Map<String, AnalysisType> allTypesMap = ImmutableMap.<String, AnalysisType>builder()
			.put(PHYLOGENOMICS.getName(), PHYLOGENOMICS)
			.put(SISTR_TYPING.getName(), SISTR_TYPING)
			.put(ASSEMBLY_ANNOTATION.getName(), ASSEMBLY_ANNOTATION)
			.put(BIO_HANSEL.getName(), BIO_HANSEL)
			.put(ASSEMBLY_ANNOTATION_COLLECTION.getName(), ASSEMBLY_ANNOTATION_COLLECTION)
			.put(REFSEQ_MASHER.getName(), REFSEQ_MASHER)
			.put(FASTQC.getName(), FASTQC)
			.put(MLST_MENTALIST.getName(), MLST_MENTALIST)
			.put(DEFAULT.getName(), DEFAULT)
			.build();
	
	private static Map<String, AnalysisType> runnableTypesMap = ImmutableMap.<String, AnalysisType>builder()
			.put(PHYLOGENOMICS.getName(), PHYLOGENOMICS)
			.put(SISTR_TYPING.getName(), SISTR_TYPING)
			.put(ASSEMBLY_ANNOTATION.getName(), ASSEMBLY_ANNOTATION)
			.put(BIO_HANSEL.getName(), BIO_HANSEL)
			.put(ASSEMBLY_ANNOTATION_COLLECTION.getName(), ASSEMBLY_ANNOTATION_COLLECTION)
			.put(REFSEQ_MASHER.getName(), REFSEQ_MASHER)
			.put(MLST_MENTALIST.getName(), MLST_MENTALIST)
			.build();

	public static AnalysisType fromString(String string) {
		return allTypesMap.get(string);
	}

	public static Collection<AnalysisType> values() {
		return runnableTypesMap.values();
	}
}
