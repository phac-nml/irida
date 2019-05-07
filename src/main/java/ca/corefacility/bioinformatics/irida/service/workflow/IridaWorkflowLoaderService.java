package ca.corefacility.bioinformatics.irida.service.workflow;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;

/**
 * Used to load up IRIDA workflows.
 * 
 *
 */
@Service
public class IridaWorkflowLoaderService {

	private static final Logger logger = LoggerFactory.getLogger(IridaWorkflowLoaderService.class);

	private static final String WORKFLOW_DEFINITION_FILE = "irida_workflow.xml";
	private static final String WORKFLOW_STRUCTURE_FILE = "irida_workflow_structure.ga";

	private Unmarshaller workflowDescriptionUnmarshaller;
	
	private AnalysisTypesService analysisTypesService;

	/**
	 * Builds a new {@link IridaWorkflowLoaderService} with the given
	 * unmarshaller.
	 * 
	 * @param workflowDescriptionUnmarshaller
	 *            The unmarshaller to use.
	 * @param analysisTypesService The {@link AnalysisTypesService} to use.
	 */
	@Autowired
	public IridaWorkflowLoaderService(Unmarshaller workflowDescriptionUnmarshaller, AnalysisTypesService analysisTypesService) {
		this.workflowDescriptionUnmarshaller = workflowDescriptionUnmarshaller;
		this.analysisTypesService = analysisTypesService;
	}

	/**
	 * Loads up a set of {@link IridaWorkflow}s from the given directory.
	 * 
	 * @param workflowDirectory
	 *            The directory containing the different workflow
	 *            implementations and files.
	 * @return A set of {@link IridaWorkflow}s for all implementations.
	 * @throws IOException
	 *             If there was an issue reading one of the workflow files.
	 * @throws IridaWorkflowLoadException
	 *             If there was an issue when loading up the workflows.
	 */
	public Set<IridaWorkflow> loadAllWorkflowImplementations(Path workflowDirectory) throws IOException,
			IridaWorkflowLoadException {
		checkNotNull(workflowDirectory, "workflowDirectory is null");
		checkArgument(Files.isDirectory(workflowDirectory), "workflowDirectory is not a directory");

		Set<IridaWorkflow> workflowImplementations = new HashSet<>();

		DirectoryStream<Path> stream = Files.newDirectoryStream(workflowDirectory);

		for (Path implementationDirectory : stream) {
			if (!Files.isDirectory(implementationDirectory)) {
				logger.warn("Workflow directory " + workflowDirectory + " contains a file " + implementationDirectory
						+ " that is not a proper workflow directory");
			} else {
				IridaWorkflow iridaWorkflow = loadIridaWorkflowFromDirectory(implementationDirectory);

				workflowImplementations.add(iridaWorkflow);
			}
		}

		return workflowImplementations;
	}

	/**
	 * Loads up a workflow from the given directory.
	 * 
	 * @param workflowDirectory
	 *            The directory containing a single version of a workflow.
	 * @return An {@link IridaWorkflow} from this directory.
	 * @throws IOException
	 *             If there was an error reading one of the files.
	 * @throws IridaWorkflowLoadException
	 *             If there was an issue loading up the workflow.
	 */
	public IridaWorkflow loadIridaWorkflowFromDirectory(Path workflowDirectory) throws IOException,
			IridaWorkflowLoadException {
		checkNotNull(workflowDirectory, "workflowDirectory is null");

		return loadIridaWorkflow(workflowDirectory.resolve(WORKFLOW_DEFINITION_FILE),
				workflowDirectory.resolve(WORKFLOW_STRUCTURE_FILE));
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
	 * @throws IridaWorkflowLoadException
	 *             If there was an issue loading up the workflow.
	 */
	public IridaWorkflow loadIridaWorkflow(Path descriptionFile, Path structureFile) throws IOException,
			IridaWorkflowLoadException {
		checkNotNull(descriptionFile, "descriptionFile is null");
		checkNotNull(structureFile, "structureFile is null");

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
	 * @throws IridaWorkflowLoadException
	 *             If there was an issue loading up the workflow description.
	 */
	public IridaWorkflowDescription loadWorkflowDescription(Path descriptionFile) throws IOException,
			IridaWorkflowLoadException {
		checkNotNull(descriptionFile, "descriptionFile is null");
		if (!Files.exists(descriptionFile)) {
			throw new FileNotFoundException(descriptionFile.toAbsolutePath().toString());
		}

		Source source = new StreamSource(Files.newInputStream(descriptionFile));
		IridaWorkflowDescription workflowDescription = (IridaWorkflowDescription) workflowDescriptionUnmarshaller
				.unmarshal(source);

		if (workflowDescription.getId() == null) {
			throw new IridaWorkflowLoadException("No id for workflow description from file " + descriptionFile);
		} else if (!analysisTypesService.isValid(workflowDescription.getAnalysisType())) {
			throw new IridaWorkflowLoadException("Invalid analysisType=" + workflowDescription.getAnalysisType() + " for workflow description from file " + descriptionFile);
		} else {
			if (workflowDescription.acceptsParameters()) {
				for (IridaWorkflowParameter workflowParameter : workflowDescription.getParameters()) {
					if (workflowParameter.getDefaultValue() == null && !workflowParameter.isRequired()) {
						throw new IridaWorkflowLoadException("Parameters with no default value must set the \"required\" attribute to \"true\"." + descriptionFile);
					}
					if (workflowParameter.hasDynamicSource() && !workflowParameter.isRequired()) {
						throw new IridaWorkflowLoadException("Parameters loaded from Dynamic Sources must set the \"required\" attribute to \"true\"." + descriptionFile);
					}
					if (workflowParameter.isRequired() && workflowParameter.getDefaultValue() != null) {
						throw new IridaWorkflowLoadException("Required parameters should not have a default value." + descriptionFile);
					}
					if (workflowParameter.hasChoices() && !workflowParameter.isRequired()) {
						throw new IridaWorkflowLoadException("If parameter name='" + workflowParameter.getName()
								+ "' has choices then the 'required' attribute must be set to 'true'. "
								+ descriptionFile);
					}
					if (workflowParameter.isRequired() && workflowParameter.isChoicesEmpty()) {
						throw new IridaWorkflowLoadException(
								"Expected one or more <choice name='{name}' value='{value}'> tags within <choices> "
										+ "tag for parameter name='" + workflowParameter.getName() + "'  in file "
										+ descriptionFile);
					}
					try {
						workflowParameter.getDynamicSource();
					} catch (IridaWorkflowParameterException e) {
						throw new IridaWorkflowLoadException("Parameters may have no more than one Dynamic Source." + descriptionFile);
					}
				}
			}

			return workflowDescription;
		}
	}

	/**
	 * Loads up the structure of the workflow given the file.
	 * 
	 * @param structureFile
	 *            The file to load up.
	 * @return An {@link IridaWorkflowStructure} defining the structure of the
	 *         workflow.
	 * @throws FileNotFoundException
	 *             If the structure file could not be found.
	 */
	public IridaWorkflowStructure loadWorkflowStructure(Path structureFile) throws FileNotFoundException {
		checkNotNull(structureFile, "structureFile is null");
		if (!Files.exists(structureFile)) {
			throw new FileNotFoundException(structureFile.toAbsolutePath().toString());
		}

		return new IridaWorkflowStructure(structureFile);
	}
}
