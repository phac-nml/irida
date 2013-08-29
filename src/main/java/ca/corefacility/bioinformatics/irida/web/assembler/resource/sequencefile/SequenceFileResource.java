package ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile;

import java.nio.file.Path;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Resource wrapper for {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "sequenceFile")
public class SequenceFileResource extends IdentifiableResource<SequenceFile> {
   
    public SequenceFileResource() {
            super(new SequenceFile());
    }

    public SequenceFileResource(SequenceFile sequenceFile) {
            super(sequenceFile);
    }

    @XmlElement
    public String getFile() {
            return resource.getFile().toString();
    }

    @XmlElement
    public String getFileName() {
            return resource.getFile().getFileName().toString();
    }

    @XmlElement
    public String getFileType() {
            return resource.getFileType();
    }

    @XmlElement
    public String getEncoding() {
            return resource.getEncoding();
    }

    @XmlElement
    public Integer getTotalSequences() {
            return resource.getTotalSequences();
    }

    @XmlElement
    public Integer getFilteredSequences() {
            return resource.getFilteredSequences();
    }

    @XmlElement
    public Integer getMinLength() {
            return resource.getMinLength();
    }

    @XmlElement
    public Integer getMaxLength() {
            return resource.getMaxLength();
    }

    @XmlElement
    public Short getGcContent() {
            return resource.getGcContent();
    }

    @JsonIgnore
    public Path getPath() {
            return resource.getFile();
    }
	
	@XmlElement
    public String getSamplePlate() {
        return resource.getSamplePlate();
    }

    public void setSamplePlate(String samplePlate) {
        resource.setSamplePlate(samplePlate);
    }

    @XmlElement
    public String getSampleWell() {
        return resource.getSampleWell();
    }

    public void setSampleWell(String sampleWell) {
        resource.setSampleWell(sampleWell);
    }

    @XmlElement
    public String getI7IndexId() {
        return resource.getI7IndexId();
    }

    public void setI7IndexId(String i7IndexId) {
        resource.setI7IndexId(i7IndexId);
    }

    @XmlElement
    public String getI7Index() {
        return resource.getI7Index();
    }

    public void setI7Index(String i7Index) {
        resource.setI7Index(i7Index);
    }

    @XmlElement
    public String getI5IndexId() {
        return resource.getI5IndexId();
    }

    public void setI5IndexId(String i5IndexId) {
        resource.setI5IndexId(i5IndexId);
    }

    @XmlElement
    public String getI5Index() {
        return resource.getI5Index();
    }

    public void setI5Index(String i5Index) {
        resource.setI5Index(i5Index);
    }

}
