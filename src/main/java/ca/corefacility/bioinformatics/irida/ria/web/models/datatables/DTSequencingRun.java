package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

import java.util.Date;

/**
 * Datatables response class for {@link SequencingRun}
 */
public class DTSequencingRun implements DataTablesResponseModel {

	private SequencingRun sequencingRun;
	private String statusMessage;

	public DTSequencingRun(SequencingRun sequencingRun, String statusMessage) {
		this.sequencingRun = sequencingRun;
		this.statusMessage = statusMessage;
	}

	public Long getId() {
		return sequencingRun.getId();
	}

	public String getSequencerType() {
		return sequencingRun.getSequencerType();
	}

	public String getUploadStatus() {
		return statusMessage;
	}

	public User getUser() {
		return sequencingRun.getUser();
	}

	public Date getCreatedDate() {
		return sequencingRun.getCreatedDate();
	}
}
