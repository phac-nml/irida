package ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.file.Path;

/**
 * Resource wrapper for {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "sequenceFile")
public class SequenceFileResource extends Resource<Identifier, SequenceFile> {

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

    @JsonIgnore
    public Path getPath() {
        return resource.getFile();
    }
}
