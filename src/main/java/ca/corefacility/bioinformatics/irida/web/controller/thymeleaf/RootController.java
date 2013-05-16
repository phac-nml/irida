package ca.corefacility.bioinformatics.irida.web.controller.thymeleaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Thymeleaf portion of the web project is powered by an AngularJS single-page application. This controller is
 * responsible for only responding with a Thymeleaf index page, then the AngularJS application takes over and queries
 * the server for resources using the REST api.
 */
@Controller
public class RootController {
    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    /**
     * Get the index for any request that does not contain the string "css", "js", or "font" for thymeleaf to render.
     *
     * @return the name of the index page
     */
    @RequestMapping("/**")
    public String getRoot() {
        logger.debug("Loading index.");
        return "index";
    }
}
