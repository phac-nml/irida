package ca.corefacility.bioinformatics.irida.web.controller.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Thymeleaf portion of the web project is powered by an AngularJS single-page application. This controller is
 * responsible for only responding with a Thymeleaf index page, then the AngularJS application takes over and queries
 * the server for resources using the REST api.
 */
@Controller
@RequestMapping("/**")
public class RootController {
    @RequestMapping
    public String getRoot() {
        return "index";
    }
}
