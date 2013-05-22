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
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SequenceFileServiceImpl}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImplTest.class);
    private CRUDService<Identifier, SequenceFile> sequenceFileService;
    private SequenceFileRepository crudRepository;
    private CRUDRepository<Identifier, SequenceFile> fileRepository;
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        crudRepository = mock(SequenceFileRepository.class);
        fileRepository = mock(CRUDRepository.class);
        sequenceFileService = new SequenceFileServiceImpl(crudRepository, fileRepository, validator);
    }

    @Test
    public void testCreateFile() throws IOException, NoSuchFieldException {
        File f = Files.createTempFile(null, null).toFile();
        f.deleteOnExit();
        SequenceFile sf = new SequenceFile(f);
        SequenceFile withIdentifier = new SequenceFile(new Identifier(), f);
        when(crudRepository.create(sf)).thenReturn(withIdentifier);
        when(fileRepository.create(withIdentifier)).thenReturn(withIdentifier);
        when(crudRepository.update(withIdentifier.getIdentifier(),
                ImmutableMap.of("file", (Object) withIdentifier.getFile()))).thenReturn(withIdentifier);

        when(crudRepository.exists(withIdentifier.getIdentifier())).thenReturn(Boolean.TRUE);

        SequenceFile created = sequenceFileService.create(sf);
        assertEquals(created, withIdentifier);

        verify(crudRepository).create(sf);
        verify(fileRepository).create(withIdentifier);
        verify(crudRepository).update(withIdentifier.getIdentifier(),
                ImmutableMap.of("file", (Object) withIdentifier.getFile()));
        verify(crudRepository).exists(withIdentifier.getIdentifier());
    }

    @Test
    public void testUpdateWithoutFile() throws IOException, NoSuchFieldException {
        Identifier updatedId = new Identifier();
        Identifier originalId = new Identifier();
        File f = Files.createTempFile(null, null).toFile();
        SequenceFile sf = new SequenceFile(originalId, f);
        SequenceFile updatedSf = new SequenceFile(updatedId, f);
        sf.getFile().deleteOnExit();
        ImmutableMap<String, Object> updatedMap = ImmutableMap.of("identifier", (Object) updatedId);

        when(crudRepository.exists(originalId)).thenReturn(Boolean.TRUE);
        when(crudRepository.update(sf.getIdentifier(), updatedMap)).thenReturn(updatedSf);

        sf = sequenceFileService.update(originalId, updatedMap);

        assertEquals(updatedId, sf.getIdentifier());

        verify(crudRepository).exists(originalId);
        verify(crudRepository).update(originalId, updatedMap);
        verify(fileRepository, times(0)).update(sf.getIdentifier(), updatedMap);
    }

    @Test
    public void testUpdateWithFile() throws IOException, NoSuchFieldException {
        Identifier id = new Identifier();
        File originalFile = Files.createTempFile(null, null).toFile();
        File updatedFile = Files.createTempFile(null, null).toFile();
        SequenceFile sf = new SequenceFile(id, originalFile);
        SequenceFile updatedSf = new SequenceFile(id, updatedFile);
        sf.getFile().deleteOnExit();

        ImmutableMap<String, Object> updatedMap = ImmutableMap.of("file", (Object) updatedFile);

        when(crudRepository.exists(id)).thenReturn(Boolean.TRUE);
        when(crudRepository.update(sf.getIdentifier(), updatedMap)).thenReturn(updatedSf);
        when(fileRepository.update(sf.getIdentifier(), updatedMap)).thenReturn(updatedSf);

        sf = sequenceFileService.update(id, updatedMap);

        assertEquals(updatedFile, sf.getFile());

        // each is called twice, once to update other possible fields, once
        // again after an updated file has been dropped into the appropriate
        // directory
        verify(crudRepository, times(2)).exists(id);
        verify(crudRepository, times(2)).update(sf.getIdentifier(), updatedMap);
        verify(fileRepository).update(sf.getIdentifier(), updatedMap);
    }
}
