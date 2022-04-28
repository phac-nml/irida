package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.*;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplePairer;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.*;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;

import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleGenomeAssemblyFileModel;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleSequencingObjectFileModel;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleFiles;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareMetadataRestriction;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareSamplesRequest;

import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * UI Service for samples
 */
@Component
public class UISampleService {
	private final SampleService sampleService;
	private final ProjectService projectService;
	private final UpdateSamplePermission updateSamplePermission;
	private final SequencingObjectService sequencingObjectService;
	private final GenomeAssemblyService genomeAssemblyService;
	private final MetadataTemplateService metadataTemplateService;
	private final MessageSource messageSource;
	private final UICartService cartService;
	private final MetadataEntryRepository metadataEntryRepository;
	private final MetadataRestrictionRepository metadataRestrictionRepository;

	@Autowired
	public UISampleService(SampleService sampleService, ProjectService projectService,
			UpdateSamplePermission updateSamplePermission, SequencingObjectService sequencingObjectService,
			GenomeAssemblyService genomeAssemblyService, MessageSource messageSource, UICartService cartService,
			MetadataTemplateService metadataTemplateService, MetadataEntryRepository metadataEntryRepository,
			MetadataRestrictionRepository metadataRestrictionRepository) {

		this.sampleService = sampleService;
		this.projectService = projectService;
		this.updateSamplePermission = updateSamplePermission;
		this.sequencingObjectService = sequencingObjectService;
		this.genomeAssemblyService = genomeAssemblyService;
		this.metadataTemplateService = metadataTemplateService;
		this.messageSource = messageSource;
		this.cartService = cartService;
		this.metadataEntryRepository = metadataEntryRepository;
		this.metadataRestrictionRepository = metadataRestrictionRepository;
	}

	/**
	 * Get full details for a {@link Sample}
	 *
	 * @param id        Identifier for a {@link Sample}
	 * @param projectId Idenfitifer for a {@link Project}
	 * @return {@link SampleDetails}
	 */
	public SampleDetails getSampleDetails(Long id, Long projectId) {
		Sample sample = sampleService.read(id);
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		boolean isModifiable = updateSamplePermission.isAllowed(authentication, sample);
		Project project = null;
		if (cartService.isSampleInCart(id) != null) {
			project = projectService.read(cartService.isSampleInCart(id));
		} else {
			project = projectService.read(projectId);
		}
		return new SampleDetails(sample, isModifiable, project);
	}

	/**
	 * Get all the metadata for a {@link Sample}
	 *
	 * @param id        Identifier for a {@link Sample}
	 * @param projectId Identifier for a {@link Project}
	 * @return {@link SampleMetadata}
	 */
	public SampleMetadata getSampleMetadata(Long id, Long projectId) {
		Sample sample = sampleService.read(id);
		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(sample);

		List<SampleMetadataFieldEntry> metadata = metadataForSample.stream()
				.map(s -> new SampleMetadataFieldEntry(s.getField()
						.getId(), s.getField()
						.getLabel(), s.getValue(), s.getId(), getMetadataFieldRestriction(projectId, s.getField()
						.getId())))
				.sorted(Comparator.comparing(SampleMetadataFieldEntry::getMetadataTemplateField))
				.collect(Collectors.toList());

		return new SampleMetadata(metadata);
	}

	/**
	 * Update a field within the sample details.
	 *
	 * @param id      {@link Long} identifier for the sample
	 * @param request {@link UpdateSampleAttributeRequest} details about which field to update
	 * @param locale  {@link Locale} for the currently logged in user
	 * @return message indicating update status
	 */
	public String updateSampleDetails(Long id, UpdateSampleAttributeRequest request, Locale locale) {
		try {
			String dateValue = null;

			Sample sample = sampleService.read(id);
			switch (request.getField()) {
			case "sampleName":
				sample.setSampleName(request.getValue());
				break;
			case "description":
				sample.setDescription(request.getValue());
				break;
			case "organism":
				sample.setOrganism(request.getValue());
				break;
			case "isolate":
				sample.setIsolate(request.getValue());
				break;
			case "strain":
				sample.setStrain(request.getValue());
				break;
			case "collectedBy":
				sample.setCollectedBy(request.getValue());
				break;
			case "collectionDate":
				Instant instant = Instant.parse(request.getValue());
				Date collectionDate = Date.from(instant);
				dateValue = new SimpleDateFormat("yyyy-MM-dd").format(collectionDate);
				sample.setCollectionDate(collectionDate);
				break;
			case "isolationSource":
				sample.setIsolationSource(request.getValue());
				break;
			case "geographicLocationName":
				sample.setGeographicLocationName(request.getValue());
				break;
			case "latitude":
				sample.setLatitude(request.getValue());
				break;
			case "longitude":
				sample.setLongitude(request.getValue());
				break;
			default:
				return messageSource.getMessage("server.sample.details.update.error",
						new Object[] { request.getField() }, locale);
			}
			sampleService.update(sample);
			String message;
			if (Strings.isNullOrEmpty(request.getValue())) {
				message = messageSource.getMessage("server.sample.details.removed.success",
						new Object[] { request.getField() }, locale);
			} else {
				String value = dateValue != null ? dateValue : request.getValue();
				message = messageSource.getMessage("server.sample.details.updated.success",
						new Object[] { request.getField(), value }, locale);
			}
			return message;
		} catch (ConstraintViolationException e) {
			throw new ConstraintViolationException(e.getConstraintViolations());
		}
	}

