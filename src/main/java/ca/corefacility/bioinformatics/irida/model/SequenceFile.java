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
    
    private Boolean enabled = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;    
    
    @Column(name="filePath")
    private String stringPath;
    
    public void setStringPath(){
        stringPath = file.toFile().toString();
    }
    
    public void setRealPath(){
        file = Paths.get(stringPath);
    }

    public SequenceFile() {
        createdDate = new Date();
        modifiedDate = createdDate;
    }
    
    /**
     * Create a new {@link SequenceFile} with the given file Path
     * @param sampleFile The Path to a {@link SequenceFile}
     */
    public SequenceFile(Path sampleFile) {
        this();
        this.file = sampleFile;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SequenceFile) {
            SequenceFile sampleFile = (SequenceFile) other;
            return Objects.equals(file, sampleFile.file) 
                    && Objects.equals(createdDate, sampleFile.createdDate)
                    && Objects.equals(modifiedDate, sampleFile.modifiedDate);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file,createdDate,modifiedDate);
    }


    @Override
    public int compareTo(SequenceFile other) {
        return modifiedDate.compareTo(other.modifiedDate);
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
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean valid) {
        this.enabled = valid;
    }    

    @Override
    public Date getTimestamp() {
        return createdDate;
    }

    @Override
    public void setTimestamp(Date date) {
        this.createdDate = date;
    }
    
    @Override
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }        
}
