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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

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
public class SequenceFile implements IridaThing, Comparable<SequenceFile> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull
    @Transient
    private Path file;
    
    private Boolean valid = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    
    @Column(name="filePath")
    private String stringPath;
    
    public void setStringPath(){
        stringPath = file.toFile().toString();
    }
    
    public void setRealPath(){
        file = Paths.get(stringPath);
    }

    public SequenceFile() {
        timestamp = new Date();
    }
    
    public SequenceFile(Path sampleFile) {
        this.file = sampleFile;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SequenceFile) {
            SequenceFile sampleFile = (SequenceFile) other;
            return Objects.equals(file, sampleFile.file) && Objects.equals(timestamp, sampleFile.timestamp);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file,timestamp);
    }


    @Override
    public int compareTo(SequenceFile other) {
        return timestamp.compareTo(other.timestamp);
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    @Override
    public String getLabel() {
        return file.getFileName().toString();
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
    
    @Override
    public void setValid(Boolean valid) {
        this.valid = valid;
    }    

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Date date) {
        this.timestamp = date;
    }
}