	/**
	 * Update the default sequencing object for the sample
	 *
	 * @param sampleId           The sample identifier
	 * @param sequencingObjectId The sequencing object identifier
	 * @param locale             {@link Locale} for the currently logged in user
	 * @return message indicating if update was successful or not
	 */
	public String updateDefaultSequencingObjectForSample(Long sampleId, Long sequencingObjectId, Locale locale) {
		try {
			Sample sample = sampleService.read(sampleId);
			SequencingObject sequencingObject = sequencingObjectService.readSequencingObjectForSample(sample,
					sequencingObjectId);
			sample.setDefaultSequencingObject(sequencingObject);
			sampleService.update(sample);
			return messageSource.getMessage("server.SequenceFileHeaderOwner.successfully.set.default.seq.object",
					new Object[] {  }, locale);
		} catch (EntityNotFoundException e) {
			return messageSource.getMessage("server.SequenceFileHeaderOwner.unable.to.update.sample",
					new Object[] {  }, locale);
		}
	}

	/**
	 * Update the default genome assembly for the sample
	 *
	 * @param sampleId           The sample identifier
	 * @param genomeAssemblyId  The genome assembly identifier
	 * @param locale             {@link Locale} for the currently logged in user
	 * @return message indicating if update was successful or not
	 */
	public String updateDefaultGenomeAssemblyForSample(Long sampleId, Long genomeAssemblyId, Locale locale) {
		try {
			Sample sample = sampleService.read(sampleId);
			GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(sample, genomeAssemblyId);
			sample.setDefaultGenomeAssembly(genomeAssembly);
			sampleService.update(sample);
			return "Successfully set default genome assembly for sample.";
		} catch (EntityNotFoundException e) {
			return "There was an error setting the default genome assembly for the sample.";
		}
	}

	/**
	 * Add metadata for the sample
	 *
	 * @param sampleId                 {@link Long} identifier for the sample
	 * @param addSampleMetadataRequest DTO containing sample metadata to add params
	 * @param locale                   {@link Locale} for the currently logged in user
	 * @return {@link AddSampleMetadataResponse} with added metadata field, entry, restriction, and response message
	 */
	public AddSampleMetadataResponse addSampleMetadata(Long sampleId, AddSampleMetadataRequest addSampleMetadataRequest,
			Locale locale) {
		Sample sample = sampleService.read(sampleId);
		ProjectMetadataRole metadataRole = ProjectMetadataRole.fromString(
				addSampleMetadataRequest.getMetadataRestriction());
		Project project = projectService.read(addSampleMetadataRequest.getProjectId());

		MetadataTemplateField existingTemplateField = metadataTemplateService.readMetadataFieldByLabel(
				addSampleMetadataRequest.getMetadataField());

		MetadataTemplateField templateField;

		if (existingTemplateField != null) {
			templateField = existingTemplateField;
		} else {
			templateField = metadataTemplateService.saveMetadataField(
					new MetadataTemplateField(addSampleMetadataRequest.getMetadataField(), "text"));
		}

		MetadataRestriction metadataRestriction = null;

		String message = "";
		MetadataEntry entry;
		Long entryId = null;
		String entryValue = "";
		Set<MetadataEntry> metadataEntrySet = new HashSet<>();
		MetadataTemplateField metadataTemplateField = null;

		if (!Strings.isNullOrEmpty(addSampleMetadataRequest.getMetadataEntry())) {
			entry = new MetadataEntry(addSampleMetadataRequest.getMetadataEntry(), "text", templateField);
			metadataEntrySet.add(entry);
			sampleService.mergeSampleMetadata(sample, metadataEntrySet);

			MetadataTemplateField finalTemplateField = templateField;
			Optional<MetadataEntry> savedEntry = sampleService.getMetadataForSample(sample)
					.stream()
					.filter(s -> s.getField()
							.equals(finalTemplateField))
					.findFirst();

			if (savedEntry.isPresent()) {
				entryId = savedEntry.get()
						.getId();

				entryValue = savedEntry.get()
						.getValue();

				metadataTemplateField = savedEntry.get()
						.getField();

				metadataRestriction = metadataTemplateService.setMetadataRestriction(project, metadataTemplateField,
						metadataRole);
				metadataRestrictionRepository.save(metadataRestriction);
			}
			String metadataRestrictionString = messageSource.getMessage(
					"metadataRole." + metadataRestriction.getLevel(), new Object[] {}, locale);

			message = messageSource.getMessage("server.sample.metadata.add.success",
					new Object[] { addSampleMetadataRequest.getMetadataField(),
							addSampleMetadataRequest.getMetadataEntry(), metadataRestrictionString }, locale);
		}

		return new AddSampleMetadataResponse(metadataTemplateField.getId(), metadataTemplateField.getLabel(),
				entryValue, entryId, metadataRestriction.getLevel()
				.name(), message);

	}

