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
package ca.corefacility.bioinformatics.irida.web.controller.link.test;

import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageLink;
import ca.corefacility.bioinformatics.irida.web.controller.links.PageableControllerLinkBuilder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Unit tests for {@link PageableControllerLinkBuilder}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class PageableControllerLinkBuilderTest {

    private static final String DEFAULT_ORDER_PROPERTY = "defaultOrderProperty";
    @Before
    public void setUp() {
        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
    }

    /**
     * If the sort by property is null, then the sortProperty field should not
     * be included in the link responses.
     */
    @Test
    public void testNullSortBy() {
        Iterable<Link> links = PageableControllerLinkBuilder.pageLinksFor(
                FakeController.class, 1, 10, 30, null, Order.ASCENDING);
        Iterator<Link> iterator = links.iterator();
        while (iterator.hasNext()) {
            Link link = iterator.next();
            assertFalse(link.getHref().contains("null"));
        }
    }

    /**
     * If the order by property is null, then the orderBy field should not be
     * included in the link responses.
     */
    @Test
    public void testNullOrderBy() {
        Iterable<Link> links = PageableControllerLinkBuilder.pageLinksFor(
                FakeController.class, 1, 10, 30, DEFAULT_ORDER_PROPERTY, null);
        Iterator<Link> iterator = links.iterator();
        while (iterator.hasNext()) {
            Link link = iterator.next();
            assertFalse(link.getHref().contains("null"));
        }
    }

    /**
     * If the number of elements is a multiple of page size, and we're on the
     * last page, the next page link should not be present.
     */
    @Test
    public void testNumElementsMultipleOfPageSize() {
        int totalPages = 5;
        int pageSize = 20;
        int numberOfElements = totalPages * pageSize;

        Iterable<Link> links = PageableControllerLinkBuilder.pageLinksFor(
                FakeController.class, totalPages, pageSize, numberOfElements,
                DEFAULT_ORDER_PROPERTY, Order.ASCENDING);
        Iterator<Link> iterator = links.iterator();
        while (iterator.hasNext()) {
            Link link = iterator.next();
            if (link.getRel().equals(PageLink.REL_NEXT)) {
                fail();
            }
        }
    }
    
        /**
     * If the number of elements is a multiple of page size, and we're on the
     * last page, the next page link should not be present.
     */
    @Test
    public void testNumElementsNotMultipleOfPageSize() {
        int totalPages = 5;
        int pageSize = 20;
        int numberOfElements = totalPages * pageSize + 1;

        Iterable<Link> links = PageableControllerLinkBuilder.pageLinksFor(
                FakeController.class, totalPages, pageSize, numberOfElements,
                DEFAULT_ORDER_PROPERTY, Order.ASCENDING);
        Iterator<Link> iterator = links.iterator();
        Collection<String> linkRels = new HashSet<>();
        while (iterator.hasNext()) {
            Link link = iterator.next();
            linkRels.add(link.getRel());
        }
        assertTrue(linkRels.contains(PageLink.REL_NEXT));
    }
}
