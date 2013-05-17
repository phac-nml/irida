package ca.corefacility.bioinformatics.irida.web.controller.thymeleaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * The Thymeleaf portion of the web project is powered by an AngularJS single-page application. This controller is
 * responsible for only responding with a Thymeleaf index page, then the AngularJS application takes over and queries
 * the server for resources using the REST api.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public final class RootController {
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RootController.class);
    /**
     * Base URI for the REST api.
     */
    private static final String REST_API_PREFIX = "/api";
    /**
     * Prefix used by spring for forwarding requests to another controller.
     */
    private static final String SPRING_FORWARD_PREFIX = "forward:";

    /**
     * Get the index for any request that does not contain the string "css", "js", or "font" for thymeleaf to render.
     *
     * @return the name of the index page
     */
    @RequestMapping(value = "/**", produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE})
    public String getRoot() {
        logger.debug("Loading index.");
        return "index";
    }

    /**
     * This method passes all requests that are not for HTML content through to the REST api servlet.
     *
     * @param request the request that was sent by the client.
     * @return a forward string to the REST api.
     */
    @RequestMapping(value = "/**", produces = MediaType.ALL_VALUE)
    public String getResources(HttpServletRequest request) {
        // get the URI part of the request (everything after the servlet's /, but before ?)
        // Spring continues to handle the request params after ? correctly because it reads
        // the HttpServletRequest object when determining which RequestMapping to use.
        String view = request.getRequestURI();

        // the URI to forward to includes the REST prefix and the view name
        StringBuilder forward = new StringBuilder(SPRING_FORWARD_PREFIX);
        forward.append(REST_API_PREFIX).append(view);

        logger.debug("Forwarding to REST API: [" + forward.toString() + "]");

        // let Spring internally forward the client to the correct resource
        return forward.toString();
    }
}
