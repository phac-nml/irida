package ca.corefacility.bioinformatics.irida.model.export;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String bioSample;

	@ManyToMany(fetch=FetchType.EAGER)
	private List<SequenceFile> files;

	@ManyToMany(fetch=FetchType.EAGER)
	private List<SequenceFilePair> pairs;
	
	@Enumerated(EnumType.STRING)
	NcbiInstrumentModel instrument_model;

	String library_name;

	@Enumerated(EnumType.STRING)
	NcbiLibrarySelection library_selection;

	@Enumerated(EnumType.STRING)
	NcbiLibrarySource library_source;

	@Enumerated(EnumType.STRING)
	NcbiLibraryStrategy library_strategy;

	String library_construction_protocol;
	
	public NcbiBioSampleFiles() {
	}

	public NcbiBioSampleFiles(String bioSample) {
		this.bioSample = bioSample;
	}

	public NcbiBioSampleFiles(String bioSample, List<SequenceFile> files, List<SequenceFilePair> pairs) {
		this(bioSample);
		this.files = files;
		this.pairs = pairs;
	}

	public NcbiBioSampleFiles(String bioSample, List<SequenceFile> files, List<SequenceFilePair> pairs,
			NcbiInstrumentModel instrument_model, String library_name, NcbiLibrarySelection library_selection,
			NcbiLibrarySource library_source, NcbiLibraryStrategy library_strategy,
			String library_construction_protocol) {
		this.bioSample = bioSample;
		this.files = files;
		this.pairs = pairs;
		this.instrument_model = instrument_model;
		this.library_name = library_name;
		this.library_selection = library_selection;
		this.library_source = library_source;
		this.library_strategy = library_strategy;
		this.library_construction_protocol = library_construction_protocol;
	}

	public static class Builder {
		private String bioSample;

		private List<SequenceFile> files;
		private List<SequenceFilePair> pairs;
		private NcbiInstrumentModel instrument_model;
		private String library_name;
		private NcbiLibrarySelection library_selection;
		private NcbiLibrarySource library_source;
		private NcbiLibraryStrategy library_strategy;
		private String library_construction_protocol;

		public Builder files(List<SequenceFile> files) {
			this.files = files;
			return this;
		}

		public Builder pairs(List<SequenceFilePair> pairs) {
			this.pairs = pairs;
			return this;
		}

		public Builder instrument_model(NcbiInstrumentModel instrument_model) {
			this.instrument_model = instrument_model;
			return this;
		}

		public Builder library_name(String library_name) {
			this.library_name = library_name;
			return this;
		}

		public Builder library_selection(NcbiLibrarySelection library_selection) {
			this.library_selection = library_selection;
			return this;
		}

		public Builder library_source(NcbiLibrarySource library_source) {
			this.library_source = library_source;
			return this;
		}

		public Builder library_strategy(NcbiLibraryStrategy library_strategy) {
			this.library_strategy = library_strategy;
			return this;
		}

		public Builder library_construction_protocol(String library_construction_protocol) {
			this.library_construction_protocol = library_construction_protocol;
			return this;
		}

		public Builder bioSample(String bioSample) {
			this.bioSample = bioSample;
			return this;
		}

		public NcbiBioSampleFiles build() {
			return new NcbiBioSampleFiles(bioSample, files, pairs, instrument_model, library_name, library_selection,
					library_source, library_strategy, library_construction_protocol);
		}
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

	public NcbiInstrumentModel getInstrument_model() {
		return instrument_model;
	}

	public void setInstrument_model(NcbiInstrumentModel instrument_model) {
		this.instrument_model = instrument_model;
	}

	public String getLibrary_construction_protocol() {
		return library_construction_protocol;
	}

	public String getLibrary_name() {
		return library_name;
	}

	public NcbiLibrarySelection getLibrary_selection() {
		return library_selection;
	}

	public NcbiLibrarySource getLibrary_source() {
		return library_source;
	}

	public NcbiLibraryStrategy getLibrary_strategy() {
		return library_strategy;
	}
}