	/**
	 * Remove metadata from the sample
	 *
	 * @param projectId       The project id
	 * @param metadataField   The metadata field
	 * @param metadataEntryId The metadata entry id
	 * @param locale          {@link Locale} for the currently logged in user
	 * @return message indicating deletion status
	 */
	public String removeSampleMetadata(Long projectId, String metadataField, Long metadataEntryId, Locale locale) {
		Project project = projectService.read(projectId);
		List<Sample> sampleList = sampleService.getSamplesForProject(project)
				.stream()
				.map((s) -> s.getObject())
				.collect(Collectors.toList());
		MetadataTemplateField metadataTemplateField = metadataTemplateService.readMetadataFieldByLabel(metadataField);
		Long fieldUsageCount = metadataEntryRepository.getMetadataEntriesCountBySamplesAndField(metadataTemplateField,
				sampleList);
		metadataEntryRepository.deleteById(metadataEntryId);
		MetadataRestriction restrictionToDelete = metadataTemplateService.getMetadataRestrictionForFieldAndProject(
				project, metadataTemplateField);
		/*
		 Only delete the restriction on the field if there is only one place
		 where the field is in use within the project
		 */
		if (fieldUsageCount == 1) {
			metadataRestrictionRepository.delete(restrictionToDelete);
		}

		return messageSource.getMessage("server.sample.metadata.remove.success", new Object[] { metadataField },
				locale);
	}

	/**
	 * Update metadata for the sample
	 *
	 * @param sampleId                    The sample identifier
	 * @param updateSampleMetadataRequest DTO containing sample metadata update params
	 * @param locale                      {@link Locale} for the currently logged in user
	 * @return message indicating update status
	 */
	public String updateSampleMetadata(Long sampleId, UpdateSampleMetadataRequest updateSampleMetadataRequest,
			Locale locale) {
		Sample sample = sampleService.read(sampleId);
		Project project = projectService.read(updateSampleMetadataRequest.getProjectId());
		boolean sampleUpdated = false;
		MetadataTemplateField metadataTemplateField = metadataTemplateService.readMetadataFieldByLabel(
				updateSampleMetadataRequest.getMetadataField());
		MetadataTemplateField existingFieldWithLabel = metadataTemplateService.readMetadataField(
				updateSampleMetadataRequest.getMetadataFieldId());
		Set<MetadataEntry> metadataEntrySet = new HashSet<>();

		ProjectMetadataRole projectMetadataRole = ProjectMetadataRole.fromString(
				updateSampleMetadataRequest.getMetadataRestriction());

		// Update the metadata field and project metadata role
		if (metadataTemplateField == null) {
			if (existingFieldWithLabel == null) {
				metadataTemplateField = new MetadataTemplateField(updateSampleMetadataRequest.getMetadataField(),
						"text");
			} else {
				metadataTemplateField = existingFieldWithLabel;
				metadataTemplateField.setLabel(updateSampleMetadataRequest.getMetadataField());
			}
			metadataTemplateService.updateMetadataField(metadataTemplateField);
		} else {
			ProjectMetadataRole roleFromUpdateRequest = projectMetadataRole;
			ProjectMetadataRole currRestriction = getMetadataFieldRestriction(project.getId(), metadataTemplateField.getId());
			projectMetadataRole = currRestriction != null ?
					currRestriction :
					ProjectMetadataRole.fromString("LEVEL_1");

			/*
			 We want to only set the role from the update request if it
			 is different than the current metadata role for the field
			 or if a previous metadata role was not set for the field
			 */

			if ((projectMetadataRole != null && !projectMetadataRole.equals(roleFromUpdateRequest)) || projectMetadataRole == null) {
				projectMetadataRole = roleFromUpdateRequest;
			}
		}

		// Update the metadata entry
		MetadataEntry prevEntry = metadataEntryRepository.getMetadataEntryBySampleAndField(metadataTemplateField,
				sample);
		boolean fieldOrValUpdated = false;
		if (prevEntry != null) {
			if (!prevEntry.getField()
					.equals(metadataTemplateField)) {
				prevEntry.setField(metadataTemplateField);
				fieldOrValUpdated = true;
			}
			if (!prevEntry.getValue()
					.equals(updateSampleMetadataRequest.getMetadataEntry())) {
				prevEntry.setValue(updateSampleMetadataRequest.getMetadataEntry());
				fieldOrValUpdated = true;
			}

			if (fieldOrValUpdated) {
				metadataEntrySet.add(prevEntry);
			}
		} else {
			MetadataEntry entry = new MetadataEntry(updateSampleMetadataRequest.getMetadataEntry(), "text",
					metadataTemplateField);
			metadataEntrySet.add(entry);
			fieldOrValUpdated = true;
		}

		//Only merge if changes were made to field or value
		if (fieldOrValUpdated) {
			sampleService.mergeSampleMetadata(sample, metadataEntrySet);
		}

		/*
		 Get the metadata restriction for the field and update if there is no previous
		 restriction on the field or a user modifies the restriction for the field
		 */
		MetadataRestriction currentRestriction = metadataTemplateService.getMetadataRestrictionForFieldAndProject(
				project, metadataTemplateField);

		if (currentRestriction == null) {
			metadataTemplateService.setMetadataRestriction(project, metadataTemplateField, projectMetadataRole);
			sampleUpdated = true;
		} else {
			if (!currentRestriction.getLevel()
					.equals(projectMetadataRole)) {
				currentRestriction.setLevel(projectMetadataRole);
				sampleUpdated = true;
				metadataRestrictionRepository.save(currentRestriction);
			}
		}

		// Delete existing field/entry/restriction if user updates field to an existing field label
		MetadataTemplateField fieldToDelete = metadataTemplateService.readMetadataField(
				updateSampleMetadataRequest.getMetadataFieldId());
		if (fieldToDelete != null && !fieldToDelete.getLabel()
				.equals(metadataTemplateField.getLabel())) {
			MetadataEntry metadataEntryToDelete = metadataEntryRepository.getMetadataEntryById(
					updateSampleMetadataRequest.getMetadataEntryId());
			MetadataRestriction restrictionToDelete = metadataTemplateService.getMetadataRestrictionForFieldAndProject(
					project, fieldToDelete);
			metadataRestrictionRepository.delete(restrictionToDelete);
			metadataEntryRepository.delete(metadataEntryToDelete);
			metadataTemplateService.deleteMetadataField(fieldToDelete);
		}

		// If sample metadata was updated then update the sample modified date
		if (sampleUpdated) {
			sample.setModifiedDate(new Date());
		}

		return messageSource.getMessage("server.sample.metadata.update.success",
				new Object[] { updateSampleMetadataRequest.getMetadataField() }, locale);
	}

