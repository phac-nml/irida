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

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.io.File;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 * A file that may be stored somewhere on the file system and belongs to a
 * particular {@link Sample}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="sequence_file")
@Audited
public class SequenceFile implements IridaThing<SequenceFile, Audit, Identifier>, Comparable<SequenceFile> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Transient
    private Identifier identifier;
    
    @NotNull
    @Transient 
    private Audit audit;
    
    @NotNull
    @Transient
    private Path file;
    
    private Boolean valid = true;
    
    @Column(name="filePath")
    private String stringPath;
    
    public void setStringPath(){
        stringPath = file.toFile().toString();
    }
    
    public void setRealPath(){
        file = Paths.get(stringPath);
    }

    public SequenceFile() {
    }
    
    public SequenceFile(Path sampleFile) {
        this.audit = new Audit();
        this.file = sampleFile;
    }

    public SequenceFile(Identifier id, Path sampleFile) {
        this(sampleFile);
        this.identifier = id;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SequenceFile) {
            SequenceFile sampleFile = (SequenceFile) other;
            return Objects.equals(file, sampleFile.file);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    @Override
    public int compareTo(SequenceFile other) {
        return audit.compareTo(other.audit);
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    @Override
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getLabel() {
        return file.getFileName().toString();
    }
    
    public File getIoFile(){
        return file.toFile();
    }    

    @Override
    public SequenceFile copy() {
        SequenceFile f = new SequenceFile();
        f.setFile(getIoFile().toPath());
        return f;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }

    @Override
    public Boolean isValid() {
        return valid;
    }
    
    public void setValid(Boolean valid) {
        this.valid = valid;
    }    
}
