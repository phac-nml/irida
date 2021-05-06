package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.*;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTAnalysisSubmissionController;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTUsersController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.net.HttpHeaders;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller for managing relationships between {@link Sample} and
 * {@link SequenceFile}.
 */
@Tag(name = "samples")
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
	 * Rel for paired sequence files for a given sample
	 */
	public static final String REL_SAMPLE_SEQUENCE_FILE_PAIRS = "sample/sequenceFiles/pairs";

	/**
	 * rel for the unpaired sequence files for a given sample
	 */
	public static final String REL_SAMPLE_SEQUENCE_FILE_UNPAIRED = "sample/sequenceFiles/unpaired";
	public static final String REL_SAMPLE_SEQUENCE_FILE_FAST5 = "sample/sequenceFiles/fast5";

	public static final String REL_SEQUENCEFILE_SAMPLE = "sequenceFile/sample";
	public static final String REL_PAIR_SAMPLE = "sequenceFilePair/sample";

	/**
	 * Rel to a sequencefile's sequencing object
	 */
	public static final String REL_SEQ_OBJECT = "sequenceFile/sequencingObject";

	/**
	 * Rel for a sequencefile's fastqc info
	 */
	public static final String REL_SEQ_QC = "sequencefile/qc";
	public static final String REL_QC_SEQFILE = "qc/sequencefile";

	/**
	 * rel for forward and reverse files
	 */
	public static final String REL_PAIR_FORWARD = "pair/forward";
	public static final String REL_PAIR_REVERSE = "pair/reverse";

	/**
	 * rel for automated analyses associated with sequencing object
	 */
	public static final String REL_AUTOMATED_ASSEMBLY = "analysis/assembly";
	public static final String REL_SISTR_TYPING = "analysis/sistr";

	/**
	 * The key used in the request to add an existing {@link SequenceFile} to a
	 * {@link Sample}.
	 */
	public static final String SEQUENCE_FILE_ID_KEY = "sequenceFileId";

	/**
	 * Filetype labels for different {@link SequencingObject} subclasses. These
	 * will be used in the hrefs for reading {@link SequencingObject}s
	 */
	public static BiMap<Class<? extends SequencingObject>, String> objectLabels = ImmutableBiMap.of(
			SequenceFilePair.class, "pairs", SingleEndSequenceFile.class, "unpaired", Fast5Object.class, "fast5");

	/**
	 * Reference to the {@link SampleService}.
	 */
	private SampleService sampleService;

	/**
	 * Reference to the {@link SequencingRunService}
	 */
	private SequencingRunService sequencingRunService;

	private SequencingObjectService sequencingObjectService;
	private AnalysisService analysisService;

	protected RESTSampleSequenceFilesController() {
	}

	@Autowired
	public RESTSampleSequenceFilesController(SampleService sampleService, SequencingRunService miseqRunService,
			SequencingObjectService sequencingObjectService, AnalysisService analysisService) {
		this.sampleService = sampleService;
		this.sequencingRunService = miseqRunService;
		this.sequencingObjectService = sequencingObjectService;
		this.analysisService = analysisService;
	}

	/**
	 * Get the {@link SequenceFile} entities associated with a specific
	 * {@link Sample}.
	 *
	 * @param sampleId the identifier for the {@link Sample}.
	 * @return the {@link SequenceFile} entities associated with the
	 * {@link Sample}.
	 */
	@Operation(operationId = "getSampleSequenceFiles", summary = "Find the sequence files for a given sample", description = "Get the sequence files for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/sequenceFiles", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResource<ResourceCollection<SequenceFile>> getSampleSequenceFiles(@PathVariable Long sampleId) {
		logger.trace("Reading seq files for sample " + sampleId);
		Sample sample = sampleService.read(sampleId);

		Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = sequencingObjectService.getSequencingObjectsForSample(
				sample);

		ResourceCollection<SequenceFile> resources = new ResourceCollection<>();
		/*
		 * Note: This is a kind of antiquated seeing we should be referencing
		 * sequencing objects instead. At the very least the link we're pointing
		 * to here should be going through the sequencing object
		 */
		for (SampleSequencingObjectJoin r : sequencingObjectsForSample) {
			for (SequenceFile sf : r.getObject()
					.getFiles()) {

				String fileLabel = objectLabels.get(r.getObject()
						.getClass());
				sf.add(linkTo(
						methodOn(RESTSampleSequenceFilesController.class).readSequenceFileForSequencingObject(sampleId,
								fileLabel, r.getObject()
										.getId(), sf.getId())).withSelfRel());

				resources.add(sf);

			}
		}

		// add a link to this collection
		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(
				sampleId)).withSelfRel());
		// add a link back to the sample
		resources.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE));

		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).listSequencingObjectsOfTypeForSample(
				sample.getId(), RESTSampleSequenceFilesController.objectLabels.get(SequenceFilePair.class))).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_PAIRS));
		resources.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).listSequencingObjectsOfTypeForSample(
				sample.getId(),
				RESTSampleSequenceFilesController.objectLabels.get(SingleEndSequenceFile.class))).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_UNPAIRED));

		ResponseResource<ResourceCollection<SequenceFile>> responseObject = new ResponseResource<>(resources);
		return responseObject;
	}

	/**
	 * List all {@link SequencingObject}s of a given type for a {@link Sample}
	 *
	 * @param sampleId   ID of the {@link Sample} to read from
	 * @param objectType {@link SequencingObject} type
	 * @return The {@link SequencingObject}s of the given type for the
	 * {@link Sample}
	 */
	@Operation(operationId = "listSequencingObjectsOfTypeForSample", summary = "Find all the sequencing objects for a given sample", description = "Get the sequencing objects for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/{objectType}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResource<ResourceCollection<SequencingObject>> listSequencingObjectsOfTypeForSample(
			@PathVariable Long sampleId, @PathVariable String objectType) {
		logger.trace("Reading seq file for sample " + sampleId);
		Sample sample = sampleService.read(sampleId);

		Class<? extends SequencingObject> type = objectLabels.inverse()
				.get(objectType);

		Collection<SampleSequencingObjectJoin> unpairedSequenceFilesForSample = sequencingObjectService.getSequencesForSampleOfType(
				sample, type);

		ResourceCollection<SequencingObject> resources = new ResourceCollection<>(
				unpairedSequenceFilesForSample.size());
		for (SampleSequencingObjectJoin join : unpairedSequenceFilesForSample) {
			SequencingObject sequencingObject = join.getObject();

			sequencingObject = addSequencingObjectLinks(sequencingObject, sampleId);
			resources.add(sequencingObject);
		}

		// add a link to this collection
		resources.add(
				linkTo(methodOn(RESTSampleSequenceFilesController.class).listSequencingObjectsOfTypeForSample(sampleId,
						objectType)).withSelfRel());
		// add a link back to the sample
		resources.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE));

		ResponseResource<ResourceCollection<SequencingObject>> responseObject = new ResponseResource<>(resources);
		return responseObject;
	}

	/**
	 * Read a single {@link SequencingObject} of the given type from a
	 * {@link Sample}
	 *
	 * @param sampleId   {@link Sample} identifier
	 * @param objectType type of {@link SequencingObject}
	 * @param objectId   ID of the {@link SequencingObject}
	 * @return A single {@link SequencingObject}
	 */
	@Operation(operationId = "readSequencingObject", summary = "Find the sequencing object for a given sample", description = "Get the sequencing object for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/{objectType}/{objectId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResource<SequencingObject> readSequencingObject(@PathVariable Long sampleId,
			@PathVariable String objectType, @PathVariable Long objectId) {
		Sample sample = sampleService.read(sampleId);
		SequencingObject sequencingObject = sequencingObjectService.readSequencingObjectForSample(sample, objectId);

		sequencingObject = addSequencingObjectLinks(sequencingObject, sampleId);

		ResponseResource<SequencingObject> responseObject = new ResponseResource<>(sequencingObject);

		return responseObject;
	}

	/**
	 * Read a single {@link SequenceFile} for a given {@link Sample} and
	 * {@link SequencingObject}
	 *
	 * @param sampleId   ID of the {@link Sample}
	 * @param objectType type of {@link SequencingObject}
	 * @param objectId   id of the {@link SequencingObject}
	 * @param fileId     ID of the {@link SequenceFile} to read
	 * @return a {@link SequenceFile}
	 */
	@Operation(operationId = "readSequenceFileForSequencingObject", summary = "Find the sequence file for a given sample and sequencing object", description = "Get the sequence file for a given sample and sequencing object.", tags = "samples")
	@ApiResponse(responseCode = "200", description = "Returns the file for a given sample and sequencing object.", content = @Content(schema = @Schema(implementation = SequenceFileSchema.class)))
	@RequestMapping(value = "/api/samples/{sampleId}/{objectType}/{objectId}/files/{fileId}", method = RequestMethod.GET)
	public ModelMap readSequenceFileForSequencingObject(@PathVariable Long sampleId, @PathVariable String objectType,
			@PathVariable Long objectId, @PathVariable Long fileId) {
		ModelMap modelMap = new ModelMap();

		Sample sample = sampleService.read(sampleId);

		SequencingObject readSequenceFilePairForSample = sequencingObjectService.readSequencingObjectForSample(sample,
				objectId);

		Optional<SequenceFile> findFirst = readSequenceFilePairForSample.getFiles()
				.stream()
				.filter(f -> f.getId()
						.equals(fileId))
				.findFirst();

		if (!findFirst.isPresent()) {
			throw new EntityNotFoundException(
					"File with id " + fileId + " is not associated with this sequencing object");
		}
		SequenceFile file = findFirst.get();

		file.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(sampleId)).withRel(
				REL_SAMPLE_SEQUENCE_FILES));
		file.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(REL_SAMPLE));

		file.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).readSequencingObject(sampleId, objectType,
				objectId)).withRel(REL_SEQ_OBJECT));

		file.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).readQCForSequenceFile(sampleId, objectType,
				objectId, fileId)).withRel(REL_SEQ_QC));

		file.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).readSequenceFileForSequencingObject(sampleId,
				objectType, objectId, fileId)).withSelfRel());

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, file);

		return modelMap;
	}

	/**
	 * Get the fastqc metrics for a {@link SequenceFile}
	 *
	 * @param sampleId   {@link Sample} id of the file
	 * @param objectType type of {@link SequencingObject}
	 * @param objectId   id of the {@link SequencingObject}
	 * @param fileId     id of the {@link SequenceFile}
	 * @return an {@link AnalysisFastQC} for the file
	 */
	@Operation(operationId = "readQCForSequenceFile", summary = "Find the fastqc metrics for a given sequence file", description = "Get the fastqc metrics for a given sequence file.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/{objectType}/{objectId}/files/{fileId}/qc", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResource<AnalysisFastQC> readQCForSequenceFile(@PathVariable Long sampleId,
			@PathVariable String objectType, @PathVariable Long objectId, @PathVariable Long fileId) {
		Sample sample = sampleService.read(sampleId);
		SequencingObject readSequencingObjectForSample = sequencingObjectService.readSequencingObjectForSample(sample,
				objectId);

		AnalysisFastQC fastQCAnalysisForSequenceFile = analysisService.getFastQCAnalysisForSequenceFile(
				readSequencingObjectForSample, fileId);

		if (fastQCAnalysisForSequenceFile == null) {
			throw new EntityNotFoundException("No QC data for file");
		}

		fastQCAnalysisForSequenceFile.add(
				linkTo(methodOn(RESTSampleSequenceFilesController.class).readSequenceFileForSequencingObject(sampleId,
						objectType, objectId, fileId)).withRel(REL_QC_SEQFILE));

		fastQCAnalysisForSequenceFile.add(
				linkTo(methodOn(RESTSampleSequenceFilesController.class).readQCForSequenceFile(sampleId, objectType,
						objectId, fileId)).withSelfRel());

		ResponseResource<AnalysisFastQC> responseObject = new ResponseResource<>(fastQCAnalysisForSequenceFile);

		return responseObject;
	}

	/**
	 * Add a new {@link SequenceFile} to a {@link Sample}.
	 *
	 * @param sampleId     the identifier for the {@link Sample}.
	 * @param file         the content of the {@link SequenceFile}.
	 * @param fileResource the parameters for the file
	 * @param response     the servlet response.
	 * @return a response indicating the success of the submission.
	 * @throws IOException if we can't write the file to disk.
	 */
	@Operation(operationId = "addNewSequenceFileToSample", summary = "Add a new sequence file to the given sample", description = "Add a new sequence file to the given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/sequenceFiles", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseResource<SequenceFile> addNewSequenceFileToSample(@PathVariable Long sampleId,
			@RequestPart("file") MultipartFile file,
			@RequestPart(value = "parameters", required = false) SequenceFileResource fileResource,
			HttpServletResponse response) throws IOException {
		ResponseResource<SequenceFile> responseObject;

		logger.debug("Adding sequence file to sample " + sampleId);
		logger.trace("Uploaded file size: " + file.getSize() + " bytes");
		// load the sample from the database
		Sample sample = sampleService.read(sampleId);
		logger.trace("Read sample " + sampleId);
		// prepare a new sequence file using the multipart file supplied by the
		// caller
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());

		try {
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
					miseqRun = sequencingRunService.read(miseqRunId);
					logger.trace("Read miseq run " + miseqRunId);
				}
			} else {
				sf = new SequenceFile();
			}

			sf.setFile(target);

			SingleEndSequenceFile singleEndSequenceFile = new SingleEndSequenceFile(sf);
			if (miseqRun != null) {
				if (miseqRun.getUploadStatus() != SequencingRunUploadStatus.UPLOADING) {
					throw new IllegalArgumentException(
							"The sequencing run must be in the UPLOADING state to upload data.");
				}
				singleEndSequenceFile.setSequencingRun(miseqRun);
				logger.trace("Added seqfile to miseqrun");
			}

			// save the seqobject and sample
			SampleSequencingObjectJoin createSequencingObjectInSample = sequencingObjectService.createSequencingObjectInSample(
					singleEndSequenceFile, sample);

			singleEndSequenceFile = (SingleEndSequenceFile) createSequencingObjectInSample.getObject();
			logger.trace("Created seqfile in sample " + createSequencingObjectInSample.getObject()
					.getId());

			// prepare a link to the sequence file itself (on the sequence file
			// controller)
			String objectType = objectLabels.get(SingleEndSequenceFile.class);
			Long sequenceFileId = singleEndSequenceFile.getSequenceFile()
					.getId();
			Link selfRel = linkTo(
					methodOn(RESTSampleSequenceFilesController.class).readSequenceFileForSequencingObject(sampleId,
							objectType, singleEndSequenceFile.getId(), sequenceFileId)).withSelfRel();

			// Changed, because sfr.setResource(sf)
			// and sfr.setResource(sampleSequenceFileRelationship.getObject())
			// both will not pass a GET-POST comparison integration test.
			singleEndSequenceFile = (SingleEndSequenceFile) sequencingObjectService.read(singleEndSequenceFile.getId());
			SequenceFile sequenceFile = singleEndSequenceFile.getFileWithId(sequenceFileId);

			// add links to the resource
			sequenceFile.add(
					linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(sampleId)).withRel(
							REL_SAMPLE_SEQUENCE_FILES));
			sequenceFile.add(selfRel);
			sequenceFile.add(
					linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(REL_SAMPLE));
			sequenceFile.add(
					linkTo(methodOn(RESTSampleSequenceFilesController.class).readSequencingObject(sampleId, objectType,
							singleEndSequenceFile.getId())).withRel(REL_SEQ_OBJECT));

			responseObject = new ResponseResource<>(sequenceFile);
			// add a location header.
			response.addHeader(HttpHeaders.LOCATION, selfRel.getHref());
			// set the response status.
			response.setStatus(HttpStatus.CREATED.value());

		} catch (IllegalArgumentException e) {
			logger.debug("Error 400 - Bad Request: " + e.getMessage());
			throw e;
		} finally {
			// clean up the temporary files.
			logger.trace("Deleted temp files");
			Files.deleteIfExists(target);
			Files.deleteIfExists(temp);
		}

		// respond to the client
		return responseObject;
	}

	/**
	 * REST function to add new Fast5 object to a sample
	 *
	 * @param sampleId     the ID of the sample to add
	 * @param file         The multipart file uploa to create
	 * @param fileResource the parameters for the file
	 * @param response     the servlet response.
	 * @return a response indicating the success of the submission.
	 * @throws IOException if we can't write the file to disk.
	 */
	@Operation(operationId = "addNewFast5FileToSample", summary = "Add a new fast5 file to the given sample", description = "Add a new fast5 file to the given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/fast5", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseResource<SequenceFile> addNewFast5FileToSample(@PathVariable Long sampleId,
			@RequestPart("file") MultipartFile file,
			@RequestPart(value = "parameters", required = false) SequenceFileResource fileResource,
			HttpServletResponse response) throws IOException {
		ResponseResource<SequenceFile> responseObject;

		logger.debug("Adding sequence file to sample " + sampleId);
		logger.trace("Uploaded file size: " + file.getSize() + " bytes");
		// load the sample from the database
		Sample sample = sampleService.read(sampleId);
		logger.trace("Read sample " + sampleId);
		// prepare a new sequence file using the multipart file supplied by the caller
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());

		try {
			// Changed to MultipartFile.transerTo(File) because it was truncating
			// large files to 1039956336 bytes
			// target = Files.write(target, file.getBytes());
			file.transferTo(target.toFile());
			logger.trace("Wrote temp file to " + target);

			SequenceFile sf;
			SequencingRun sequencingRun = null;
			if (fileResource != null) {
				sf = fileResource.getResource();

				Long sequencingRunId = fileResource.getMiseqRunId();
				if (sequencingRunId != null) {
					sequencingRun = sequencingRunService.read(sequencingRunId);
					logger.trace("Read miseq run " + sequencingRunId);
				}
			} else {
				sf = new SequenceFile();
			}

			sf.setFile(target);

			Fast5Object fast5Object = new Fast5Object(sf);
			if (sequencingRun != null) {
				if (sequencingRun.getUploadStatus() != SequencingRunUploadStatus.UPLOADING) {
					throw new IllegalArgumentException(
							"The sequencing run must be in the UPLOADING state to upload data.");
				}
				fast5Object.setSequencingRun(sequencingRun);
				logger.trace("Added seqfile to miseqrun");
			}

			// save the seqobject and sample
			SampleSequencingObjectJoin createSequencingObjectInSample = sequencingObjectService.createSequencingObjectInSample(
					fast5Object, sample);

			fast5Object = (Fast5Object) createSequencingObjectInSample.getObject();
			logger.trace("Created seqfile in sample " + createSequencingObjectInSample.getObject()
					.getId());

			// prepare a link to the sequence file itself (on the sequence file
			// controller)
			String objectType = objectLabels.get(Fast5Object.class);
			Long sequenceFileId = fast5Object.getFile()
					.getId();
			Link selfRel = linkTo(
					methodOn(RESTSampleSequenceFilesController.class).readSequenceFileForSequencingObject(sampleId,
							objectType, fast5Object.getId(), sequenceFileId)).withSelfRel();

			// Changed, because sfr.setResource(sf)
			// and sfr.setResource(sampleSequenceFileRelationship.getObject())
			// both will not pass a GET-POST comparison integration test.
			fast5Object = (Fast5Object) sequencingObjectService.read(fast5Object.getId());
			SequenceFile sequenceFile = fast5Object.getFileWithId(sequenceFileId);

			// add links to the resource
			sequenceFile.add(
					linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(sampleId)).withRel(
							REL_SAMPLE_SEQUENCE_FILES));
			sequenceFile.add(selfRel);
			sequenceFile.add(
					linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(REL_SAMPLE));
			sequenceFile.add(
					linkTo(methodOn(RESTSampleSequenceFilesController.class).readSequencingObject(sampleId, objectType,
							fast5Object.getId())).withRel(REL_SEQ_OBJECT));

			responseObject = new ResponseResource<>(sequenceFile);
			// add a location header.
			response.addHeader(HttpHeaders.LOCATION, selfRel.getHref());
			// set the response status.
			response.setStatus(HttpStatus.CREATED.value());

		} catch (IllegalArgumentException e) {
			logger.debug("Error 400 - Bad Request: " + e.getMessage());
			throw e;
		} finally {
			// clean up the temporary files.
			logger.trace("Deleted temp files");
			Files.deleteIfExists(target);
			Files.deleteIfExists(temp);
		}

		// respond to the client
		return responseObject;
	}

	/**
	 * Add a pair of {@link SequenceFile}s to a {@link Sample}
	 *
	 * @param sampleId      The {@link Sample} id to add to
	 * @param file1         The first multipart file
	 * @param fileResource1 The metadata for the first file
	 * @param file2         The second multipart file
	 * @param fileResource2 the metadata for the second file
	 * @param response      a reference to the servlet response.
	 * @return Response containing the locations for the created files
	 * @throws IOException if we can't write the files to disk
	 */
	@Operation(operationId = "addNewSequenceFilePairToSample", summary = "Add a new pair of sequence files to the given sample", description = "Add a new pair of sequence files to the given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/pairs", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseResource<SequencingObject> addNewSequenceFilePairToSample(@PathVariable Long sampleId,
			@RequestPart("file1") MultipartFile file1,
			@RequestPart(value = "parameters1") SequenceFileResource fileResource1,
			@RequestPart("file2") MultipartFile file2,
			@RequestPart(value = "parameters2") SequenceFileResource fileResource2, HttpServletResponse response)
			throws IOException {
		logger.debug("Adding pair of sequence files to sample " + sampleId);
		logger.trace("First uploaded file size: " + file1.getSize() + " bytes");
		logger.trace("Second uploaded file size: " + file2.getSize() + " bytes");

		ResponseResource<SequencingObject> responseObject;

		// confirm that a relationship exists between the project and the sample
		Sample sample = sampleService.read(sampleId);
		logger.trace("Read sample " + sampleId);
		// create temp files
		Path temp1 = Files.createTempDirectory(null);
		Path target1 = temp1.resolve(file1.getOriginalFilename());
		Path temp2 = Files.createTempDirectory(null);
		Path target2 = temp2.resolve(file2.getOriginalFilename());

		try {
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
				throw new IllegalArgumentException("Cannot upload a pair of files from different sequencing runs.");
			}

			Long runId = fileResource1.getMiseqRunId();

			SequenceFilePair sequenceFilePair = new SequenceFilePair(sf1, sf2);

			if (runId != null) {
				sequencingRun = sequencingRunService.read(runId);
				if (sequencingRun.getUploadStatus() != SequencingRunUploadStatus.UPLOADING) {
					throw new IllegalArgumentException(
							"The sequencing run must be in the UPLOADING state to upload data.");
				}
				sequenceFilePair.setSequencingRun(sequencingRun);
				logger.trace("Added sequencing run to files" + runId);
			}

			// add the files and join
			SampleSequencingObjectJoin createSequencingObjectInSample = sequencingObjectService.createSequencingObjectInSample(
					sequenceFilePair, sample);

			SequencingObject sequencingObject = createSequencingObjectInSample.getObject();

			sequencingObject = addSequencingObjectLinks(sequencingObject, sampleId);

			sequencingObject.add(
					linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(sampleId)).withRel(
							REL_SAMPLE_SEQUENCE_FILES));

			// add location header
			response.addHeader(HttpHeaders.LOCATION, sequencingObject.getLink("self")
					.getHref());

			// set the response status.
			response.setStatus(HttpStatus.CREATED.value());
			responseObject = new ResponseResource<>(sequencingObject);
		} catch (IllegalArgumentException e) {
			logger.debug("Error 400 - Bad Request: " + e.getMessage());
			throw e;
		} finally {
			// clean up the temporary files.
			logger.trace("Deleted temp files");
			Files.deleteIfExists(target1);
			Files.deleteIfExists(temp1);
			Files.deleteIfExists(target2);
			Files.deleteIfExists(temp2);
		}

		// respond to the client
		return responseObject;
	}

	/**
	 * Remove a {@link SequencingObject} from a {@link Sample}.
	 *
	 * @param sampleId   the source {@link Sample} identifier.
	 * @param objectType The type of sequencing object being removed
	 * @param objectId   the identifier of the {@link SequencingObject} to move.
	 * @return a status indicating the success of the move.
	 */
	@Operation(operationId = "removeSequenceFileFromSample", summary = "Remove a sequencing object from a given sample", description = "Delete the sequencing object from a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/{objectType}/{objectId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseResource<RootResource> removeSequenceFileFromSample(@PathVariable Long sampleId,
			@PathVariable String objectType, @PathVariable Long objectId) {
		// load the project, sample and sequence file from the database
		Sample s = sampleService.read(sampleId);
		SequencingObject seqObject = sequencingObjectService.readSequencingObjectForSample(s, objectId);

		// ask the service to remove the sample from the sequence file
		sampleService.removeSequencingObjectFromSample(s, seqObject);

		// respond with a link to the sample, the new location of the sequence
		// file (as it is associated with the
		// project)
		RootResource resource = new RootResource();
		resource.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(REL_SAMPLE));
		resource.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(sampleId)).withRel(
				REL_SAMPLE_SEQUENCE_FILES));

		ResponseResource<RootResource> responseObject = new ResponseResource<>(resource);

		return responseObject;
	}

	/**
	 * add the forward and reverse file links and a link to the pair's sample
	 *
	 * @param pair     The {@link SequenceFilePair} to enhance
	 * @param sampleId the id of the {@link Sample} the pair is in
	 * @return The {@link SequenceFilePair} with added links
	 */
	private static SequenceFilePair addSequenceFilePairLinks(SequenceFilePair pair, Long sampleId) {
		SequenceFile forward = pair.getForwardSequenceFile();
		String forwardLink = forward.getLink("self")
				.getHref();

		SequenceFile reverse = pair.getReverseSequenceFile();
		String reverseLink = reverse.getLink("self")
				.getHref();

		pair.add(new Link(forwardLink, REL_PAIR_FORWARD));
		pair.add(new Link(reverseLink, REL_PAIR_REVERSE));

		return pair;
	}

	/**
	 * Add the links for a {@link SequencingObject} to its sample, self, to each
	 * individual {@link SequenceFile}
	 *
	 * @param sequencingObject {@link SequencingObject} to enhance
	 * @param sampleId         ID of the {@link Sample} for the object
	 * @param <T>              The subclass of {@link SequencingObject} being enhanced by this method
	 * @return the enhanced {@link SequencingObject}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends SequencingObject> T addSequencingObjectLinks(T sequencingObject, Long sampleId) {

		String objectType = objectLabels.get(sequencingObject.getClass());

		// link to self
		sequencingObject.add(
				linkTo(methodOn(RESTSampleSequenceFilesController.class).readSequencingObject(sampleId, objectType,
						sequencingObject.getId())).withSelfRel());

		// link to the sample
		sequencingObject.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE));

		// link to the individual files
		for (SequenceFile file : sequencingObject.getFiles()) {
			file.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).readSequenceFileForSequencingObject(
					sampleId, objectType, sequencingObject.getId(), file.getId())).withSelfRel());
		}

		AnalysisSubmission automatedAssembly = sequencingObject.getAutomatedAssembly();
		if (automatedAssembly != null) {
			sequencingObject.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getResource(
					automatedAssembly.getId())).withRel(REL_AUTOMATED_ASSEMBLY));
		}

		AnalysisSubmission sistrTyping = sequencingObject.getSistrTyping();
		if (sistrTyping != null) {
			sequencingObject.add(
					linkTo(methodOn(RESTAnalysisSubmissionController.class).getResource(sistrTyping.getId())).withRel(
							REL_SISTR_TYPING));
		}

		// if it's a pair, add forward/reverse links
		if (sequencingObject instanceof SequenceFilePair) {
			sequencingObject = (T) addSequenceFilePairLinks((SequenceFilePair) sequencingObject, sampleId);
		}

		return sequencingObject;
	}

	private class SequenceFileSchema {
		public SequenceFile resource;
	}

}
