/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.web.controller.links;

import ca.corefacility.bioinformatics.irida.model.enums.Order;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

/**
 * A class that enables adding page links to a resource.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public abstract class PageableControllerLinkBuilder {

    public static final String REQUEST_PARAM_PAGE = "page";
    public static final String REQUEST_PARAM_SIZE = "size";
    public static final String REQUEST_PARAM_SORT_PROPERTY = "sortProperty";
    public static final String REQUEST_PARAM_SORT_ORDER = "sortOrder";
    private static final Logger logger = LoggerFactory.getLogger(PageableControllerLinkBuilder.class);

    /**
     * Get a collection of page {@link Link} objects to add to a collection of
     * resources.
     *
     * @param controller the controller that you want to generate the links for.
     * @param page the current page.
     * @param size the current size of requested resources.
     * @param totalElements the total number of elements.
     * @param sortColumn the column that should be used to sort the resources.
     * @param sortOrder the order that the column should be sorted on.
     * @return A collection of links to assist with page navigation.
     */
    public static Iterable<Link> pageLinksFor(Class<?> controller, int page, int size, int totalElements, String sortColumn, Order sortOrder) {
        List<Link> links = new ArrayList<>(5);
        String baseUrl = ControllerLinkBuilder.linkTo(controller).withSelfRel().getHref();
        int lastPage = (int) Math.ceil(totalElements / (size * 1.));
        int nextPage = page == lastPage ? lastPage : page + 1;
        int prevPage = page > 2 ? page - 1 : 1;
        int firstPage = 1;
        logger.debug("page: [" + page + "], size: [" + size + "], totalElements [" + totalElements + "]");
        logger.debug("firstPage: [" + firstPage + "], prevPage [" + prevPage + "], nextPage [" + nextPage + "], lastPage: [" + lastPage + "]");

        if (!baseUrl.endsWith("?")) {
            baseUrl = baseUrl + "?";
        }

        links.add(new Link(baseUrl + pageParams(firstPage, size, sortColumn, sortOrder), PageLink.REL_FIRST));
        if (page > 1) {
            links.add(new Link(baseUrl + pageParams(prevPage, size, sortColumn, sortOrder), PageLink.REL_PREV));
        }
        if (page < lastPage) {
            links.add(new Link(baseUrl + pageParams(nextPage, size, sortColumn, sortOrder), PageLink.REL_NEXT));
        }
        links.add(new Link(baseUrl + pageParams(lastPage, size, sortColumn, sortOrder), PageLink.REL_LAST));
        links.add(new Link(baseUrl + pageParams(page, size, sortColumn, sortOrder), PageLink.REL_SELF));

        return links;
    }

    /**
     * Construct the paging parameter lists for appending to a URL.
     *
     * @param page the page that the link should point to.
     * @param size the size of the result set in the page.
     * @param sortColumn the column that the result set should be sorted by.
     * @param sortOrder the order of the sort.
     * @return the parameter section of the URL.
     */
    private static String pageParams(int page, int size, String sortColumn, Order sortOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(REQUEST_PARAM_PAGE).append("=").append(page).append("&");
        sb.append(REQUEST_PARAM_SIZE).append("=").append(size).append("&");
        if (!Strings.isNullOrEmpty(sortColumn)) {
            sb.append(REQUEST_PARAM_SORT_PROPERTY).append("=").append(sortColumn).append("&");
        }
        if (sortOrder != null) {
            sb.append(REQUEST_PARAM_SORT_ORDER).append("=").append(sortOrder);
        }
        return sb.toString();
    }
}
