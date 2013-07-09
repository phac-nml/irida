package ca.corefacility.bioinformatics.irida.web.controller.api.links;

import org.springframework.hateoas.Link;

/**
 * A specialized version of {@link Link} that exposes some predetermined keys for paging data. Notably, we want "prev"
 * instead of "previous".
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class PageLink extends Link {

	private static final long serialVersionUID = -5737013318572835160L;
	/**
     * link rel for the previous page
     */
    public static final String REL_PREV = "prev";
}
