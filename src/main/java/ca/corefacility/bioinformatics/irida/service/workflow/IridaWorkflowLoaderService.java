package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowDescription;

/**
 * Used to load up IRIDA workflows.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class IridaWorkflowLoaderService {

	private Unmarshaller workflowDescriptionUnmarshaller;
	
	/**
	 * Builds a new {@link IridaWorkflowLoaderService} with the given unmarshaller.
	 * @param workflowDescriptionUnmarshaller  The unmarshaller to use.
	 */
	@Autowired
	public IridaWorkflowLoaderService(Unmarshaller workflowDescriptionUnmarshaller) {
		this.workflowDescriptionUnmarshaller = workflowDescriptionUnmarshaller;
	}
	
	/**
	 * Loads up the workflow description from the given file.
	 * @param descriptionFile  The file to load up a workflow description.
	 * @return  An IridaWorkflowDescription object.
	 * @throws IOException If there was an issue reading the passed file.
	 * @throws XmlMappingException If there was an issue parsing the XML document.
	 */
	public IridaWorkflowDescription loadWorkflowDescription(Path descriptionFile) throws XmlMappingException, IOException {
		checkNotNull("descriptionFile is null", descriptionFile);
		Source source = new StreamSource(new FileInputStream(descriptionFile.toFile()));
		return (IridaWorkflowDescription)workflowDescriptionUnmarshaller.unmarshal(source);
	}
}