	/**
	 * Get the sequence files associated with a sample
	 *
	 * @param sampleId  Identifier for a sample
	 * @param projectId Identifier for the project the sample belong to
	 * @return All the sequencing files associated with the sample
	 */
	public SampleFiles getSampleFiles(Long sampleId, Long projectId) {
		Sample sample = sampleService.read(sampleId);
		// get the project if available
		Project project = null;
		if (projectId != null) {
			project = projectService.read(projectId);
		}

		List<SampleSequencingObjectFileModel> filePairs = getPairedSequenceFilesForSample(sample, project, null);
		List<SampleSequencingObjectFileModel> singles = getSingleEndSequenceFilesForSample(sample, project, null);
		List<SampleSequencingObjectFileModel> fast5 = getFast5FilesForSample(sample, project, null);
		List<SampleGenomeAssemblyFileModel> genomeAssemblies = getGenomeAssembliesForSample(sample);

		return new SampleFiles(singles, filePairs, fast5, genomeAssemblies);
	}

	/**
	 * Get updated sample sequencing objects for given sequencing object ids
	 *
	 * @param sampleId            Identifier for a sample
	 * @param sequencingObjectIds Identifiers for updated sequencing objects to get
	 * @param projectId           Identifier for the project the sample belongs to
	 * @return list of {@link SampleFiles} objects
	 */
	public SampleFiles getUpdatedSequencingObjects(Long sampleId, List<Long> sequencingObjectIds, Long projectId) {
		Sample sample = sampleService.read(sampleId);

		Project project = null;
		if (projectId != null) {
			project = projectService.read(projectId);
		}

		/*
		Only get updated sequencing object info for the provided sequencing object ids
		 */
		List<SampleSequencingObjectFileModel> filePairs = getPairedSequenceFilesForSample(sample, project,
				sequencingObjectIds);
		List<SampleSequencingObjectFileModel> singles = getSingleEndSequenceFilesForSample(sample, project,
				sequencingObjectIds);
		List<SampleSequencingObjectFileModel> fast5 = getFast5FilesForSample(sample, project, sequencingObjectIds);

		/*
		 We set assemblies to null as they don't have any file processing that was run on the files
		 so we don't require any updated info for these files
		 */
		return new SampleFiles(singles, filePairs, fast5, null);
	}

