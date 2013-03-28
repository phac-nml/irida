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
package ca.corefacility.bioinformatics.irida.model;

import java.io.File;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * A file that may be stored somewhere on the file system and belongs to a
 * particular {@link Sample}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleFile {

    private Identifier id;
    @NotNull
    private Audit audit;
    @NotNull
    private File file;

    public SampleFile(File sampleFile) {
        this.audit = new Audit();
        this.file = sampleFile;
    }

    public SampleFile(Identifier id, File sampleFile) {
        this(sampleFile);
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SampleFile) {
            SampleFile sampleFile = (SampleFile) other;
            return Objects.equals(file, sampleFile.file);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    public Identifier getId() {
        return id;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
