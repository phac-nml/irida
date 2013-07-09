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
    @RequestMapping(value = {"/{pageName}", "/{pageName}/*"}, method = RequestMethod.GET)
    public String getPage(@PathVariable String pageName) {
        logger.debug(pageName);
        return "partials/" + pageName;
    }
}
