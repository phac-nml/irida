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
package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.controller.ProjectsController;
import com.google.common.net.HttpHeaders;
import java.util.HashSet;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Unit tests for {@link ProjectsController}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectsControllerTest {

    private ProjectsController controller;
    private ProjectService projectService;

    @Before
    public void setUp() {
        projectService = mock(ProjectService.class);
        controller = new ProjectsController(projectService);

        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
    }

    @Test
    public void testCreateBadProject() {
        Project p = new Project();
        ProjectResource pr = new ProjectResource(p);

        when(projectService.create(p)).thenThrow(new ConstraintViolationException(new HashSet<ConstraintViolation<?>>()));

        try {
            controller.create(pr);
            fail();
        } catch (ConstraintViolationException e) {
        } catch (Exception e) {
            fail();
        }
    }

    //@Test
    public void createGoodProject() {
        Identifier id = new Identifier();
        Project p = new Project(id);
        p.setName("super duper project");
        ProjectResource pr = new ProjectResource(p);
        when(projectService.create(p)).thenReturn(p);

        ResponseEntity<String> mav = controller.create(pr);
        assertEquals(HttpStatus.CREATED, mav.getStatusCode());
        assertTrue(mav.getHeaders().getFirst(HttpHeaders.LOCATION).
                endsWith(id.getUUID().toString()));
    }
}
