package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import com.google.common.base.Objects;
import com.google.common.net.HttpHeaders;

/**
 * Controller for managing relationships between {@link Sample} and
 * {@link SequenceFile}.
 * 
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
	
	public static final String REL_SAMPLE_SEQUENCE_FILE_PAIRS = "sample/sequenceFiles/pairs";
	
	public static final String REL_SAMPLE_SEQUENCE_FILE_UNPAIRED = "sample/sequenceFiles/unpaired";
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
	 * Reference to the {@link SequenceFilePairService}
	 */
	private SequenceFilePairService  sequenceFilePairService;
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
	public RESTSampleSequenceFilesController(SequenceFileService sequenceFileService, SequenceFilePairService sequenceFilePairService, SampleService sampleService,
			ProjectService projectService, SequencingRunService miseqRunService) {
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.miseqRunService = miseqRunService;
	}

	/**
	 * Get the {@link SequenceFile} entities associated with a specific
	 * {@link Sample}.
	 *
	 * @param projectId
	 *            the ID of the project.
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

		ResourceCollection<SequenceFile> resources = new ResourceCollection<>(relationships.size());
		for (Join<Sample, SequenceFile> r : relationships) {
			SequenceFile sf = r.getObject();

			sf.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
							sf.getId())).withSelfRel());
			resources.add(sf);
		}

		// add a link to this collection
		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withSelfRel());
		// add a link back to the sample
		resources.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE));
		
		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSequenceFilePairsForSample(projectId, sampleId))
				.withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_PAIRS));
		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getUnpairedSequenceFilesForSample(projectId, sampleId))
				.withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_UNPAIRED));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resources);
		return modelMap;
	}
	
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/pairs", method = RequestMethod.GET)
	public ModelMap getSequenceFilePairsForSample(@PathVariable Long projectId, @PathVariable Long sampleId){		
		ModelMap modelMap = new ModelMap();

		logger.debug("Reading seq file  for sample " + sampleId +  " in project " + projectId);
		Sample sample = sampleService.read(sampleId);
		
		List<SequenceFilePair> sequenceFilePairsForSample = sequenceFilePairService.getSequenceFilePairsForSample(sample);

		ResourceCollection<SequenceFilePair> resources = new ResourceCollection<>(sequenceFilePairsForSample.size());
		for (SequenceFilePair pair : sequenceFilePairsForSample) {
			
			for(SequenceFile file : pair.getFiles()){
				file.add(linkTo(
						methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
								file.getId())).withSelfRel());
			}

			pair.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).readSequenceFilePair(projectId, sampleId, pair.getId()))
					.withSelfRel());
			
			resources.add(pair);
		}

		// add a link to this collection
		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSequenceFilePairsForSample(projectId, sampleId))
				.withSelfRel());
		// add a link back to the sample
		resources.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resources);
		return modelMap;
	}
	
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/unpaired", method = RequestMethod.GET)
	public ModelMap getUnpairedSequenceFilesForSample(@PathVariable Long projectId, @PathVariable Long sampleId) {
		ModelMap modelMap = new ModelMap();

		logger.debug("Reading seq file  for sample " + sampleId + " in project " + projectId);
		Sample sample = sampleService.read(sampleId);

		List<Join<Sample, SequenceFile>> unpairedSequenceFilesForSample = sequenceFileService
				.getUnpairedSequenceFilesForSample(sample);

		ResourceCollection<SequenceFile> resources = new ResourceCollection<>(unpairedSequenceFilesForSample.size());
		for (Join<Sample, SequenceFile> join : unpairedSequenceFilesForSample) {
			SequenceFile file = join.getObject();

			file.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
							file.getId())).withSelfRel());

			resources.add(file);
		}

		// add a link to this collection
		resources.add(linkTo(
				methodOn(RESTSampleSequenceFilesController.class)
						.getUnpairedSequenceFilesForSample(projectId, sampleId)).withSelfRel());
		// add a link back to the sample
		resources.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId))
				.withRel(RESTSampleSequenceFilesController.REL_SAMPLE));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resources);
		return modelMap;
	}
	
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/pairs/{pairId}", method = RequestMethod.GET)
	public ModelMap readSequenceFilePair(@PathVariable Long projectId, @PathVariable Long sampleId,
			@PathVariable Long pairId) {
		ModelMap modelMap = new ModelMap();

		Sample sample = sampleService.read(sampleId);

		SequenceFilePair readSequenceFilePairForSample = sequenceFilePairService.readSequenceFilePairForSample(sample,
				pairId);
		
		for(SequenceFile file : readSequenceFilePairForSample.getFiles()){
			file.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId, file.getId())).withSelfRel());
		}

		readSequenceFilePairForSample.add(linkTo(
				methodOn(RESTSampleSequenceFilesController.class).readSequenceFilePair(projectId, sampleId, pairId))
				.withSelfRel());
		
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME,readSequenceFilePairForSample);

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
	 * @param fileResource
	 *            the parameters for the file
	 * @param response
	 *            the servlet response.
	 * @return a response indicating the success of the submission.
	 * @throws IOException
	 *             if we can't write the file to disk.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelMap addNewSequenceFileToSample(@PathVariable Long projectId, @PathVariable Long sampleId,
			@RequestPart("file") MultipartFile file,
			@RequestPart(value = "parameters", required = false) SequenceFileResource fileResource, HttpServletResponse response) throws IOException {
		ModelMap modelMap = new ModelMap();
		
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
		// Changed to MultipartFile.transerTo(File) because it was truncating
		// large files to 1039956336 bytes
		// target = Files.write(target, file.getBytes());
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
		// prepare a link to the sequence file itself (on the sequence file
		// controller)
		Long sequenceFileId = sampleSequenceFileRelationship.getObject().getId();
		String location = linkTo(
				methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
						sequenceFileId)).withSelfRel().getHref();
		
		// Changed, because sfr.setResource(sf) 
		// and sfr.setResource(sampleSequenceFileRelationship.getObject())
		// both will not pass a GET-POST comparison integration test.
		SequenceFile sequenceFile = sequenceFileService.read(sequenceFileId);
		
		// add links to the resource
		sequenceFile.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withRel(REL_SAMPLE_SEQUENCE_FILES));
		sequenceFile.add(linkTo(
				methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
						sequenceFileId)).withSelfRel());
		sequenceFile.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				REL_SAMPLE));
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, sequenceFile);
		// add a location header.
		response.addHeader(HttpHeaders.LOCATION, location);
		// set the response status.
		response.setStatus(HttpStatus.CREATED.value());

		// respond to the client
		return modelMap;
	}
	
	/**
	 * Add a pair of {@link SequenceFile}s to a {@link Sample}
	 * 
	 * @param projectId
	 *            The {@link Project} id to add to
	 * @param sampleId
	 *            The {@link Sample} id to add to
	 * @param file1
	 *            The first multipart file
	 * @param fileResource1
	 *            The metadata for the first file
	 * @param file2
	 *            The second multipart file
	 * @param fileResource2
	 *            the metadata for the second file
	 * @param response
	 *            a reference to the servlet response.
	 * @return Response containing the locations for the created files
	 * @throws IOException
	 *             if we can't write the files to disk
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}/sequenceFiles/pairs", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelMap addNewSequenceFilePairToSample(@PathVariable Long projectId,
			@PathVariable Long sampleId, @RequestPart("file1") MultipartFile file1,
			@RequestPart(value = "parameters1") SequenceFileResource fileResource1,
			@RequestPart("file2") MultipartFile file2,
			@RequestPart(value = "parameters2") SequenceFileResource fileResource2,
			HttpServletResponse response) throws IOException {
		logger.debug("Adding pair of sequence files to sample " + sampleId + " in project " + projectId);
		logger.trace("First uploaded file size: " + file1.getSize() + " bytes");
		logger.trace("Second uploaded file size: " + file2.getSize() + " bytes");

		ModelMap modelMap = new ModelMap();
		Project p = projectService.read(projectId);
		logger.trace("Read project " + projectId);
		// confirm that a relationship exists between the project and the sample
		Sample sample = sampleService.getSampleForProject(p, sampleId);
		logger.trace("Read sample " + sampleId);
		// create temp files
		Path temp1 = Files.createTempDirectory(null);
		Path target1 = temp1.resolve(file1.getOriginalFilename());
		Path temp2 = Files.createTempDirectory(null);
		Path target2 = temp2.resolve(file2.getOriginalFilename());
		// transfer the files to temp directories
		file1.transferTo(target1.toFile());
		file2.transferTo(target2.toFile());
		// create the model objects
		SequenceFile sf1 = fileResource1.getResource();
		SequenceFile sf2 = fileResource2.getResource();
		sf1.setFile(target1);
		sf2.setFile(target2);
		// get the sequencing run
		SequencingRun sequencingRun = null;
		
		if (!Objects.equal(fileResource1.getMiseqRunId(), fileResource2.getMiseqRunId())) {
			throw new IllegalArgumentException("Cannot upload a pair of files from different sequencing runs");
		}

		Long runId = fileResource1.getMiseqRunId();
		
		if (runId != null) {
			sequencingRun = miseqRunService.read(runId);
			sf1.setSequencingRun(sequencingRun);
			sf2.setSequencingRun(sequencingRun);
			logger.trace("Added sequencing run to files" + runId);
		}
		// add the files
		List<Join<Sample, SequenceFile>> createSequenceFilePairInSample = sequenceFileService
				.createSequenceFilePairInSample(sf1, sf2, sample);
		// get the joins
		Iterator<Join<Sample, SequenceFile>> iterator = createSequenceFilePairInSample.iterator();
		Join<Sample, SequenceFile> join1 = iterator.next();
		Join<Sample, SequenceFile> join2 = iterator.next();
		// clean up the temporary files.
		Files.deleteIfExists(target1);
		Files.deleteIfExists(temp1);
		Files.deleteIfExists(target2);
		Files.deleteIfExists(temp2);
		logger.trace("Deleted temp files");
		// add 2 labeled relationship resources to a collection
		ResourceCollection<LabelledRelationshipResource<Sample,SequenceFile>> sequenceResources = new ResourceCollection
				<>(createSequenceFilePairInSample.size());
		LabelledRelationshipResource<Sample,SequenceFile> lrr1 = new LabelledRelationshipResource<Sample,SequenceFile>(
				join1.getLabel(),join1);
		LabelledRelationshipResource<Sample,SequenceFile> lrr2 = new LabelledRelationshipResource<Sample,SequenceFile>(
				join2.getLabel(),join2);
		sequenceResources.add(lrr1);
		sequenceResources.add(lrr2);
		// add links to each labeled relationship resource
		for(int i = 0; i < 2; i++) {
			LabelledRelationshipResource<Sample,SequenceFile> lrr = sequenceResources.getResources().get(i);
			lrr.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
					.withRel(REL_SAMPLE_SEQUENCE_FILES));
			lrr.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
					REL_SAMPLE));
			Link selfLink = linkTo(methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(
					projectId, sampleId,lrr.getResource().getObject().getId())).withSelfRel();
			lrr.add(selfLink);
			response.addHeader(HttpHeaders.LOCATION, selfLink.getHref());
		}	
		// add a link back to the sample
		sequenceResources.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(
				projectId, sampleId)).withRel(RESTSampleSequenceFilesController.REL_SAMPLE));
		// add a link to this collection
		sequenceResources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).addNewSequenceFilePairToSample(
				p.getId(), sample.getId(),file1, fileResource1, file2, fileResource2, response)).withSelfRel());
		// set the response status.
		response.setStatus(HttpStatus.CREATED.value());
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, sequenceResources);
		// respond to the client
		return modelMap;
	}

	/**
	 * Remove a {@link SequenceFile} from a {@link Sample}. The
	 * {@link SequenceFile} will be moved to the {@link Project} that is related
	 * to this {@link Sample}.
	 * 
	 * @param projectId
	 *            the destination {@link Project} identifier.
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
		Sample sample = sampleService.read(sampleId);

		// if the relationships exist, load the sequence file from the database
		// and prepare for serialization.
		Join<Sample, SequenceFile> sequenceFileForSample = sequenceFileService.getSequenceFileForSample(sample, sequenceFileId);
		SequenceFile sf = sequenceFileForSample.getObject();

		// add links to the resource
		sf.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withRel(REL_SAMPLE_SEQUENCE_FILES));
		sf.add(linkTo(
				methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
						sequenceFileId)).withSelfRel());
		sf.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				REL_SAMPLE));
		
		/**
		 * if a SequenceFilePair exists for this file, add the rel
		 */
		try{
			logger.trace("Getting paired file for " + sequenceFileId);
			SequenceFile pairedFileForSequenceFile = sequenceFilePairService.getPairedFileForSequenceFile(sf);
			sf.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).getSequenceFileForSample(projectId, sampleId,
							pairedFileForSequenceFile.getId())).withRel(REL_PAIR));
		}
		catch(EntityNotFoundException ex){
			logger.trace("No pair for file " + sequenceFileId);
		}
		
		// add the resource to the response
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, sf);

		return modelMap;
	}
	
	/**
	 * Update a {@link SequenceFile} details.
	 *
	 * @param projectId
	 *            the identifier of the {@link Project} that the {@link Sample}
	 *            belongs to.
	 * @param sampleId
	 *            the identifier of the {@link Sample}.
	 * @param sequenceFileId
	 *            the identifier of the {@link SequenceFile} to be updated.
	 * @param updatedFields
	 *            the updated fields of the {@link Sample}.
	 * @return a response including links to the {@link Project} and
	 *         {@link Sample}.
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
