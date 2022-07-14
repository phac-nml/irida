package ca.corefacility.bioinformatics.irida.ria.web.models.export;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.export.NcbiInstrumentModel;
import ca.corefacility.bioinformatics.irida.model.export.NcbiLibrarySelection;
import ca.corefacility.bioinformatics.irida.model.export.NcbiLibrarySource;
import ca.corefacility.bioinformatics.irida.model.export.NcbiLibraryStrategy;

/**
 * Represents a sample in a {@link NcbiSubmissionRequest}
 */
public class NcbiSubmissionSample {
	private String bioSample;
	private String libraryName;
	private NcbiLibrarySelection LibrarySelection;
	private NcbiLibrarySource librarySource;
	private NcbiLibraryStrategy libraryStrategy;
	private String libraryConstructionProtocol;
	private NcbiInstrumentModel instrumentModel;
	private List<Long> pairs;
	private List<Long> singles;

	public String getBioSample() {
		return bioSample;
	}

	public void setBioSample(String bioSample) {
		this.bioSample = bioSample;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public NcbiLibrarySelection getLibrarySelection() {
		return LibrarySelection;
	}

	public void setLibrarySelection(NcbiLibrarySelection librarySelection) {
		LibrarySelection = librarySelection;
	}

	public NcbiLibrarySource getLibrarySource() {
		return librarySource;
	}

	public void setLibrarySource(NcbiLibrarySource librarySource) {
		this.librarySource = librarySource;
	}

	public NcbiLibraryStrategy getLibraryStrategy() {
		return libraryStrategy;
	}

	public void setLibraryStrategy(NcbiLibraryStrategy libraryStrategy) {
		this.libraryStrategy = libraryStrategy;
	}

	public String getLibraryConstructionProtocol() {
		return libraryConstructionProtocol;
	}

	public void setLibraryConstructionProtocol(String libraryConstructionProtocol) {
		this.libraryConstructionProtocol = libraryConstructionProtocol;
	}

	public NcbiInstrumentModel getInstrumentModel() {
		return instrumentModel;
	}

	public void setInstrumentModel(NcbiInstrumentModel instrumentModel) {
		this.instrumentModel = instrumentModel;
	}

	public List<Long> getPairs() {
		return pairs;
	}

	public void setPairs(List<Long> pairs) {
		this.pairs = pairs;
	}

	public List<Long> getSingles() {
		return singles;
	}

	public void setSingles(List<Long> singles) {
		this.singles = singles;
	}
}
