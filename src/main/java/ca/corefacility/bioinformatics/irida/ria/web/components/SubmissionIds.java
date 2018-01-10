package ca.corefacility.bioinformatics.irida.ria.web.components;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store workflow submission ids in the session.  This should be temporary until
 * they are stored in the database.  Storing a list in a class seems to be handles better by Spring.
 *
 */
public class SubmissionIds {
	List<Long> ids;

	public SubmissionIds() {
		ids = new ArrayList<>();
	}

	/**
	 * add an id to the submission ids
	 *
	 * @param id the id to add
	 */
	public void addId(Long id) {
		ids.add(id);
	}
}
