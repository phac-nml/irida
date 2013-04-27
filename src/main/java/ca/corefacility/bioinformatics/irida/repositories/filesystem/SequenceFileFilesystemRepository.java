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
package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A repository class for managing {@link SequenceFile} files on the file
 * system.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileFilesystemRepository implements CRUDRepository<File, SequenceFile> {

    private static final Logger logger = LoggerFactory.getLogger(SequenceFileFilesystemRepository.class);
    private final Path BASE_DIRECTORY;

    public SequenceFileFilesystemRepository(String baseDirectory) {
        this(FileSystems.getDefault().getPath(baseDirectory));
    }

    public SequenceFileFilesystemRepository(Path baseDirectory) {
        this.BASE_DIRECTORY = baseDirectory;
    }

    /**
     * The {@link SequenceFile} *must* have an identifier before being passed to
     * this method, because the identifier is used as an internal directory
     * name.
     *
     * @param object the {@link SequenceFile} to store.
     * @return a reference to the {@link SequenceFile} with the stored path.
     * @throws IllegalArgumentException if the {@link SequenceFile} does not
     * have an identifier.
     */
    @Override
    public SequenceFile create(SequenceFile object) throws IllegalArgumentException {
        if (object.getIdentifier() == null
                || Strings.isNullOrEmpty(object.getIdentifier().getIdentifier())) {
            throw new IllegalArgumentException("Identifier is required.");
        }
        Path sequenceFileDir = BASE_DIRECTORY.resolve(object.getIdentifier().getIdentifier());
        File target = sequenceFileDir.resolve(object.getFile().getName()).toFile();
        try {
            sequenceFileDir.toFile().mkdir();
            logger.debug("Created directory: [" + sequenceFileDir.toString() + "]");
            Files.move(object.getFile(), target);
        } catch (IOException e) {
            throw new StorageException("Failed to move file into new directory.");
        }
        object.setFile(target);
        return object;
    }

    @Override
    public SequenceFile read(File id) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SequenceFile update(SequenceFile object) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(File id) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SequenceFile> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<SequenceFile> list(int page, int size, String sortProperty, Order order) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean exists(File id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer count() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
