package ca.corefacility.bioinformatics.irida.model.export;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * {@link SequenceFile}s and {@link SequenceFilePair}s associated with a
 * {@link NcbiExportSubmission}.
 * 
 * @see NcbiExportSubmission
 */
@Entity
@Table(name = "ncbi_export_biosample")
public class NcbiBioSampleFiles {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String bioSample;

	@ManyToMany
	private List<SequenceFile> files;

	@ManyToMany
	private List<SequenceFilePair> pairs;

	public NcbiBioSampleFiles(String bioSample) {
		this.bioSample = bioSample;
	}

	public NcbiBioSampleFiles(String bioSample, List<SequenceFile> files, List<SequenceFilePair> pairs) {
		this(bioSample);
		this.files = files;
		this.pairs = pairs;
	}

	public String getBioSample() {
		return bioSample;
	}

	public List<SequenceFile> getFiles() {
		return files;
	}

	public Long getId() {
		return id;
	}

	public List<SequenceFilePair> getPairs() {
		return pairs;
	}

	public void setFiles(List<SequenceFile> files) {
		this.files = files;
	}

	public void setPairs(List<SequenceFilePair> pairs) {
		this.pairs = pairs;
	}
}
