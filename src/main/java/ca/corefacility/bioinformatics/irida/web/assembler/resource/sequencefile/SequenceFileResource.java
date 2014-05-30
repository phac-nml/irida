package ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile;

import java.nio.file.Path;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resource wrapper for {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "sequenceFile")
public class SequenceFileResource extends IdentifiableResource<SequenceFile> {
	
	private Long miseqRunId;
   
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
    
	@JsonAnySetter
	public void addAttribute(String key, String value) {
			resource.addOptionalProperty(key, value);
	}
	
	@JsonAnyGetter
	public Map<String,String> getAttributes(){
		return resource.getOptionalProperties();
	}

	@JsonIgnore
	public Long getMiseqRunId() {
		return miseqRunId;
	}
	
	@JsonProperty
	public void setMiseqRunId(Long miseqRunId) {
		this.miseqRunId = miseqRunId;
	}

}
