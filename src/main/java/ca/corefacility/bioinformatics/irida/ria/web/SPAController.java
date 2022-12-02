package ca.corefacility.bioinformatics.irida.ria.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Only controller to be used for IRIDA SPA
 */
@Controller
public class SPAController {

    /**
     * Entry point for IRIDA SPA.
     *
     * @return Index page.
     */
    @GetMapping("/projects/**")
    public String getSPAEntry() {
        return "index";
    }

}
