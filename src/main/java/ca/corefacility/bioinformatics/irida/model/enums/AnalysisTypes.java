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
	
	private static Map<String, AnalysisType> typesMap = ImmutableMap.<String, AnalysisType>builder()
			.put("phylogenomics", PHYLOGENOMICS)
			.put("sistr-typing", SISTR_TYPING)
			.put("assembly-annotation", ASSEMBLY_ANNOTATION)
			.put("bio_hansel", BIO_HANSEL)
			.put("assembly-annotation-collection", ASSEMBLY_ANNOTATION_COLLECTION)
			.put("refseq-masher", REFSEQ_MASHER)
			.put("fastqc", FASTQC)
			.put("mlst-mentalist", MLST_MENTALIST)
			.put("default", DEFAULT)
			.build();

	public static AnalysisType fromString(String string) {
		return typesMap.get(string);
	}

	public static Collection<AnalysisType> values() {
		return typesMap.values();
	}
}
