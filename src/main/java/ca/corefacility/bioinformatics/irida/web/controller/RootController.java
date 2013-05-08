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
package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * A basis for clients to begin discovering other URLs in our API.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);
    private static final Map<String, Class> CONTROLLERS = new ConcurrentHashMap<>();

    @PostConstruct
    public void initLinks() {
        CONTROLLERS.put("users", UsersController.class);
        CONTROLLERS.put("projects", ProjectsController.class);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ModelAndView getLinks(Model model) {
        logger.debug("Discovering application");
        ModelAndView mav = new ModelAndView("index");
        RootResource resource = new RootResource();
        List<Link> links = new ArrayList<>();

        for (Entry<String, Class> entry : CONTROLLERS.entrySet()) {
            Link link = linkTo(entry.getValue()).withRel(entry.getKey());
            links.add(link);
        }


        resource.add(linkTo(methodOn(RootController.class, Model.class).getLinks(model)).withSelfRel());
        resource.add(links);
        model.addAttribute(resource);
        return mav;
    }
}
