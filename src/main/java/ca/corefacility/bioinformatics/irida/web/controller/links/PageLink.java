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

import org.springframework.hateoas.Link;

/**
 * A specialized version of {@link Link} that exposes some predetermined keys for paging data. Notably, we want "prev"
 * instead of "previous".
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class PageLink extends Link {
    /**
     * link rel for the next page
     */
    public static final String REL_NEXT = "next";
    /**
     * link rel for the previous page
     */
    public static final String REL_PREV = "prev";
    /**
     * link rel for the first page
     */
    public static final String REL_FIRST = "first";
    /**
     * link rel for the last page
     */
    public static final String REL_LAST = "last";
    /**
     * link rel for the current page
     */
    public static final String REL_SELF = "self";
}
