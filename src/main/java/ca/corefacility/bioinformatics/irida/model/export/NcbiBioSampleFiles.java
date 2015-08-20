package ca.corefacility.bioinformatics.irida.model.export;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
	private String id;

	private String bioSample;

	@ManyToMany(fetch = FetchType.EAGER)
	private List<SequenceFile> files;

	@ManyToMany(fetch = FetchType.EAGER)
	private List<SequenceFilePair> pairs;

	@Enumerated(EnumType.STRING)
	@Column(name = "instrument_model")
	private NcbiInstrumentModel instrumentModel;

	@Column(name="library_name")
	private String libraryName;

	@Enumerated(EnumType.STRING)
	@Column(name = "library_selection")
	private NcbiLibrarySelection librarySelection;

	@Enumerated(EnumType.STRING)
	@Column(name = "library_source")
	private NcbiLibrarySource librarySource;

	@Enumerated(EnumType.STRING)
	@Column(name = "library_strategy")
	private NcbiLibraryStrategy libraryStrategy;

	@Column(name = "library_construction_protocol")
	private String libraryConstructionProtocol;

	public NcbiBioSampleFiles() {
	}

	public NcbiBioSampleFiles(String namespace) {
		// Creating a new SRA upload identifier using the current time
		StringBuilder builder = new StringBuilder(namespace);
		builder.append(new Date().getTime());
		id = builder.toString();

	}

	public NcbiBioSampleFiles(String bioSample, List<SequenceFile> files, List<SequenceFilePair> pairs,
			NcbiInstrumentModel instrument_model, String library_name, NcbiLibrarySelection library_selection,
			NcbiLibrarySource library_source, NcbiLibraryStrategy library_strategy,
			String library_construction_protocol, String namespace) {
		this(namespace);
		this.bioSample = bioSample;
		this.files = files;
		this.pairs = pairs;
		this.instrumentModel = instrument_model;
		this.libraryName = library_name;
		this.librarySelection = library_selection;
		this.librarySource = library_source;
		this.libraryStrategy = library_strategy;
		this.libraryConstructionProtocol = library_construction_protocol;
	}

	public static class Builder {
		private String bioSample;

		private List<SequenceFile> files;
		private List<SequenceFilePair> pairs;
		private NcbiInstrumentModel instrumentModel;
		private String libraryName;
		private NcbiLibrarySelection librarySelection;
		private NcbiLibrarySource librarySource;
		private NcbiLibraryStrategy libraryStrategy;
		private String libraryConstructionProtocol;
		private String namespace;

		public Builder files(List<SequenceFile> files) {
			this.files = files;
			return this;
		}

		public Builder pairs(List<SequenceFilePair> pairs) {
			this.pairs = pairs;
			return this;
		}

		/**
		 * Sequencer model that created these files
		 * 
		 * @param instrument_model
		 *            {@link NcbiInstrumentModel} instance
		 * @return {@link Builder}
		 */
		public Builder instrumentModel(NcbiInstrumentModel instrument_model) {
			this.instrumentModel = instrument_model;
			return this;
		}

		/**
		 * Name of the library for these files
		 * 
		 * @param library_name
		 *            String name
		 * @return {@link Builder}
		 */
		public Builder libraryName(String library_name) {
			this.libraryName = library_name;
			return this;
		}

		/**
		 * Method used to select the library for these files
		 * 
		 * @param library_selection
		 *            {@link NcbiLibrarySelection}
		 * @return {@link Builder}
		 */
		public Builder librarySelection(NcbiLibrarySelection library_selection) {
			this.librarySelection = library_selection;
			return this;
		}

		public Builder librarySource(NcbiLibrarySource library_source) {
			this.librarySource = library_source;
			return this;
		}

		/**
		 * Strategy used for generating this library
		 * 
		 * @param library_strategy
		 *            {@link NcbiLibraryStrategy}
		 * @return {@link Builder}
		 */
		public Builder libraryStrategy(NcbiLibraryStrategy library_strategy) {
			this.libraryStrategy = library_strategy;
			return this;
		}

		/**
		 * String describing the library construction protocol
		 * 
		 * @param library_construction_protocol
		 *            String protocol description
		 * @return {@link Builder}
		 */
		public Builder libraryConstructionProtocol(String library_construction_protocol) {
			this.libraryConstructionProtocol = library_construction_protocol;
			return this;
		}

		/**
		 * BioSample identifier for these files
		 * 
		 * @param bioSample
		 *            String BioSample id
		 * @return {@link Builder}
		 */
		public Builder bioSample(String bioSample) {
			this.bioSample = bioSample;
			return this;
		}

		/**
		 * Namespace for generating the submission identifier
		 * 
		 * @param namespace
		 *            String namespace
		 * @return {@link Builder}
		 */
		public Builder namespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		/**
		 * Build an instance of {@link NcbiBioSampleFiles}
		 * 
		 * @return Newly constructd {@link NcbiBioSampleFiles}
		 */
		public NcbiBioSampleFiles build() {
			return new NcbiBioSampleFiles(bioSample, files, pairs, instrumentModel, libraryName, librarySelection,
					librarySource, libraryStrategy, libraryConstructionProtocol, namespace);
		}
	}

	public String getBioSample() {
		return bioSample;
	}

	public List<SequenceFile> getFiles() {
		return files;
	}

	public String getId() {
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

	public NcbiInstrumentModel getInstrumentModel() {
		return instrumentModel;
	}

	public String getLibraryConstructionProtocol() {
		return libraryConstructionProtocol;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public NcbiLibrarySelection getLibrarySelection() {
		return librarySelection;
	}

	public NcbiLibrarySource getLibrarySource() {
		return librarySource;
	}

	public NcbiLibraryStrategy getLibraryStrategy() {
		return libraryStrategy;
	}
}
