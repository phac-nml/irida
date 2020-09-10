package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

/**
 * UI Response for successfully creating an item.  Since all items in IRIDA require an id,
 * the identifier is returned to the client on successful creation.
 */
public class AjaxCreateItemSuccessResponse extends AjaxResponse {
	private final long id;

	public AjaxCreateItemSuccessResponse(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}
