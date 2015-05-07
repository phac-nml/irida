package ca.corefacility.bioinformatics.irida.web.controller.api;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun.RESTSequencingRunController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * A basis for clients to begin discovering other URLs in our API.
 *
 */
@Controller
public class RESTRootController {

    /**
     * A collection of the controllers in our system.
     */
    public static final Map<String, Class<?>> CONTROLLERS = new ConcurrentHashMap<>();
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(RESTRootController.class);

    /**
     * Initialize a collection of all controllers in the system.
     */
    @PostConstruct
    public void initLinks() {
        CONTROLLERS.put("users", RESTUsersController.class);
        CONTROLLERS.put("projects", RESTProjectsController.class);
        CONTROLLERS.put("sequencingRuns", RESTSequencingRunController.class);
    }

    /**
     * Creates a response with a set of links used to discover the rest of the system.
     *
     * @return a response to the client.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api")
    public ModelMap getLinks() {
        logger.debug("Discovering application");
        RootResource resource = new RootResource();
        List<Link> links = new ArrayList<>();

        // create a link to all of the controllers defined in our set, then add the link to the list of links.
        for (Entry<String, Class<?>> entry : CONTROLLERS.entrySet()) {
            Link link = linkTo(entry.getValue()).withRel(entry.getKey());
            links.add(link);
        }

        // add a self-rel to the current page
        resource.add(linkTo(methodOn(RESTRootController.class).
                getLinks()).withSelfRel());

        // add all of the links to the response
        resource.add(links);

        ModelMap map = new ModelMap();
        map.addAttribute(RESTGenericController.RESOURCE_NAME, resource);

        // respond to the client
        return map;
    }
}
