/*
 * Copyright 2013 josh.
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
package ca.corefacility.bioinformatics.irida.web.controller.thymeleaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A controller for responding to requests for fragments of HTML content.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/partials", produces = MediaType.TEXT_HTML_VALUE)
public class PartialsController {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PartialsController.class);

    /**
     * Send a single page fragment back to the client.
     *
     * @param pageName the name of the page fragment to send back to the client.
     * @return a reference to the page fragment.
     */
    @RequestMapping(value = "/{pageName}", method = RequestMethod.GET)
    public String getPage(@PathVariable String pageName) {
        logger.debug(pageName);
        return "partials/" + pageName;
    }

    /**
     * Send a single page fragment back to the client.
     *
     * @param singlePage the name of the page fragment to send back to the client.
     * @return a reference to the page fragment.
     */
    @RequestMapping(value = "/{singlePage}/*", method = RequestMethod.GET)
    public String getUserPartial(@PathVariable String singlePage) {
        logger.debug(singlePage);
        return "partials/" + singlePage;
    }
}
