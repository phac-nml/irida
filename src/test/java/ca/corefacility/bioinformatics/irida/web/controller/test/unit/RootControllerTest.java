package ca.corefacility.bioinformatics.irida.web.controller.test.unit;


import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.RootController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for the {@link RootController}.
 */
public class RootControllerTest {

    private RootController controller = new RootController();

    @Before
    public void setUp() {
        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
        controller.initLinks();
    }

    @Test
    public void testGetLinks() {
        Map<String, Class<?>> controllers = RootController.CONTROLLERS;
        ModelMap map = controller.getLinks();
        Object o = map.get(GenericController.RESOURCE_NAME);
        assertNotNull(o);
        assertTrue(o instanceof RootResource);
        RootResource r = (RootResource) o;
        for (Link l : r.getLinks()) {
            if (!l.getRel().equals("self")) {
                assertTrue(controllers.containsKey(l.getRel()));
            }
        }

        assertNotNull(r.getLink("self"));
    }
}
