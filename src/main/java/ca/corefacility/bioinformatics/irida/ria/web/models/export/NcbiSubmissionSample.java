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
	private NcbiLibrarySelection librarySelection;
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
		return librarySelection;
	}

	public void setLibrarySelection(String librarySelection) {
		this.librarySelection = NcbiLibrarySelection.valueOf(librarySelection);
	}

	public NcbiLibrarySource getLibrarySource() {
		return librarySource;
	}

	public void setLibrarySource(String librarySource) {
		this.librarySource = NcbiLibrarySource.valueOf(librarySource);
	}

	public NcbiLibraryStrategy getLibraryStrategy() {
		return libraryStrategy;
	}

	public void setLibraryStrategy(String libraryStrategy) {
		this.libraryStrategy = NcbiLibraryStrategy.valueOf(libraryStrategy);
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

	public void setInstrumentModel(String instrumentModel) {
		this.instrumentModel = NcbiInstrumentModel.valueOf(instrumentModel);
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
