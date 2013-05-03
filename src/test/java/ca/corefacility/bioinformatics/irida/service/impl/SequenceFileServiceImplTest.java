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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for {@link SequenceFileServiceImpl}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImplTest {

    private CRUDService<Identifier, SequenceFile> sequenceFileService;
    private SequenceFileRepository crudRepository;
    private CRUDRepository<File, SequenceFile> fileRepository;
    private Validator validator;
    private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImplTest.class);

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        crudRepository = mock(SequenceFileRepository.class);
        fileRepository = mock(CRUDRepository.class);
        sequenceFileService = new SequenceFileServiceImpl(crudRepository, fileRepository, validator);
    }

    @Test
    public void testCreateFile() throws IOException {
        File f = Files.createTempFile(null, null).toFile();
        f.deleteOnExit();
        SequenceFile sf = new SequenceFile(f);
        SequenceFile withIdentifier = new SequenceFile(new Identifier(), f);
        when(crudRepository.create(sf)).thenReturn(withIdentifier);
        when(fileRepository.create(withIdentifier)).thenReturn(withIdentifier);
        when(crudRepository.update(withIdentifier)).thenReturn(withIdentifier);
        when(crudRepository.exists(withIdentifier.getIdentifier())).thenReturn(Boolean.TRUE);
        when(crudRepository.read(withIdentifier.getIdentifier())).thenReturn(withIdentifier);

        SequenceFile created = sequenceFileService.create(sf);
        assertEquals(created, withIdentifier);

        verify(crudRepository).create(sf);
        verify(fileRepository).create(withIdentifier);
        verify(crudRepository).update(withIdentifier);
        verify(crudRepository).read(withIdentifier.getIdentifier());
        verify(crudRepository).exists(withIdentifier.getIdentifier());
    }

    @Test
    public void testUpdateWithoutFile() throws IOException {
        Identifier updatedId = new Identifier();
        Identifier originalId = new Identifier();
        SequenceFile sf = new SequenceFile(originalId,
                Files.createTempFile(null, null).toFile());

        sf.getFile().deleteOnExit();

        when(crudRepository.exists(originalId)).thenReturn(Boolean.TRUE);
        when(crudRepository.read(originalId)).thenReturn(sf);
        when(crudRepository.update(sf)).thenReturn(sf);

        sequenceFileService.update(originalId, ImmutableMap.of("identifier", (Object) updatedId));

        assertEquals(updatedId, sf.getIdentifier());

        verify(crudRepository).exists(originalId);
        verify(crudRepository).read(originalId);
        verify(crudRepository).update(sf);
        verify(fileRepository, times(0)).update(sf);
    }

    @Test
    public void testUpdateWithFile() throws IOException {
        Identifier id = new Identifier();
        File originalFile = Files.createTempFile(null, null).toFile();
        File updatedFile = Files.createTempFile(null, null).toFile();
        SequenceFile sf = new SequenceFile(id, originalFile);
        sf.getFile().deleteOnExit();

        when(crudRepository.exists(id)).thenReturn(Boolean.TRUE);
        when(crudRepository.read(id)).thenReturn(sf);
        when(crudRepository.update(sf)).thenReturn(sf);
        when(fileRepository.update(sf)).thenReturn(sf);

        sequenceFileService.update(id, ImmutableMap.of("file", (Object) updatedFile));

        assertEquals(updatedFile, sf.getFile());

        // each is called twice, once to update other possible fields, once
        // again after an updated file has been dropped into the appropriate
        // directory
        verify(crudRepository, times(2)).exists(id);
        verify(crudRepository, times(2)).read(id);
        verify(crudRepository, times(2)).update(sf);
        verify(fileRepository).update(sf);
    }
}
