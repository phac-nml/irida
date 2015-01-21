package ca.corefacility.bioinformatics.irida.ria.web.components;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public class SubmissionIds {
	List<Long> ids;

	public SubmissionIds() {
		ids = new ArrayList<>();
	}

	public void addId(Long id) {
		ids.add(id);
	}
}
