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

import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowStructure;

/**
 * Used to load up IRIDA workflows.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class IridaWorkflowLoaderService {

	private Unmarshaller workflowDescriptionUnmarshaller;

	/**
	 * Builds a new {@link IridaWorkflowLoaderService} with the given
	 * unmarshaller.
	 * 
	 * @param workflowDescriptionUnmarshaller
	 *            The unmarshaller to use.
	 */
	@Autowired
	public IridaWorkflowLoaderService(Unmarshaller workflowDescriptionUnmarshaller) {
		this.workflowDescriptionUnmarshaller = workflowDescriptionUnmarshaller;
	}

	/**
	 * Loads up an {@link IridaWorkflow} from the given information files.
	 * 
	 * @param descriptionFile
	 *            The description file for the workflow.
	 * @param structureFile
	 *            The file describing the structure of a workflow.
	 * @return An IridaWorkflow object for this workflow.
	 * @throws IOException
	 *             If there was an issue reading the passed file.
	 * @throws XmlMappingException
	 *             If there was an issue parsing the XML document.
	 */
	public IridaWorkflow loadIridaWorkflow(Path descriptionFile, Path structureFile) throws XmlMappingException,
			IOException {
		checkNotNull("descriptionFile is null", descriptionFile);
		checkNotNull("structureFile is null", structureFile);

		IridaWorkflowDescription worklowDescription = loadWorkflowDescription(descriptionFile);
		IridaWorkflowStructure workflowStructure = loadWorkflowStructure(structureFile);
		return new IridaWorkflow(worklowDescription, workflowStructure);
	}

	/**
	 * Loads up the workflow description from the given file.
	 * 
	 * @param descriptionFile
	 *            The file to load up a workflow description.
	 * @return An IridaWorkflowDescription object.
	 * @throws IOException
	 *             If there was an issue reading the passed file.
	 * @throws XmlMappingException
	 *             If there was an issue parsing the XML document.
	 */
	public IridaWorkflowDescription loadWorkflowDescription(Path descriptionFile) throws XmlMappingException,
			IOException {
		checkNotNull("descriptionFile is null", descriptionFile);
		Source source = new StreamSource(new FileInputStream(descriptionFile.toFile()));
		return (IridaWorkflowDescription) workflowDescriptionUnmarshaller.unmarshal(source);
	}

	/**
	 * Loads up the structure of the workflow given the file.
	 * 
	 * @param structureFile
	 *            The file to load up.
	 * @return An {@link IridaWorkflowStructure} defining the structure of the
	 *         workflow.
	 */
	public IridaWorkflowStructure loadWorkflowStructure(Path structureFile) {
		checkNotNull("structureFile is null", structureFile);
		return new IridaWorkflowStructure(structureFile);
	}
}
