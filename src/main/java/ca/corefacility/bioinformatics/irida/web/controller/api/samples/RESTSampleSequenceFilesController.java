package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import com.google.common.net.HttpHeaders;

/**
 * Controller for managing relationships between {@link Sample} and
 * {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public class RESTSampleSequenceFilesController {
	private static final Logger logger = LoggerFactory.getLogger(RESTSampleSequenceFilesController.class);
	/**
	 * Rel to get back to the {@link Sample}.
	 */
	public static final String REL_SAMPLE = "sample";
	/**
	 * Rel to the {@link SequenceFile} pair
	 */
	public static final String REL_PAIR = "pair";
	/**
	 * Rel to get to the new location of the {@link SequenceFile}.
	 */
	public static final String REL_SAMPLE_SEQUENCE_FILES = "sample/sequenceFiles";
	/**
	 * The key used in the request to add an existing {@link SequenceFile} to a
	 * {@link Sample}.
	 */
	public static final String SEQUENCE_FILE_ID_KEY = "sequenceFileId";
	/**
	 * Reference to the {@link SequenceFileService}.
	 */
	private SequenceFileService sequenceFileService;
	/**
	 * Reference to the {@link SampleService}.
	 */
	private SampleService sampleService;
	/**
	 * Reference to the {@link ProjectService}.
	 */
	private ProjectService projectService;	
	/**
	 * Reference to the {@link MiseqRunService}
	 */
	private SequencingRunService miseqRunService;

	protected RESTSampleSequenceFilesController() {
	}

	@Autowired
	public RESTSampleSequenceFilesController(SequenceFileService sequenceFileService, SampleService sampleService,
			ProjectService projectService, SequencingRunService miseqRunService) {
		this.sequenceFileService = sequenceFileService;
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.miseqRunService = miseqRunService;
	}

	/**
	 * Get the {@link SequenceFile} entities associated with a specific
	 * {@link Sample}.
	 * 
	 * @param sampleId
	 *            the identifier for the {@link Sample}.
	 * @return the {@link SequenceFile} entities associated with the
	 *         {@link Sample}.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles", method = RequestMethod.GET)
	public ModelMap getSampleSequenceFiles(@PathVariable Long projectId, @PathVariable Long sampleId) {
		ModelMap modelMap = new ModelMap();
		// Use the RelationshipService to get the set of SequenceFile
		// identifiers associated with a Sample, then
		// retrieve each of the SequenceFiles and prepare for serialization.
		logger.debug("Reading seq files for sample " + sampleId +  " in project " + projectId);
		Sample sample = sampleService.read(sampleId);
		List<Join<Sample, SequenceFile>> relationships = sequenceFileService.getSequenceFilesForSample(sample);

		ResourceCollection<SequenceFileResource> resources = new ResourceCollection<>(relationships.size());
		for (Join<Sample, SequenceFile> r : relationships) {
			SequenceFile sf = r.getObject();

			SequenceFileResource sfr = new SequenceFileResource();
			sfr.setResource(sf);
			sfr.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
							sf.getId())).withSelfRel());
			resources.add(sfr);
		}

		// add a link to this collection
		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withSelfRel());
		// add a link back to the sample
		resources.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resources);
		return modelMap;
	}

	/**
	 * Add a new {@link SequenceFile} to a {@link Sample}.
	 * 
	 * @param projectId
	 *            the identifier for the {@link Project}.
	 * @param sampleId
	 *            the identifier for the {@link Sample}.
	 * @param file
	 *            the content of the {@link SequenceFile}.
	 * @return a response indicating the success of the submission.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> addNewSequenceFileToSample(@PathVariable Long projectId, @PathVariable Long sampleId,
			@RequestPart("file") MultipartFile file, @RequestPart(value="parameters",required=false) SequenceFileResource fileResource) throws IOException {
		logger.debug("Adding sequence file to sample " + sampleId + " in project " + projectId);
		logger.trace("Uploaded file size: " + file.getSize() + " bytes");
		
		Project p = projectService.read(projectId);
		logger.trace("Read project " + projectId);
		// confirm that a relationship exists between the project and the sample
		sampleService.getSampleForProject(p, sampleId);

		// load the sample from the database
		Sample sample = sampleService.read(sampleId);
		logger.trace("Read sample " + sampleId);

		// prepare a new sequence file using the multipart file supplied by the
		// caller
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());
		
		Join<Sample, SequenceFile> sampleSequenceFileRelationship = addSequenceFileToSample(file, fileResource, sample);

		// clean up the temporary files.
		Files.deleteIfExists(target);
		Files.deleteIfExists(temp);
		logger.trace("Deleted temp file");

		// prepare a link to the sequence file itself (on the sequence file
		// controller)
		Long sequenceFileId = sampleSequenceFileRelationship.getObject().getId();
		String location = linkTo(
				methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
						sequenceFileId)).withSelfRel().getHref();

		// prepare the headers
		MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
		responseHeaders.add(HttpHeaders.LOCATION, location);

		// respond to the client
		return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
	}
	
	/**
	 * Add a {@link SequenceFile} to a {@link Sample}
	 * 
	 * @param file
	 *            The multipart file to save
	 * @param fileResource
	 *            The file metadata resource
	 * @param sample
	 *            the {@link Sample} to add to
	 * @return A {@link Join} between the file and sample
	 * @throws IOException
	 *             If the file is not successfully saved
	 */
	private Join<Sample, SequenceFile> addSequenceFileToSample(MultipartFile file, SequenceFileResource fileResource,
			Sample sample) throws IOException {
		// prepare a new sequence file using the multipart file supplied by the
		// caller
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());

		//transfer the file to the temp location
		file.transferTo(target.toFile());

		logger.trace("Wrote temp file to " + target);

		SequenceFile sf;
		SequencingRun miseqRun = null;

		if (fileResource != null) {
			sf = fileResource.getResource();

			Long miseqRunId = fileResource.getMiseqRunId();
			if (miseqRunId != null) {
				miseqRun = miseqRunService.read(miseqRunId);
				logger.trace("Read miseq run " + miseqRunId);
			}
		} else {
			sf = new SequenceFile();
		}
		sf.setFile(target);

		if (miseqRun != null) {
			sf.setSequencingRun(miseqRun);
			logger.trace("Added seqfile to miseqrun");
		}

		// persist the changes by calling the sample service
		Join<Sample, SequenceFile> sampleSequenceFileRelationship = sequenceFileService.createSequenceFileInSample(sf,
				sample);
		logger.trace("Created seqfile in sample " + sampleSequenceFileRelationship.getObject().getId());

		// clean up the temporary files.
		Files.deleteIfExists(target);
		Files.deleteIfExists(temp);
		logger.trace("Deleted temp file");

		return sampleSequenceFileRelationship;
	}

	/**
	 * Add a relationship between an existing {@link SequenceFile} and a
	 * {@link Sample}. If the {@link SequenceFile} has a relationship with the
	 * {@link Project} that this {@link Sample} belongs to, then the
	 * relationship is terminated. The {@link SequenceFile} is not required to
	 * belong to the parent {@link Project}.
	 * 
	 * @param projectId
	 *            the identifier of the {@link Project} that the {@link Sample}
	 *            belongs to.
	 * @param sampleId
	 *            the identifier of the {@link Sample}.
	 * @param requestBody
	 *            the JSON/XML encoded request that contains the
	 *            {@link SequenceFile} identifier.
	 * @return
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> addExistingSequenceFileToSample(@PathVariable Long projectId,
			@PathVariable Long sampleId, @RequestBody Map<String, String> requestBody) {
		// sanity checking, does the correct key exist in the request body?
		if (!requestBody.containsKey(SEQUENCE_FILE_ID_KEY)) {
			throw new InvalidPropertyException("Required property [" + SEQUENCE_FILE_ID_KEY + "] not found in request.");
		}

		Long sequenceFileIdentifier = Long.valueOf(requestBody.get(SEQUENCE_FILE_ID_KEY));
		logger.debug("Adding sequence file reference " + sequenceFileIdentifier + " to sample " + sampleId);
		Project p = projectService.read(projectId);
		// confirm the relationship between the sample and the project.
		Sample s = sampleService.getSampleForProject(p, sampleId);

		// load the sample and sequence file from the database.
		SequenceFile sf = sequenceFileService.read(sequenceFileIdentifier);

		// persist the changes by calling the sample service
		Join<Sample, SequenceFile> sampleSequenceFileRelationship = sampleService.addSequenceFileToSample(s, sf);

		// prepare a link to the sequence file itself (on the sequence file
		// controller)
		Long sequenceFileId = sampleSequenceFileRelationship.getObject().getId();
		String location = linkTo(
				methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
						sequenceFileId)).withSelfRel().getHref();

		// prepare the headers
		MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
		responseHeaders.add(HttpHeaders.LOCATION, location);

		// respond to the client
		return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
	}

	/**
	 * Remove a {@link SequenceFile} from a {@link Sample}. The
	 * {@link SequenceFile} will be moved to the
	 * {@link ca.corefacility.bioinformatics.irida.model.Project} that is
	 * related to this {@link Sample}.
	 * 
	 * @param projectId
	 *            the destination
	 *            {@link ca.corefacility.bioinformatics.irida.model.Project}
	 *            identifier.
	 * @param sampleId
	 *            the source {@link Sample} identifier.
	 * @param sequenceFileId
	 *            the identifier of the {@link SequenceFile} to move.
	 * @return a status indicating the success of the move.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.DELETE)
	public ModelMap removeSequenceFileFromSample(@PathVariable Long projectId, @PathVariable Long sampleId,
			@PathVariable Long sequenceFileId) {
		ModelMap modelMap = new ModelMap();
		// load the project, sample and sequence file from the database
		projectService.read(projectId);
		Sample s = sampleService.read(sampleId);
		SequenceFile sf = sequenceFileService.read(sequenceFileId);

		// ask the service to remove the sample from the sequence file and
		// associate it with the project. The service
		// responds with the new relationship between the project and the
		// sequence file.
		sampleService.removeSequenceFileFromSample(s, sf);

		// respond with a link to the sample, the new location of the sequence
		// file (as it is associated with the
		// project)
		RootResource resource = new RootResource();
		resource.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				REL_SAMPLE));
		resource.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withRel(REL_SAMPLE_SEQUENCE_FILES));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resource);

		return modelMap;
	}

	/**
	 * Get a specific {@link SequenceFile} associated with a {@link Sample}.
	 * 
	 * @param projectId
	 *            the identifier of the {@link Project}.
	 * @param sampleId
	 *            the identifier of the {@link Sample}.
	 * @param sequenceFileId
	 *            the identifier of the {@link SequenceFile}.
	 * @return a representation of the {@link SequenceFile}.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.GET)
	public ModelMap getSequenceFileForSample(@PathVariable Long projectId, @PathVariable Long sampleId,
			@PathVariable Long sequenceFileId) {
		ModelMap modelMap = new ModelMap();
		projectService.read(projectId);
		sampleService.read(sampleId);

		// if the relationships exist, load the sequence file from the database
		// and prepare for serialization.
		SequenceFile sf = sequenceFileService.read(sequenceFileId);
		SequenceFileResource sfr = new SequenceFileResource();
		sfr.setResource(sf);

		// add links to the resource
		sfr.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withRel(REL_SAMPLE_SEQUENCE_FILES));
		sfr.add(linkTo(
				methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
						sequenceFileId)).withSelfRel());
		sfr.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				REL_SAMPLE));
		
		/**
		 * if a SequenceFilePair exists for this file, add the rel
		 */
		try{
			logger.trace("Getting paired file for " + sequenceFileId);
			SequenceFile pairedFileForSequenceFile = sequenceFileService.getPairedFileForSequenceFile(sf);
			sfr.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
							pairedFileForSequenceFile.getId())).withRel(REL_PAIR));
		}
		catch(EntityNotFoundException ex){
			logger.trace("No pair for file " + sequenceFileId);
		}
		
		// add the resource to the response
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, sfr);

		return modelMap;
	}
	
	/**
	 * Add a {@link SequenceFilePair} for the given files
	 * 
	 * @param projectId
	 *            the project ID for the files
	 * @param sampleId
	 *            the sample ID for the files
	 * @param pairIds
	 *            the pair ids
	 * @return A success message
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/pair", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> addSequenceFilePair(@PathVariable Long projectId, @PathVariable Long sampleId,
			@RequestBody List<Long> pairIds) {
		projectService.read(projectId);
		sampleService.read(sampleId);

		if (pairIds.size() != 2) {
			throw new IllegalArgumentException("Request must contain 2 keys");
		}

		Iterator<Long> iterator = pairIds.iterator();
		Long file1Id = iterator.next();
		Long file2Id = iterator.next();
		
		SequenceFile file1 = sequenceFileService.read(file1Id);
		SequenceFile file2 = sequenceFileService.read(file2Id);

		logger.debug("Adding sequence file pair for " + file1 + " and " + file2);

		sequenceFileService.createSequenceFilePair(file1, file2);

		return new ResponseEntity<>("success", HttpStatus.CREATED);
	}
	
	/**
     * Update a {@link SequenceFile} details.
     *
     * @param projectId     the identifier of the {@link Project} that the {@link Sample} belongs to.
     * @param sampleId      the identifier of the {@link Sample}.
     * @param updatedFields the updated fields of the {@link Sample}.
     * @return a response including links to the {@link Project} and {@link Sample}.
     */
    @RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequenceFileId}", method = RequestMethod.PATCH,
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ModelMap updateSequenceFile(@PathVariable Long projectId, @PathVariable Long sampleId,
			@PathVariable Long sequenceFileId, @RequestBody Map<String, Object> updatedFields) {
        ModelMap modelMap = new ModelMap();

        // confirm that the project is related to the sample
        Project p = projectService.read(projectId);
        sampleService.getSampleForProject(p, sampleId);

        // issue an update request
		sequenceFileService.update(sequenceFileId, updatedFields);

        // respond to the client with a link to self, sequence files collection and project.
        RootResource resource = new RootResource();
        resource.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId, sequenceFileId))
                .withSelfRel());
        resource.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
                .withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
        resource.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(RESTProjectSamplesController.REL_PROJECT_SAMPLES));

        modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resource);

        return modelMap;
    }
}