	/**
	 * Remove a sequencing object linked to a {@link Sample}
	 *
	 * @param sampleId           Identifier for a sample
	 * @param sequencingObjectId Identifier for the sequencingObject
	 * @param locale             {@link Locale} for the currently logged in user
	 * @return {@link String} explaining to the user the results of the delete.
	 */
	public String deleteSequencingObjectFromSample(Long sampleId, Long sequencingObjectId, Locale locale) {
		Sample sample = sampleService.read(sampleId);
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);

		try {
			if (sample.getDefaultSequencingObject() != null && sample.getDefaultSequencingObject()
					.getId() == sequencingObjectId) {
				sample.setDefaultSequencingObject(null);
				sampleService.update(sample);
			}
			sampleService.removeSequencingObjectFromSample(sample, sequencingObject);
			return messageSource.getMessage("server.SampleFiles.removeSequencingObjectSuccess", new Object[] {},
					locale);
		} catch (Exception e) {
			return messageSource.getMessage("samples.files.remove.error", new Object[] { sequencingObject.getLabel() },
					locale);
		}
	}

	/**
	 * Remove a genome assembly linked to a {@link Sample}
	 *
	 * @param sampleId         Identifier for a sample
	 * @param genomeAssemblyId Identifier for the GenomeAssembly
	 * @param locale           {@link Locale} for the currently logged in user
	 * @return {@link String} explaining to the user the results of the delete.
	 */
	public String deleteGenomeAssemblyFromSample(Long sampleId, Long genomeAssemblyId, Locale locale) {
		Sample sample = sampleService.read(sampleId);
		GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(sample, genomeAssemblyId);

		try {
			genomeAssemblyService.removeGenomeAssemblyFromSample(sample, genomeAssemblyId);
			return messageSource.getMessage("server.SampleFiles.removeGenomeAssemblySuccess", new Object[] {},
					locale);
		} catch (Exception e) {
			return messageSource.getMessage("samples.files.remove.error", new Object[] { genomeAssembly.getLabel() }, locale);
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
	public void downloadAssembly(Long sampleId, Long genomeAssemblyId, HttpServletResponse response)
			throws IOException {
		Sample sample = sampleService.read(sampleId);
		GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(sample, genomeAssemblyId);

		Path path = genomeAssembly.getFile();
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + genomeAssembly.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}

	/**
	 * Get a list of paired end sequence files for a sample
	 *
	 * @param sample              the {@link Sample} to get the files for.
	 * @param project             the {@link Project} the sample belongs to
	 * @param sequencingObjectIds The ids of the sequencing objects to return
	 * @return list of paired end sequence files
	 */
	public List<SampleSequencingObjectFileModel> getPairedSequenceFilesForSample(Sample sample, Project project,
			List<Long> sequencingObjectIds) {
		Collection<SampleSequencingObjectJoin> filePairJoins = sequencingObjectIds == null ?
				sequencingObjectService.getSequencesForSampleOfType(sample, SequenceFilePair.class) :
				sequencingObjectService.getSequencesForSampleOfType(sample, SequenceFilePair.class)
						.stream()
						.filter(j -> sequencingObjectIds.contains(j.getObject()
								.getId()))
						.collect(Collectors.toList());
		// add project to qc entries and filter any unavailable entries
		List<SampleSequencingObjectFileModel> filePairs = new ArrayList<>();
		for (SampleSequencingObjectJoin join : filePairJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			SequenceFilePair sfp = (SequenceFilePair) obj;
			String firstFileSize = sfp.getForwardSequenceFile()
					.getFileSize();
			String secondFileSize = sfp.getReverseSequenceFile()
					.getFileSize();

			filePairs.add(new SampleSequencingObjectFileModel(obj, firstFileSize, secondFileSize,  obj.getQcEntries()));
		}

		return filePairs;
	}

	/**
	 * Get a list of single end sequence files for a sample
	 *
	 * @param sample              the {@link Sample} to get the files for.
	 * @param project             the {@link Project} the sample belongs to
	 * @param sequencingObjectIds The ids of the sequencing objects to return
	 * @return list of single end sequence files
	 */
	public List<SampleSequencingObjectFileModel> getSingleEndSequenceFilesForSample(Sample sample, Project project,
			List<Long> sequencingObjectIds) {
		Collection<SampleSequencingObjectJoin> singleFileJoins = sequencingObjectIds == null ?
				sequencingObjectService.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class) :
				sequencingObjectService.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class)
						.stream()
						.filter(j -> sequencingObjectIds.contains(j.getObject()
								.getId()))
						.collect(Collectors.toList());

		List<SampleSequencingObjectFileModel> singles = new ArrayList<>();
		for (SampleSequencingObjectJoin join : singleFileJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			SingleEndSequenceFile sf = (SingleEndSequenceFile) obj;
			String fileSize = sf.getSequenceFile()
					.getFileSize();
			singles.add(new SampleSequencingObjectFileModel(obj, fileSize, null,  obj.getQcEntries()));
		}

		return singles;
	}

	/**
	 * Get a list of fast5 sequence files for a sample
	 *
	 * @param sample              the {@link Sample} to get the files for.
	 * @param project             the {@link Project} the sample belongs to
	 * @param sequencingObjectIds The ids of the sequencing objects to return
	 * @return list of fast5 sequence files
	 */
	public List<SampleSequencingObjectFileModel> getFast5FilesForSample(Sample sample, Project project,
			List<Long> sequencingObjectIds) {
		Collection<SampleSequencingObjectJoin> fast5FileJoins = sequencingObjectIds == null ?
				sequencingObjectService.getSequencesForSampleOfType(sample, Fast5Object.class) :
				sequencingObjectService.getSequencesForSampleOfType(sample, Fast5Object.class)
						.stream()
						.filter(j -> sequencingObjectIds.contains(j.getObject()
								.getId()))
						.collect(Collectors.toList());

		List<SampleSequencingObjectFileModel> fast5Files = new ArrayList<>();
		for (SampleSequencingObjectJoin join : fast5FileJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			Fast5Object f5 = (Fast5Object) obj;
			String fileSize = f5.getFile()
					.getFileSize();
			fast5Files.add(new SampleSequencingObjectFileModel(obj, fileSize, null,  obj.getQcEntries()));
		}
		return fast5Files;
	}

	/**
	 * Get any genome assemblies that are available for a sample
	 *
	 * @param sample the {@link Sample} to get the assemblies for
	 * @return a list of genome assembly files
	 */
	public List<SampleGenomeAssemblyFileModel> getGenomeAssembliesForSample(Sample sample) {
		Collection<SampleGenomeAssemblyJoin> genomeAssemblyJoins = genomeAssemblyService.getAssembliesForSample(sample);
		List<SampleGenomeAssemblyFileModel> assemblyFiles = new ArrayList<>();
		for (SampleGenomeAssemblyJoin join : genomeAssemblyJoins) {
			GenomeAssembly obj = join.getObject();
			String fileSize = obj.getFileSize();
			assemblyFiles.add(new SampleGenomeAssemblyFileModel(obj, fileSize));
		}

		return assemblyFiles;
	}

	/**
	 * Adds the {@link Project} to any {@link QCEntry} within a
	 * {@link SequencingObject}. If the {@link QCEntry} reports as
	 * {@link QCEntry.QCEntryStatus#UNAVAILABLE} after being enhanced it is removed from
	 * the list
	 *
	 * @param obj     the {@link SequencingObject} to enhance
	 * @param project the {@link Project} to add
	 */
	private void enhanceQcEntries(SequencingObject obj, Project project) {
		Set<QCEntry> availableEntries = new HashSet<>();
		if (obj.getQcEntries() != null) {
			for (QCEntry q : obj.getQcEntries()) {
				q.addProjectSettings(project);
				if (!q.getStatus()
						.equals(QCEntry.QCEntryStatus.UNAVAILABLE)) {
					availableEntries.add(q);
				}
			}
		}

		obj.setQcEntries(availableEntries);
	}

	/**
	 * Get a list of all {@link Sample} identifiers within a specific project
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @return {@link List} of {@link Sample} identifiers
	 */
	public List<Long> getSampleIdsForProject(Long projectId) {
		Project project = projectService.read(projectId);
		List<Sample> samples = sampleService.getSamplesForProjectShallow(project);
		return samples.stream()
				.map(Sample::getId)
				.collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Share / Move samples with another project
	 *
	 * @param request Request containing the details of the move
	 * @param locale  current users {@link Locale}
	 * @throws Exception if project or samples cannot be found
	 */
	public void shareSamplesWithProject(ShareSamplesRequest request, Locale locale) throws Exception {
		Project currentProject = projectService.read(request.getCurrentId());
		Project targetProject = projectService.read(request.getTargetId());

		List<Sample> samples = (List<Sample>) sampleService.readMultiple(request.getSampleIds());
		if (request.getRemove()) {
			try {
				projectService.moveSamples(currentProject, targetProject, samples);
			} catch (Exception e) {
				throw new Exception(messageSource.getMessage("server.ShareSamples.move-error",
						new Object[] { targetProject.getLabel() }, locale));
			}
		} else {
			try {
				projectService.shareSamples(currentProject, targetProject, samples, !request.getLocked());
			} catch (Exception e) {
				throw new Exception(messageSource.getMessage("server.ShareSamples.copy-error",
						new Object[] { targetProject.getLabel() }, locale));
			}
		}

		// Check metadata restrictions on samples in target project
		List<ShareMetadataRestriction> restrictions = request.getRestrictions();
		List<MetadataTemplateField> fields = metadataTemplateService.getPermittedFieldsForCurrentUser(targetProject,
				false);
		for (ShareMetadataRestriction restriction : restrictions) {
			fields.stream()
					.filter(f -> Objects.equals(restriction.getIdentifier(), f.getId()))
					.findFirst()
					.ifPresent(field -> metadataTemplateService.setMetadataRestriction(targetProject, field,
							ProjectMetadataRole.fromString(restriction.getRestriction())));
		}
	}

	/**
	 * Upload {@link SequenceFile}'s to a sample
	 *
	 * @param sampleId The {@link Sample} id to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @return list of {@link SampleSequencingObjectFileModel} containing the newly created sequencing objects
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	public List<SampleSequencingObjectFileModel> uploadSequenceFiles(Long sampleId, MultipartHttpServletRequest request)
			throws IOException {
		Sample sample = sampleService.read(sampleId);

		Iterator<String> fileNames = request.getFileNames();
		List<MultipartFile> files = new ArrayList<>();
		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = new ArrayList<>();

		while (fileNames.hasNext()) {
			files.add(request.getFile(fileNames.next()));
		}

		SamplePairer samplePairer = new SamplePairer(files);
		final Map<String, List<MultipartFile>> pairedFiles = samplePairer.getPairedFiles(files);
		final List<MultipartFile> singleFiles = samplePairer.getSingleFiles(files);

		try {
			for (String key : pairedFiles.keySet()) {
				List<MultipartFile> list = pairedFiles.get(key);
				sampleSequencingObjectFileModels.add(createSequenceFilePairsInSample(list, sample));
			}

			for (MultipartFile file : singleFiles) {
				sampleSequencingObjectFileModels.add(createSequenceFileInSample(file, sample));
			}

			return sampleSequencingObjectFileModels;
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Upload {@link Fast5Object}'s to a sample
	 *
	 * @param sampleId the ID of the sample to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @return list {@link SampleSequencingObjectFileModel} containing the newly created sequencing objects
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	public List<SampleSequencingObjectFileModel> uploadFast5Files(Long sampleId, MultipartHttpServletRequest request) throws IOException {
		Sample sample = sampleService.read(sampleId);
		Iterator<String> fileNames = request.getFileNames();
		List<MultipartFile> files = new ArrayList<>();
		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = new ArrayList<>();
		while (fileNames.hasNext()) {
			files.add(request.getFile(fileNames.next()));
		}

		try {
			for (MultipartFile file : files) {
				sampleSequencingObjectFileModels.add(createFast5FileInSample(file, sample));
			}
			return sampleSequencingObjectFileModels;
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Upload {@link GenomeAssembly}'s to a sample
	 *
	 * @param sampleId the ID of the sample to upload to
	 * @param request  The current request which contains {@link MultipartFile}
	 * @return list {@link SampleGenomeAssemblyFileModel} containing the newly created genome assemblies
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	public List<SampleGenomeAssemblyFileModel> uploadAssemblies(Long sampleId, MultipartHttpServletRequest request)
			throws IOException {
		Sample sample = sampleService.read(sampleId);
		Iterator<String> fileNames = request.getFileNames();
		List<MultipartFile> files = new ArrayList<>();
		List<SampleGenomeAssemblyFileModel> sampleGenomeAssemblyFileModels = new ArrayList<>();
		while (fileNames.hasNext()) {
			files.add(request.getFile(fileNames.next()));
		}

		try {
			for (MultipartFile file : files) {
				Path temp = Files.createTempDirectory(null);
				Path target = temp.resolve(file.getOriginalFilename());
				file.transferTo(target.toFile());
				UploadedAssembly uploadedAssembly = new UploadedAssembly(target);

				GenomeAssembly genomeAssembly = genomeAssemblyService.createAssemblyInSample(sample, uploadedAssembly)
						.getObject();

				sampleGenomeAssemblyFileModels.add(
						new SampleGenomeAssemblyFileModel(genomeAssembly, uploadedAssembly.getFileSize()));

			}
			return sampleGenomeAssemblyFileModels;
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Concatenate a collection of {@link SequencingObject}s
	 *
	 * @param sampleId        the id of the {@link Sample} to concatenate in
	 * @param objectIds       the {@link SequencingObject} ids
	 * @param filename        base of the new filename to create
	 * @param removeOriginals boolean whether to remove the original files
	 * @return The concatenated sequencing object in a {@link SampleSequencingObjectFileModel}
	 * @throws ConcatenateException if there was an error concatenating the files
	 */
	@PostMapping(value = "/{sampleId}/files/concatenate")
	public List<SampleSequencingObjectFileModel> concatenateSequenceFiles(Long sampleId, Set<Long> objectIds,
			String filename, boolean removeOriginals) throws ConcatenateException {
		Sample sample = sampleService.read(sampleId);
		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = new ArrayList<>();
		Iterable<SequencingObject> readMultiple = sequencingObjectService.readMultiple(objectIds);

		try {
			SampleSequencingObjectJoin concatenatedSequencingObjects = sequencingObjectService.concatenateSequences(
					Lists.newArrayList(readMultiple), filename, sample, removeOriginals);
			SequencingObject sequencingObject = concatenatedSequencingObjects.getObject();
			String firstFileSize;
			String secondFileSize = null;
			if (sequencingObject.getFiles()
					.size() == 1) {
				firstFileSize = sequencingObject.getFiles()
						.stream()
						.findFirst()
						.get()
						.getFileSize();
			} else {
				SequenceFilePair s = (SequenceFilePair) sequencingObject;
				firstFileSize = s.getForwardSequenceFile()
						.getFileSize();
				secondFileSize = s.getReverseSequenceFile()
						.getFileSize();
			}
			sampleSequencingObjectFileModels.add(
					new SampleSequencingObjectFileModel(sequencingObject, firstFileSize, secondFileSize,
							sequencingObject.getQcEntries()));
			return sampleSequencingObjectFileModels;
		} catch (ConcatenateException ex) {
			throw new ConcatenateException(ex.getMessage());
		}
	}

	/**
	 * Get {@link MetadataRestriction} for metadata field
	 *
	 * @param projectId               Identifier for {@link Project}
	 * @param metadataTemplateFieldId Identifier for {@link MetadataTemplateField}
	 * @return {@link MetadataRestriction}
	 */
	private ProjectMetadataRole getMetadataFieldRestriction(Long projectId, Long metadataTemplateFieldId) {
		Project project = projectService.read(projectId);
		MetadataTemplateField metadataTemplateField = metadataTemplateService.readMetadataField(
				metadataTemplateFieldId);
		MetadataRestriction metadataRestriction = metadataRestrictionRepository.getRestrictionForFieldAndProject(
				project, metadataTemplateField);
		if (metadataRestriction != null) {
			return metadataRestriction.getLevel();
		}

		return null;
	}

	/**
	 * Create {@link SequenceFile}'s then add them as {@link SequenceFilePair}
	 * to a {@link Sample}
	 *
	 * @param pair   {@link List} of {@link MultipartFile}
	 * @param sample {@link Sample} to add the pair to.
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SampleSequencingObjectFileModel createSequenceFilePairsInSample(List<MultipartFile> pair, Sample sample)
			throws IOException {
		SequenceFile firstFile = createSequenceFile(pair.get(0));
		SequenceFile secondFile = createSequenceFile(pair.get(1));
		SequencingObject sequencingObject = sequencingObjectService.createSequencingObjectInSample(new SequenceFilePair(firstFile, secondFile),
				sample).getObject();
		return new SampleSequencingObjectFileModel(
				sequencingObject, firstFile.getFileSize(), secondFile.getFileSize(), sequencingObject.getQcEntries());
	}

	/**
	 * Create a {@link SequenceFile} and add it to a {@link Sample}
	 *
	 * @param file   {@link MultipartFile}
	 * @param sample {@link Sample} to add the file to.
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SampleSequencingObjectFileModel createSequenceFileInSample(MultipartFile file, Sample sample)
			throws IOException {
		SequenceFile sequenceFile = createSequenceFile(file);
		SequencingObject sequencingObject = sequencingObjectService.createSequencingObjectInSample(new SingleEndSequenceFile(sequenceFile), sample)
				.getObject();
		return new SampleSequencingObjectFileModel(
				sequencingObject, sequenceFile.getFileSize(), null, sequencingObject.getQcEntries());
	}

	/**
	 * Create a {@link Fast5Object} and add it to a {@link Sample}
	 *
	 * @param file   {@link MultipartFile}
	 * @param sample {@link Sample} to add the file to.
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SampleSequencingObjectFileModel createFast5FileInSample(MultipartFile file, Sample sample)
			throws IOException {
		SequenceFile sequenceFile = createSequenceFile(file);
		SequencingObject sequencingObject = sequencingObjectService.createSequencingObjectInSample(new Fast5Object(sequenceFile), sample)
				.getObject();
		return new SampleSequencingObjectFileModel(
				sequencingObject, sequenceFile.getFileSize(), null, sequencingObject.getQcEntries());
	}

	/**
	 * Private method to move the sequence file into the correct directory and
	 * create the {@link SequenceFile} object.
	 *
	 * @param file {@link MultipartFile} sequence file uploaded.
	 * @return {@link SequenceFile}
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SequenceFile createSequenceFile(MultipartFile file) throws IOException {
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());
		file.transferTo(target.toFile());
		return new SequenceFile(target);
	}
}
