package ca.corefacility.bioinformatics.irida.model.workflow.analysis.type;

/**
 * Stores a number of constants for individual {@link AnalysisType}s.
 *
 */
public class BuiltInAnalysisTypes {

	public static final AnalysisType PHYLOGENOMICS = new AnalysisType("PHYLOGENOMICS");

	public static final AnalysisType SISTR_TYPING = new AnalysisType("SISTR_TYPING");

	public static final AnalysisType PHANTASTIC_TYPING = new AnalysisType("PHANTASTIC_TYPING");

	public static final AnalysisType RECOVERY_TYPING = new AnalysisType("RECOVERY_TYPING");

	public static final AnalysisType ALLELE_OBSERVER = new AnalysisType("ALLELE_OBSERVER");

	public static final AnalysisType SNP_OBSERVER = new AnalysisType("SNP_OBSERVER");

	public static final AnalysisType VIRULOTYPER = new AnalysisType("VIRULOTYPER");

	public static final AnalysisType SUMMARY = new AnalysisType("SUMMARY");

	public static final AnalysisType META_EXPORT = new AnalysisType("META_EXPORT");

	public static final AnalysisType GISAID = new AnalysisType("GISAID");

	public static final AnalysisType CONSENSUS = new AnalysisType("CONSENSUS");

	public static final AnalysisType ASSEMBLY_ANNOTATION = new AnalysisType("ASSEMBLY_ANNOTATION");

	public static final AnalysisType BIO_HANSEL = new AnalysisType("BIO_HANSEL");

	public static final AnalysisType ASSEMBLY_ANNOTATION_COLLECTION = new AnalysisType("ASSEMBLY_ANNOTATION_COLLECTION");

	public static final AnalysisType REFSEQ_MASHER = new AnalysisType("REFSEQ_MASHER");

	public static final AnalysisType FASTQC = new AnalysisType("FASTQC");

	public static final AnalysisType MLST_MENTALIST = new AnalysisType("MLST_MENTALIST");

	public static final AnalysisType DEFAULT = new AnalysisType("DEFAULT");

	public static final AnalysisType UNKNOWN = new AnalysisType("UNKNOWN");
}
