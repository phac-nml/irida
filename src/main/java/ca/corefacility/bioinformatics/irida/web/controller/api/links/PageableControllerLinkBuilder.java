package ca.corefacility.bioinformatics.irida.web.controller.api.links;

import ca.corefacility.bioinformatics.irida.model.enums.Order;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that enables adding page links to a resource.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public abstract class PageableControllerLinkBuilder {
    /**
     * the request parameter for specifying the current page.
     */
    public static final String REQUEST_PARAM_PAGE = "page";
    /**
     * the request parameter for specifying the size of the current page.
     */
    public static final String REQUEST_PARAM_SIZE = "size";
    /**
     * the request parameter for specifying the sort property.
     */
    public static final String REQUEST_PARAM_SORT_PROPERTY = "sortProperty";
    /**
     * the request parameter for specifying the sort order.
     */
    public static final String REQUEST_PARAM_SORT_ORDER = "sortOrder";
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PageableControllerLinkBuilder.class);

    /**
     * Get a collection of page {@link Link} objects to add to a collection of resources. Page rels are named according to RFC5005.
     *
     * @param controller    the controller that you want to generate the links for.
     * @param page          the current page.
     * @param size          the current size of requested resources.
     * @param totalElements the total number of elements.
     * @param sortColumn    the column that should be used to sort the resources.
     * @param sortOrder     the order that the column should be sorted on.
     * @return A collection of links to assist with page navigation.
     */
    public static Iterable<Link> pageLinksFor(Class<?> controller, int page, int size, int totalElements, String sortColumn, Order sortOrder) {
        // we're only ever going to have 5-at-most links per page.
        List<Link> links = new ArrayList<>(5);

        // the base url for all of the links on the page should start with the link to the controller.
        String baseUrl = ControllerLinkBuilder.linkTo(controller).withSelfRel().getHref();

        // the last page of the collection is the ceiling of the total number of elements divided by the page size
        int lastPage = (int) Math.ceil(totalElements / (size * 1.));
        // the next page is the current page + 1, unless you're already on the last page
        int nextPage = page == lastPage ? lastPage : page + 1;
        // the previous page is the current page - 1, unless you're on the first page
        int prevPage = page > 2 ? page - 1 : 1;
        // the first page is always 1 (duh)
        int firstPage = 1;
        logger.debug("page: [" + page + "], size: [" + size + "], totalElements [" + totalElements + "]");
        logger.debug(
                "firstPage: [" + firstPage + "], prevPage [" + prevPage + "], nextPage [" + nextPage + "], lastPage: [" + lastPage + "]");

        // if the baseUrl doesn't have a question mark at the end, we need to add it so we can add the page parameters
        if (!baseUrl.endsWith("?")) {
            baseUrl = baseUrl + "?";
        }

        // add all of the links to the collection
        links.add(new Link(baseUrl + pageParams(firstPage, size, sortColumn, sortOrder), Link.REL_FIRST));

        // only add the previous page link if you're not on the first page
        if (page > 1) {
            links.add(new Link(baseUrl + pageParams(prevPage, size, sortColumn, sortOrder), Link.REL_PREVIOUS));
        }

        // only add the next page link if you're not on the last page
        if (page < lastPage) {
            links.add(new Link(baseUrl + pageParams(nextPage, size, sortColumn, sortOrder), Link.REL_NEXT));
        }

        // add the first and last page links
        links.add(new Link(baseUrl + pageParams(lastPage, size, sortColumn, sortOrder), Link.REL_LAST));
        links.add(new Link(baseUrl + pageParams(page, size, sortColumn, sortOrder), Link.REL_SELF));

        return links;
    }

    /**
     * Construct the paging parameter lists for appending to a URL.
     *
     * @param page       the page that the link should point to.
     * @param size       the size of the result set in the page.
     * @param sortColumn the column that the result set should be sorted by.
     * @param sortOrder  the order of the sort.
     * @return the parameter section of the URL.
     */
    private static String pageParams(int page, int size, String sortColumn, Order sortOrder) {
        StringBuilder sb = new StringBuilder();
        // add the page and size parameters to the url params
        sb.append(REQUEST_PARAM_PAGE).append("=").append(page).append("&");
        sb.append(REQUEST_PARAM_SIZE).append("=").append(size).append("&");

        // if a sort property is supplied, add that to the sort params
        if (!Strings.isNullOrEmpty(sortColumn)) {
            sb.append(REQUEST_PARAM_SORT_PROPERTY).append("=").append(sortColumn).append("&");
        }

        // if an order has been supplied, add that to the order params
        if (sortOrder != null) {
            sb.append(REQUEST_PARAM_SORT_ORDER).append("=").append(sortOrder);
        }
        return sb.toString();
    }
}
