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

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.controller.api.SequenceFileController;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Unit tests for {@link SequenceFileController}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileControllerTest {

    private SequenceFileController controller;
    private CRUDService<Identifier, SequenceFile> service;
    private static final String ORIGINAL_FILENAME = "original";

    @Before
    public void setUp() {
        service = mock(CRUDService.class);
        controller = new SequenceFileController(service, null);

        // fake out the servlet response so that the URI builder will work.
        RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(ra);
    }

    @After
    public void cleanUp() throws IOException {
        File f = Paths.get(System.getProperty(("java.io.tmpdir")), ORIGINAL_FILENAME).toFile();
        f.delete();
    }

    @Test
    public void testDuplicateFilename() throws IOException {
        File f = Files.createTempFile(UUID.randomUUID().toString(), null).toFile();
        f.deleteOnExit();

        Identifier firstId = new Identifier();
        SequenceFile first = new SequenceFile(f);

        first.setIdentifier(firstId);

        when(service.create(any(SequenceFile.class))).thenReturn(first);

        MockMultipartFile mmf = new MockMultipartFile(ORIGINAL_FILENAME, ORIGINAL_FILENAME, "blurgh", FileCopyUtils.copyToByteArray(new FileInputStream(f)));

        ResponseEntity<String> response = controller.create(mmf);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        try {
            response = controller.create(mmf);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        } catch (FileAlreadyExistsException e) {
            fail();
        }
    }
}
