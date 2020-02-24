package ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represents a {@link RemoteAPI} in the Remote API table.
 */
public class RemoteAPITableModel extends TableModel {
	public RemoteAPITableModel(RemoteAPI api) {
		super(api.getId(), api.getName(), api.getCreatedDate(), api.getModifiedDate());
	}
}
