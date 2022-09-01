package ca.corefacility.bioinformatics.irida.ria.web.samples;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnalysesService;

import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleNameCheckRequest;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleNameCheckResponse;

import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;

/**
 * Controller for asynchronous requests for a {@link Sample}
 */
@RestController
@RequestMapping("/ajax/samples")
public class SamplesAjaxController {
	private final UISampleService uiSampleService;
	private final UIAnalysesService uiAnalysesService;

	@Autowired
	public SamplesAjaxController(UISampleService uiSampleService, UIAnalysesService uiAnalysesService) {
		this.uiSampleService = uiSampleService;
		this.uiAnalysesService = uiAnalysesService;
	}

	/**
	 * Upload {@link SequenceFile}'s to a sample
	 *
	 * @param sampleId The {@link Sample} id to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @return {@link ResponseEntity} containing the message for the user on the status of the action
	 */
	@RequestMapping(value = { "/{sampleId}/sequenceFiles/upload" }, method = RequestMethod.POST)
	public ResponseEntity<List<SampleSequencingObjectFileModel>> uploadSequenceFiles(@PathVariable Long sampleId,
			MultipartHttpServletRequest request) {
		try {
			return ResponseEntity.ok(uiSampleService.uploadSequenceFiles(sampleId, request));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}

	/**
	 * Upload fast5 files to the given sample
	 *
	 * @param sampleId the ID of the sample to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @return {@link ResponseEntity} containing the message for the user on the status of the action
	 */
	@RequestMapping(value = "/{sampleId}/fast5/upload", method = RequestMethod.POST)
	public ResponseEntity<List<SampleSequencingObjectFileModel>> uploadFast5Files(@PathVariable Long sampleId,
			MultipartHttpServletRequest request) {
		try {
			return ResponseEntity.ok(uiSampleService.uploadFast5Files(sampleId, request));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}

	/**
	 * Upload assemblies to the given sample
	 *
	 * @param sampleId the ID of the sample to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @return {@link ResponseEntity} containing the message for the user on the status of the action
	 */
	@RequestMapping(value = { "/{sampleId}/assemblies/upload" }, method = RequestMethod.POST)
	public ResponseEntity<List<SampleGenomeAssemblyFileModel>> uploadAssemblies(@PathVariable Long sampleId,
			MultipartHttpServletRequest request) {
		try {
			return ResponseEntity.ok(uiSampleService.uploadAssemblies(sampleId, request));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}

	/**
	 * Get {@link Sample} details for a specific sample.
	 *
	 * @param id        {@link Long} identifier for a sample.
	 * @param projectId {@link Long} identifier for project
	 * @return {@link SampleDetails} for the {@link Sample}
	 */
	@GetMapping(value = "/{id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SampleDetails> getSampleDetails(@PathVariable Long id, @RequestParam Long projectId) {
		return ResponseEntity.ok(uiSampleService.getSampleDetails(id, projectId));
	}

	/**
	 * Get {@link Sample} metadata for a specific sample.
	 *
	 * @param id        {@link Long} identifier for a sample.
	 * @param projectId {@link Long} identifier for a project
	 * @return {@link SampleMetadata} for the {@link Sample}
	 */
	@GetMapping(value = "/{id}/metadata")
	public ResponseEntity<SampleMetadata> getSampleMetadata(@PathVariable Long id, @RequestParam Long projectId) {
		return ResponseEntity.ok(uiSampleService.getSampleMetadata(id, projectId));
	}

	/**
	 * Update a field within the sample details.
	 *
	 * @param id      {@link Long} identifier for the sample
	 * @param request {@link UpdateSampleAttributeRequest} details about which field to update
	 * @param locale  {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the update.
	 */
	@PutMapping(value = "/{id}/details")
	public ResponseEntity<AjaxResponse> updateSampleDetails(@PathVariable Long id,
			@RequestBody UpdateSampleAttributeRequest request, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(uiSampleService.updateSampleDetails(id, request, locale)));
		} catch (ConstraintViolationException e) {
			String constraintViolations = "";
			for (ConstraintViolation a : e.getConstraintViolations()) {
				constraintViolations += a.getMessage() + "\n";
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(constraintViolations));
		}
	}

	/**
	 * Update the default sequencing object for the sample
	 *
	 * @param id                 {@link Long} identifier for the sample
	 * @param sequencingObjectId The sequencing object identifier
	 * @param locale             {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the update.
	 */
	@PutMapping(value = "/{id}/default-sequencing-object")
	public ResponseEntity<AjaxResponse> updateDefaultSequencingObjectForSample(@PathVariable Long id,
			@RequestParam Long sequencingObjectId, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(
					uiSampleService.updateDefaultSequencingObjectForSample(id, sequencingObjectId, locale)));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Update the default genome assembly for the sample
	 *
	 * @param id                 {@link Long} identifier for the sample
	 * @param genomeAssemblyId The genome assembly identifier
	 * @param locale             {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the update.
	 */
	@PutMapping(value = "/{id}/default-genome-assembly")
	public ResponseEntity<AjaxResponse> updateDefaultGenomeAssemblyForSample(@PathVariable Long id,
			@RequestParam Long genomeAssemblyId, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(
					uiSampleService.updateDefaultGenomeAssemblyForSample(id, genomeAssemblyId, locale)));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Get analyses for sample
	 *
	 * @param sampleId  Identifier for a sample
	 * @param principal The currently logged on user
	 * @param locale    User's locale
	 * @return {@link ResponseEntity} containing a list of analyses for the sample
	 */
	@GetMapping("/{sampleId}/analyses")
	public ResponseEntity<List<SampleAnalyses>> getSampleAnalyses(@PathVariable Long sampleId, Principal principal,
			Locale locale) {
		try {
			return ResponseEntity.ok(uiAnalysesService.getSampleAnalyses(sampleId, principal, locale));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(null);
		}
	}

	/**
	 * Add a metadata field and entry to {@link Sample}
	 *
	 * @param id                       {@link Long} identifier for the sample
	 * @param addSampleMetadataRequest DTO containing sample metadata to add params
	 * @param locale                   {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the addition.
	 */
	@PostMapping(value = "/{id}/metadata")
	public ResponseEntity<AddSampleMetadataResponse> addSampleMetadata(@PathVariable Long id,
			@RequestBody AddSampleMetadataRequest addSampleMetadataRequest, Locale locale) {
		return ResponseEntity.ok(uiSampleService.addSampleMetadata(id, addSampleMetadataRequest, locale));
	}

	/**
	 * Remove metadata field and entry from {@link Sample}
	 *
	 * @param projectId       The project identifier
	 * @param metadataField   The metadata field
	 * @param metadataEntryId The metadata entry identifier
	 * @param locale          {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the deletion.
	 */
	@DeleteMapping(value = "/metadata")
	public ResponseEntity<AjaxResponse> removeSampleMetadata(@RequestParam Long projectId,
			@RequestParam String metadataField, @RequestParam Long metadataEntryId, Locale locale) {
		return ResponseEntity.ok(new AjaxSuccessResponse(
				uiSampleService.removeSampleMetadata(projectId, metadataField, metadataEntryId, locale)));
	}

	/**
	 * Update a metadata field entry for {@link Sample}
	 *
	 * @param id                          The sample identifier
	 * @param updateSampleMetadataRequest DTO containing sample metadata update params
	 * @param locale                      {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the update.
	 */
	@PutMapping(value = "/{id}/metadata")
	public ResponseEntity<AjaxResponse> updateSampleMetadata(@PathVariable Long id,
			@RequestBody UpdateSampleMetadataRequest updateSampleMetadataRequest, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(
					uiSampleService.updateSampleMetadata(id, updateSampleMetadataRequest, locale)));
		} catch (EntityExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Get sequencing files associated with a sample
	 *
	 * @param id        Identifier for a sample
	 * @param projectId Identifier for a project
	 * @return All sequencing files associated with a sample.
	 */
	@GetMapping("/{id}/files")
	public ResponseEntity<AjaxResponse> getFilesForSample(@PathVariable Long id,
			@RequestParam(required = false) Long projectId) {
		return ResponseEntity.ok(uiSampleService.getSampleFiles(id, projectId));
	}

	/**
	 * Get updated sample sequencing objects for given sequencing object ids
	 *
	 * @param id                  Identifier for a sample
	 * @param sequencingObjectIds Identifiers for updated sequencing objects to get
	 * @param projectId           Identifier for the project the sample belongs to
	 * @return {@link ResponseEntity} list of {@link SampleFiles} objects
	 */
	@GetMapping("/{id}/updated-sequencing-objects")
	public ResponseEntity<SampleFiles> getUpdatedSequencingObjects(@PathVariable Long id,
			@RequestParam(value = "sequencingObjectIds") List<Long> sequencingObjectIds,
			@RequestParam(required = false) Long projectId) {
		return ResponseEntity.ok(uiSampleService.getUpdatedSequencingObjects(id, sequencingObjectIds, projectId));
	}

	/**
	 * Remove a sequencing object or genome assembly linked to a {@link Sample}
	 *
	 * @param id           Identifier for a sample
	 * @param fileObjectId Identifier for the genome assembly or sequencing object
	 * @param fileType     The type of file (sequencing object or genome assembly)
	 * @param locale       {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the delete.
	 */
	@DeleteMapping("/{id}/files")
	public ResponseEntity<AjaxResponse> deleteFilesFromSample(@PathVariable Long id, @RequestParam Long fileObjectId,
			@RequestParam String fileType, Locale locale) {
		if (fileType.equals("sequencingObject")) {
			return ResponseEntity.ok(new AjaxSuccessResponse(
					uiSampleService.deleteSequencingObjectFromSample(id, fileObjectId, locale)));
		} else {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(uiSampleService.deleteGenomeAssemblyFromSample(id, fileObjectId, locale)));
		}
	}

	/**
	 * Download a GenomeAssembly file
	 *
	 * @param sampleId         Identifier for a sample
	 * @param genomeAssemblyId Identifier for the genome assembly
	 * @param response         {@link HttpServletResponse}
	 * @throws IOException if the file cannot be read
	 */
	@GetMapping("/{sampleId}/assembly/download")
	public void downloadAssembly(@PathVariable Long sampleId, @RequestParam Long genomeAssemblyId,
			HttpServletResponse response) throws IOException {
		uiSampleService.downloadAssembly(sampleId, genomeAssemblyId, response);
	}

	/**
	 * Get a list of all {@link Sample} identifiers within a specific project
	 * Check if a list of sample names exist within a project
	 *
	 * @param request {@link SampleNameCheckRequest} containing the project id and sample names
	 * @return {@link SampleNameCheckResponse} containing list of valid and invalid sample names
	 */
	@PostMapping("/validate")
	public SampleNameCheckResponse checkSampleNames(@RequestBody SampleNameCheckRequest request) {
		return uiSampleService.checkSampleNames(request);
	}

	/**
	 * Concatenate a collection of {@link SequencingObject}s
	 *
	 * @param sampleId          the id of the {@link Sample} to concatenate in
	 * @param sequenceObjectIds the {@link SequencingObject} ids
	 * @param newFileName       base of the new filename to create
	 * @param removeOriginals   boolean whether to remove the original files
	 * @return {@link ResponseEntity} with the new concatenated sequencing object
	 */
	@PostMapping(value = "/{sampleId}/files/concatenate")
	public ResponseEntity<List<SampleSequencingObjectFileModel>> concatenateSequenceFiles(@PathVariable Long sampleId,
			@RequestParam(name = "sequencingObjectIds") Set<Long> sequenceObjectIds,
			@RequestParam(name = "newFileName") String newFileName,
			@RequestParam(name = "removeOriginals", defaultValue = "false", required = false) boolean removeOriginals) {
		try {
			return ResponseEntity.ok(uiSampleService.concatenateSequenceFiles(sampleId, sequenceObjectIds, newFileName,
					removeOriginals));
		} catch (ConcatenateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(null);
		}
	}

}