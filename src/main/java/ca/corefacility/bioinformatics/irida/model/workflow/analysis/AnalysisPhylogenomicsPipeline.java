package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Metadata for Core SNP Pipeline implementation in Galaxy.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis_phylogenomicspipeline")
@Audited
public class AnalysisPhylogenomicsPipeline extends Analysis {

	// newick formatted phylogenetic tree file
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private AnalysisOutputFile phylogeneticTree;

	// SNP matrix file
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private AnalysisOutputFile snpMatrix;

	// SNP table file
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private AnalysisOutputFile snpTable;

	/**
	 * required for hibernate, marked as private so nobody else uses it.
	 */
	private AnalysisPhylogenomicsPipeline() {
		super(null, null);
	}

	public AnalysisPhylogenomicsPipeline(Set<SequenceFile> inputFiles, String executionManagerAnalysisId) {
		super(inputFiles, executionManagerAnalysisId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<AnalysisOutputFile> getAnalysisOutputFiles() {
		return Sets.newHashSet(phylogeneticTree, snpMatrix, snpTable);
	}

	public AnalysisOutputFile getPhylogeneticTree() {
		return phylogeneticTree;
	}

	public void setPhylogeneticTree(AnalysisOutputFile phylogeneticTree) {
		this.phylogeneticTree = phylogeneticTree;
	}

	public AnalysisOutputFile getSnpMatrix() {
		return snpMatrix;
	}

	public void setSnpMatrix(AnalysisOutputFile snpMatrix) {
		this.snpMatrix = snpMatrix;
	}

	public AnalysisOutputFile getSnpTable() {
		return snpTable;
	}

	public void setSnpTable(AnalysisOutputFile snpTable) {
		this.snpTable = snpTable;
	}
}
