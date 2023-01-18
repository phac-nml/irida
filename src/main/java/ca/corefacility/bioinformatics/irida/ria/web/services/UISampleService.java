package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.*;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.SearchCriteria;
import ca.corefacility.bioinformatics.irida.repositories.specification.SearchOperation;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleFilesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIShareSamplesException;
import ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile.PairedEndSequenceFileModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile.SingleEndSequenceFileModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntSearch;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectCartSample;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.MergeRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.ProjectObject;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.ProjectSamplesFilter;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.SampleObject;
import ca.corefacility.bioinformatics.irida.ria.web.projects.error.SampleMergeException;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplePairer;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.*;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
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

	private final Integer MAX_PAGE_SIZE = 5000;

	/*
	 List of names of columns in the Project > Samples table
	 These correspond to their internationalized strings in the messages file
	 */
	private final List<String> TABLE_HEADERS = ImmutableList.of("server.SamplesTable.sampleName",
			"server.SamplesTable.sampleId", "server.SamplesTable.quality", "server.SamplesTable.coverage",
			"server.SamplesTable.organism", "server.SamplesTable.project", "server.SamplesTable.projectId",
			"server.SamplesTable.collectedBy", "server.SamplesTable.created", "server.SamplesTable.modified");

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
		boolean isSampleInCart = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isModifiable = updateSamplePermission.isAllowed(authentication, sample);
		Project project = null;
		if (cartService.isSampleInCart(id) != null) {
			isSampleInCart = true;
			project = projectService.read(cartService.isSampleInCart(id));
		} else {
			project = projectService.read(projectId);
		}
		return new SampleDetails(sample, isModifiable, project, isSampleInCart);
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
				.map(s -> new SampleMetadataFieldEntry(s.getField().getId(), s.getField().getLabel(), s.getValue(),
						s.getId(), getMetadataFieldRestriction(projectId, s.getField().getId())))
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
				if (!request.getValue().equals("")) {
					Instant instant = Instant.parse(request.getValue());
					Date collectionDate = Date.from(instant);
					dateValue = new SimpleDateFormat("yyyy-MM-dd").format(collectionDate);
					sample.setCollectionDate(collectionDate);
				} else {
					sample.setCollectionDate(null);
				}
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
			return messageSource.getMessage("server.SampleFilesList.successfully.set.default.seq.object",
					new Object[] {}, locale);
		} catch (EntityNotFoundException e) {
			return messageSource.getMessage("server.SampleFilesList.unable.to.update.sample", new Object[] {}, locale);
		}
	}

	/**
	 * Update the default genome assembly for the sample
	 *
	 * @param sampleId         The sample identifier
	 * @param genomeAssemblyId The genome assembly identifier
	 * @param locale           {@link Locale} for the currently logged in user
	 * @return message indicating if update was successful or not
	 */
	public String updateDefaultGenomeAssemblyForSample(Long sampleId, Long genomeAssemblyId, Locale locale) {
		try {
			Sample sample = sampleService.read(sampleId);
			GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(sample, genomeAssemblyId);
			sample.setDefaultGenomeAssembly(genomeAssembly);
			sampleService.update(sample);
			return messageSource.getMessage("server.SampleFilesList.successfully.set.default.genome.assembly",
					new Object[] {}, locale);
		} catch (EntityNotFoundException e) {
			return messageSource.getMessage("server.SampleFilesList.unable.to.update.sample", new Object[] {}, locale);
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
		ProjectMetadataRole metadataRole = ProjectMetadataRole
				.fromString(addSampleMetadataRequest.getMetadataRestriction());
		Project project = projectService.read(addSampleMetadataRequest.getProjectId());

		MetadataTemplateField existingTemplateField = metadataTemplateService
				.readMetadataFieldByLabel(addSampleMetadataRequest.getMetadataField());

		MetadataTemplateField templateField;

		if (existingTemplateField != null) {
			templateField = existingTemplateField;
		} else {
			templateField = metadataTemplateService
					.saveMetadataField(new MetadataTemplateField(addSampleMetadataRequest.getMetadataField(), "text"));
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
					.filter(s -> s.getField().equals(finalTemplateField))
					.findFirst();

			if (savedEntry.isPresent()) {
				entryId = savedEntry.get().getId();

				entryValue = savedEntry.get().getValue();

				metadataTemplateField = savedEntry.get().getField();

				metadataRestriction = metadataTemplateService.setMetadataRestriction(project, metadataTemplateField,
						metadataRole);
				metadataRestrictionRepository.save(metadataRestriction);
			}
			String metadataRestrictionString = messageSource
					.getMessage("metadataRole." + metadataRestriction.getLevel(), new Object[] {}, locale);

			message = messageSource.getMessage("server.sample.metadata.add.success",
					new Object[] {
							addSampleMetadataRequest.getMetadataField(),
							addSampleMetadataRequest.getMetadataEntry(),
							metadataRestrictionString },
					locale);
		}

		return new AddSampleMetadataResponse(metadataTemplateField.getId(), metadataTemplateField.getLabel(),
				entryValue, entryId, metadataRestriction.getLevel().name(), message);

	}

	/**
	 * Remove metadata from the sample
	 *
	 * @param projectId       The project id
	 * @param metadataFieldId The metadata field id
	 * @param metadataEntryId The metadata entry id
	 * @param locale          {@link Locale} for the currently logged in user
	 * @return message indicating deletion status
	 */
	public String removeSampleMetadata(Long projectId, Long metadataFieldId, Long metadataEntryId, Locale locale) {
		Project project = projectService.read(projectId);
		List<Sample> sampleList = sampleService.getSamplesForProject(project)
				.stream()
				.map((s) -> s.getObject())
				.collect(Collectors.toList());
		MetadataTemplateField metadataTemplateField = metadataTemplateService.readMetadataField(metadataFieldId);

		Long fieldUsageCount = metadataEntryRepository.getMetadataEntriesCountBySamplesAndField(metadataTemplateField,
				sampleList);
		metadataEntryRepository.deleteById(metadataEntryId);

		/*
		 Only delete the restriction on the field if there is only one place
		 where the field is in use within the project
		 */
		if (fieldUsageCount == 1) {
			MetadataRestriction restrictionToDelete = metadataTemplateService
					.getMetadataRestrictionForFieldAndProject(project, metadataTemplateField);
			if (restrictionToDelete != null) {
				metadataRestrictionRepository.delete(restrictionToDelete);
			}
		}

		return messageSource.getMessage("server.sample.metadata.remove.success",
				new Object[] { metadataTemplateField.getLabel() }, locale);
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
		MetadataTemplateField metadataTemplateField = null;
		// Existing field with the updated field label
		MetadataTemplateField existingField = metadataTemplateService
				.readMetadataFieldByLabel(updateSampleMetadataRequest.getMetadataField());

		// Get the existing entry if it exists
		MetadataTemplateField existingFieldById = metadataTemplateService
				.readMetadataField(updateSampleMetadataRequest.getMetadataFieldId());
		MetadataEntry existingEntry = metadataEntryRepository.getMetadataEntryBySampleAndField(existingFieldById,
				sample);

		Set<MetadataEntry> metadataEntrySet = new HashSet<>();

		ProjectMetadataRole projectMetadataRole = ProjectMetadataRole
				.fromString(updateSampleMetadataRequest.getMetadataRestriction());

		if (existingField != null) {
			metadataTemplateField = existingField;
		} else {
			// Update the metadata field and project metadata role
			metadataTemplateField = new MetadataTemplateField(updateSampleMetadataRequest.getMetadataField(), "text");
			metadataTemplateService.saveMetadataField(metadataTemplateField);
		}

		ProjectMetadataRole roleFromUpdateRequest = projectMetadataRole;
		ProjectMetadataRole currRestriction = getMetadataFieldRestriction(project.getId(),
				metadataTemplateField.getId());
		projectMetadataRole = currRestriction != null ? currRestriction : ProjectMetadataRole.fromString("LEVEL_1");

		/*
		 We want to only set the role from the update request if it
		 is different than the current metadata role for the field
		 or if a previous metadata role was not set for the field
		 */

		if ((projectMetadataRole != null && !roleFromUpdateRequest.equals(projectMetadataRole))
				|| projectMetadataRole == null) {
			projectMetadataRole = roleFromUpdateRequest;
		}

		/*
		 If there is no existing entry for the update metadata field label, then we
		 get the existing entry of the previous metadata field label (for the sample)
		 */
		MetadataEntry prevEntry;
		if (existingEntry == null) {
			prevEntry = metadataEntryRepository.getMetadataEntryBySampleAndField(metadataTemplateField, sample);
		} else {
			prevEntry = existingEntry;
		}

		boolean fieldOrValUpdated = false;
		if (prevEntry != null) {
			if (!prevEntry.getField().getLabel().equals(updateSampleMetadataRequest.getMetadataField())) {
				prevEntry.setField(metadataTemplateField);
				fieldOrValUpdated = true;
			}
			if (!prevEntry.getValue().equals(updateSampleMetadataRequest.getMetadataEntry())) {
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
		MetadataRestriction currentRestriction = metadataTemplateService
				.getMetadataRestrictionForFieldAndProject(project, metadataTemplateField);

		if (currentRestriction == null) {
			metadataTemplateService.setMetadataRestriction(project, metadataTemplateField, projectMetadataRole);
			sampleUpdated = true;
		} else {
			if (!currentRestriction.getLevel().equals(projectMetadataRole)) {
				currentRestriction.setLevel(projectMetadataRole);
				sampleUpdated = true;
				metadataRestrictionRepository.save(currentRestriction);
			}
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

	public SampleExportFiles getSampleExportFiles(Long sampleId, Long projectId) {
		Sample sample = sampleService.read(sampleId);
		// get the project if available
		Project project = null;
		if (projectId != null) {
			project = projectService.read(projectId);
		}

		List<PairedEndSequenceFileModel> filePairs = getPairedSequenceFilesForExportSample(sample, project).stream()
				.map(pair -> new PairedEndSequenceFileModel((SequenceFilePair) pair))
				.collect(Collectors.toList());
		List<SingleEndSequenceFileModel> singles = getSingleEndSequenceFilesForExportSample(sample, project).stream()
				.map(single -> new SingleEndSequenceFileModel((SingleEndSequenceFile) single))
				.collect(Collectors.toList());
		List<SequencingObject> fast5 = getFast5FilesForExportSample(sample);
		List<GenomeAssembly> genomeAssemblies = getGenomeAssembliesForExportSample(sample);

		return new SampleExportFiles(singles, filePairs, fast5, genomeAssemblies);
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
			if (sample.getDefaultSequencingObject() != null
					&& sample.getDefaultSequencingObject().getId() == sequencingObjectId) {
				sample.setDefaultSequencingObject(null);
				sampleService.update(sample);
			}
			sampleService.removeSequencingObjectFromSample(sample, sequencingObject);
			return messageSource.getMessage("server.SampleFiles.removeSequencingObjectSuccess", new Object[] {},
					locale);
		} catch (Exception e) {
			return messageSource.getMessage("server.SampleFiles.removeError",
					new Object[] { sequencingObject.getLabel() }, locale);
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
			if (sample.getDefaultGenomeAssembly() != null
					&& sample.getDefaultGenomeAssembly().getId() == genomeAssemblyId) {
				sample.setDefaultGenomeAssembly(null);
				sampleService.update(sample);
			}
			genomeAssemblyService.removeGenomeAssemblyFromSample(sample, genomeAssemblyId);
			return messageSource.getMessage("server.SampleFiles.removeGenomeAssemblySuccess", new Object[] {}, locale);
		} catch (Exception e) {
			return messageSource.getMessage("server.SampleFiles.removeError",
					new Object[] { genomeAssembly.getLabel() }, locale);
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
		response.setHeader("Content-Disposition", "attachment; filename=\"" + genomeAssembly.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}

	/**
	 * Get details about the files belonging to a list of samples
	 *
	 * @param sampleIds - List of sample identifiers to get file details for
	 * @param projectId - the project id that these samples belong to
	 * @return A map of sample id and their related file information
	 */
	public SampleFilesResponse getFilesForSamples(List<Long> sampleIds, Long projectId) {
		SampleFilesResponse response = new SampleFilesResponse();
		sampleIds.stream().forEach(id -> response.put(id, getSampleExportFiles(id, projectId)));
		return response;
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
						.filter(j -> sequencingObjectIds.contains(j.getObject().getId()))
						.collect(Collectors.toList());

		// add project to qc entries and filter any unavailable entries
		List<SampleSequencingObjectFileModel> filePairs = new ArrayList<>();
		for (SampleSequencingObjectJoin join : filePairJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			SequenceFilePair sfp = (SequenceFilePair) obj;
			String firstFileSize = sfp.getForwardSequenceFile().getFileSize();
			String secondFileSize = sfp.getReverseSequenceFile().getFileSize();

			filePairs.add(new SampleSequencingObjectFileModel(obj, firstFileSize, secondFileSize, obj.getQcEntries(),
					obj.getAutomatedAssembly()));
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
						.filter(j -> sequencingObjectIds.contains(j.getObject().getId()))
						.collect(Collectors.toList());

		List<SampleSequencingObjectFileModel> singles = new ArrayList<>();
		for (SampleSequencingObjectJoin join : singleFileJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			SingleEndSequenceFile sf = (SingleEndSequenceFile) obj;
			String fileSize = sf.getSequenceFile().getFileSize();
			singles.add(new SampleSequencingObjectFileModel(obj, fileSize, null, obj.getQcEntries()));
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
						.filter(j -> sequencingObjectIds.contains(j.getObject().getId()))
						.collect(Collectors.toList());

		List<SampleSequencingObjectFileModel> fast5Files = new ArrayList<>();
		for (SampleSequencingObjectJoin join : fast5FileJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			Fast5Object f5 = (Fast5Object) obj;
			String fileSize = f5.getFile().getFileSize();
			fast5Files.add(new SampleSequencingObjectFileModel(obj, fileSize, null, obj.getQcEntries()));
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
	 * Adds the {@link Project} to any {@link QCEntry} within a {@link SequencingObject}. If the {@link QCEntry} reports
	 * as {@link QCEntry.QCEntryStatus#UNAVAILABLE} after being enhanced it is removed from the list
	 *
	 * @param obj     the {@link SequencingObject} to enhance
	 * @param project the {@link Project} to add
	 */
	public void enhanceQcEntries(SequencingObject obj, Project project) {
		Set<QCEntry> availableEntries = new HashSet<>();
		if (obj.getQcEntries() != null) {
			for (QCEntry q : obj.getQcEntries()) {
				q.addProjectSettings(project);
				if (!q.getStatus().equals(QCEntry.QCEntryStatus.UNAVAILABLE)) {
					availableEntries.add(q);
				}
			}
		}

		obj.setQcEntries(availableEntries);
	}

	/**
	 * @param request Request containing the details of the move
	 * @param locale  current users {@link Locale}
	 * @throws UIShareSamplesException if project or samples cannot be found
	 */
	public void shareSamplesWithProject(ShareSamplesRequest request, Locale locale) throws UIShareSamplesException {
		Project currentProject = projectService.read(request.getCurrentId());
		Project targetProject = projectService.read(request.getTargetId());

		List<Sample> samples = (List<Sample>) sampleService.readMultiple(request.getSampleIds());
		if (request.getRemove()) {
			try {
				projectService.moveSamples(currentProject, targetProject, samples);
			} catch (Exception e) {
				throw new UIShareSamplesException(messageSource.getMessage("server.ShareSamples.move-error",
						new Object[] { targetProject.getLabel() }, locale));
			}
		} else {
			try {
				projectService.shareSamples(currentProject, targetProject, samples, !request.getLocked());
			} catch (Exception e) {
				throw new UIShareSamplesException(messageSource.getMessage("server.ShareSamples.copy-error",
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
	public List<SampleSequencingObjectFileModel> uploadFast5Files(Long sampleId, MultipartHttpServletRequest request)
			throws IOException {
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
	 * Get a page of samples based on the current state of the table options (filters, sort, and pagination)
	 *
	 * @param projectId Identifier for the current project.
	 * @param request   Information about the state of the table (filters, sort, and pagination).
	 * @param locale    current users {@link Locale}
	 * @return a page of samples
	 */
	public AntTableResponse<ProjectSampleTableItem> getPagedProjectSamples(Long projectId,
			ProjectSamplesTableRequest request, Locale locale) {
		List<Long> projectIds = new ArrayList<>();
		projectIds.add(projectId);
		ProjectSamplesFilter filter = request.getFilters();
		if (filter.getAssociated() != null) {
			projectIds.addAll(filter.getAssociated());
		}
		List<Project> projects = (List<Project>) projectService.readMultiple(projectIds);

		ProjectSampleJoinSpecification filterSpec = new ProjectSampleJoinSpecification();
		for (AntSearch search : request.getSearch()) {
			filterSpec.add(new SearchCriteria(search.getProperty(), search.getValue(),
					SearchOperation.fromString(search.getOperation())));
		}

		Page<ProjectSampleJoin> page = sampleService.getFilteredProjectSamples(projects, filterSpec, request.getPage(),
				request.getPageSize(), request.getSort());

		return new AntTableResponse<>(formatSamplesForTable(page, locale), page.getTotalElements());
	}

	/**
	 * Get a list of all samples in the current project and associated project that have been filtered, return a minimal
	 * * representation of them.
	 *
	 * @param projectId Identifier for the current project.
	 * @param request   Details about the filters and associated projects
	 * @return list containing a minimal representation of the samples based on the filters
	 */
	@Transactional(readOnly = true)
	public List<ProjectCartSample> getMinimalSampleDetailsForFilteredProject(Long projectId,
			ProjectSamplesTableRequest request) {
		ProjectSamplesFilter filter = request.getFilters();
		List<ProjectCartSample> filteredProjectSamples = new ArrayList<>();

		List<Long> projectIds = new ArrayList<>();
		projectIds.add(projectId);
		if (filter.getAssociated() != null) {
			projectIds.addAll(filter.getAssociated());
		}
		List<Project> projects = (List<Project>) projectService.readMultiple(projectIds);

		ProjectSampleJoinSpecification filterSpec = new ProjectSampleJoinSpecification();
		for (AntSearch search : request.getSearch()) {
			filterSpec.add(new SearchCriteria(search.getProperty(), search.getValue(),
					SearchOperation.fromString(search.getOperation())));
		}

		Page<ProjectSampleJoin> page = sampleService.getFilteredProjectSamples(projects, filterSpec, 0, MAX_PAGE_SIZE,
				request.getSort());

		while (!page.isEmpty()) {
			page.getContent().forEach(psj -> filteredProjectSamples.add(new ProjectCartSample(psj)));
			// Get the next page
			page = sampleService.getFilteredProjectSamples(projects, filterSpec, page.getNumber() + 1, MAX_PAGE_SIZE,
					request.getSort());
		}

		return filteredProjectSamples;
	}

	/**
	 * Merge 1 or more samples into another sample
	 *
	 * @param projectId identifier for the current project
	 * @param request   details about the samples to merge
	 * @param locale    current users locale information
	 * @return result of the merge
	 * @throws SampleMergeException thrown if there is an error during the merge
	 */
	public String mergeSamples(Long projectId, MergeRequest request, Locale locale) throws SampleMergeException {
		Project project = projectService.read(projectId);
		Sample primarySample = sampleService.read(request.getPrimary());

		if (!Strings.isNullOrEmpty(request.getNewName())) {
			primarySample.setSampleName(request.getNewName());
			try {
				sampleService.update(primarySample);
			} catch (EntityNotFoundException | ConstraintViolationException e) {
				throw new SampleMergeException(
						messageSource.getMessage("server.MergeModal.merged-error", new Object[] {}, locale));
			}
		}

		List<Sample> samples = (List<Sample>) sampleService.readMultiple(request.getIds());
		sampleService.mergeSamples(project, primarySample, samples);
		if (request.getIds().size() == 1) {
			return messageSource.getMessage("server.MergeModal.merged-single",
					new Object[] { samples.get(0).getSampleName(), primarySample.getSampleName() }, locale);
		} else {
			return messageSource.getMessage("server.MergeModal.merged-plural",
					new Object[] { samples.size(), primarySample.getSampleName() }, locale);
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

				sampleGenomeAssemblyFileModels
						.add(new SampleGenomeAssemblyFileModel(genomeAssembly, uploadedAssembly.getFileSize()));

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
	public List<SampleSequencingObjectFileModel> concatenateSequenceFiles(Long sampleId, Set<Long> objectIds,
			String filename, boolean removeOriginals) throws ConcatenateException {
		Sample sample = sampleService.read(sampleId);
		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = new ArrayList<>();
		Iterable<SequencingObject> readMultiple = sequencingObjectService.readMultiple(objectIds);

		try {
			SampleSequencingObjectJoin concatenatedSequencingObjects = sequencingObjectService
					.concatenateSequences(Lists.newArrayList(readMultiple), filename, sample, removeOriginals);

			if (removeOriginals) {
				for (SequencingObject sequencingObject : readMultiple) {
					if (sample.getDefaultSequencingObject() != null
							&& sample.getDefaultSequencingObject().getId().equals(sequencingObject.getId())) {
						sample.setDefaultSequencingObject(null);
						sampleService.update(sample);
						break;
					}
				}
			}

			SequencingObject sequencingObject = concatenatedSequencingObjects.getObject();
			String firstFileSize;
			String secondFileSize = null;
			if (sequencingObject.getFiles().size() == 1) {
				firstFileSize = sequencingObject.getFiles().stream().findFirst().get().getFileSize();
			} else {
				SequenceFilePair s = (SequenceFilePair) sequencingObject;
				firstFileSize = s.getForwardSequenceFile().getFileSize();
				secondFileSize = s.getReverseSequenceFile().getFileSize();
			}
			sampleSequencingObjectFileModels.add(new SampleSequencingObjectFileModel(sequencingObject, firstFileSize,
					secondFileSize, sequencingObject.getQcEntries()));
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
		MetadataTemplateField metadataTemplateField = metadataTemplateService
				.readMetadataField(metadataTemplateFieldId);
		MetadataRestriction metadataRestriction = metadataRestrictionRepository
				.getRestrictionForFieldAndProject(project, metadataTemplateField);
		if (metadataRestriction != null) {
			return metadataRestriction.getLevel();
		}

		return null;
	}

	/**
	 * Create {@link SequenceFile}'s then add them as {@link SequenceFilePair} to a {@link Sample}
	 *
	 * @param pair   {@link List} of {@link MultipartFile}
	 * @param sample {@link Sample} to add the pair to.
	 * @return A new {@link SampleSequencingObjectFileModel}
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SampleSequencingObjectFileModel createSequenceFilePairsInSample(List<MultipartFile> pair, Sample sample)
			throws IOException {
		SequenceFile firstFile = createSequenceFile(pair.get(0));
		SequenceFile secondFile = createSequenceFile(pair.get(1));
		SequencingObject sequencingObject = sequencingObjectService
				.createSequencingObjectInSample(new SequenceFilePair(firstFile, secondFile), sample)
				.getObject();
		return new SampleSequencingObjectFileModel(sequencingObject, firstFile.getFileSize(), secondFile.getFileSize(),
				sequencingObject.getQcEntries());
	}

	/**
	 * Create a {@link SequenceFile} and add it to a {@link Sample}
	 *
	 * @param file   {@link MultipartFile}
	 * @param sample {@link Sample} to add the file to.
	 * @return A new {@link SampleSequencingObjectFileModel}
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SampleSequencingObjectFileModel createSequenceFileInSample(MultipartFile file, Sample sample)
			throws IOException {
		SequenceFile sequenceFile = createSequenceFile(file);
		SequencingObject sequencingObject = sequencingObjectService
				.createSequencingObjectInSample(new SingleEndSequenceFile(sequenceFile), sample)
				.getObject();
		return new SampleSequencingObjectFileModel(sequencingObject, sequenceFile.getFileSize(), null,
				sequencingObject.getQcEntries());
	}

	/**
	 * Create a {@link Fast5Object} and add it to a {@link Sample}
	 *
	 * @param file   {@link MultipartFile}
	 * @param sample {@link Sample} to add the file to.
	 * @return A new {@link SampleSequencingObjectFileModel}
	 * @throws IOException Exception thrown if there is an error handling the file.
	 */
	private SampleSequencingObjectFileModel createFast5FileInSample(MultipartFile file, Sample sample)
			throws IOException {
		SequenceFile sequenceFile = createSequenceFile(file);
		SequencingObject sequencingObject = sequencingObjectService
				.createSequencingObjectInSample(new Fast5Object(sequenceFile), sample)
				.getObject();
		return new SampleSequencingObjectFileModel(sequencingObject, sequenceFile.getFileSize(), null,
				sequencingObject.getQcEntries());
	}

	/**
	 * Private method to move the sequence file into the correct directory and create the {@link SequenceFile} object.
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

	/**
	 * Remove 1 or more samples from a project.
	 *
	 * @param projectId identifier for the project
	 * @param sampleIds list of sampleIds to remove
	 */
	public void removeSamplesFromProject(Long projectId, List<Long> sampleIds) {
		Project project = projectService.read(projectId);
		projectService.removeSamplesFromProject(project, sampleService.readMultiple(sampleIds));
	}

	/**
	 * Get the sequence files for a list of samples
	 *
	 * @param projectId Identifier for the project
	 * @param sampleIds List of identifiers for samples
	 * @param response  {@link HttpServletResponse}
	 * @return Zip File containing sequence files for listed samples
	 */
	public StreamingResponseBody downloadSamples(Long projectId, List<Long> sampleIds, HttpServletResponse response) {
		Project project = projectService.read(projectId);
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(sampleIds);

		StreamingResponseBody body = out -> {
			final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
			ZipEntry zipEntry;

			// storing used file names to ensure we don't have a conflict
			Set<String> usedFileNames = new HashSet<>();

			try {
				for (Sample sample : samples) {
					Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = sequencingObjectService
							.getSequencingObjectsForSample(sample);

					for (SampleSequencingObjectJoin join : sequencingObjectsForSample) {
						for (SequenceFile file : join.getObject().getFiles()) {
							Path path = file.getFile();

							String fileName = project.getName() + "/" + sample.getSampleName() + "/"
									+ path.getFileName().toString();
							if (usedFileNames.contains(fileName)) {
								fileName = handleDuplicate(fileName, usedFileNames);
							}
							zipEntry = new ZipEntry(fileName);
							// set the file creation time on the zip entry to be
							// whatever the creation time is on the filesystem
							final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
							zipEntry.setCreationTime(attr.creationTime());
							zipEntry.setLastModifiedTime(attr.creationTime());

							zipOutputStream.putNextEntry(zipEntry);
							usedFileNames.add(fileName);
							Files.copy(path, zipOutputStream);
							zipOutputStream.closeEntry();
						}
					}
				}
				zipOutputStream.finish();
			} catch (IOException e) {
				// Do something here
			} finally {
				response.getOutputStream().close();
			}

		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=example.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
		return body;
	}

	/**
	 * Rename a filename {@code original} and ensure it doesn't exist in {@code usedNames}. Uses the windows style of
	 * renaming file.ext to file (1).ext
	 *
	 * @param original  original file name
	 * @param usedNames names that original must not conflict with
	 * @return modified name
	 */
	private String handleDuplicate(String original, Set<String> usedNames) {
		int lastDot = original.lastIndexOf('.');

		int index = 0;
		String result;
		do {
			index++;
			result = original.substring(0, lastDot) + " (" + index + ")" + original.substring(lastDot);
		} while (usedNames.contains(result));

		return result;
	}

	/**
	 * Download the currently filtered project samples table as either an xlsx or csv file.
	 *
	 * @param projectId Identifier for the project
	 * @param type      The type of file to generate (either excel or csv)
	 * @param request   The project samples table request
	 * @param response  The response
	 * @param locale    The current locale
	 * @throws IOException If there is an error writing the file
	 */
	@Transactional(readOnly = true)
	public void downloadSamplesSpreadsheet(Long projectId, String type, ProjectSamplesTableRequest request,
			HttpServletResponse response, Locale locale) throws IOException {
		ProjectSamplesFilter filter = request.getFilters();
		List<Long> projectIds = new ArrayList<>();
		projectIds.add(projectId);
		if (filter.getAssociated() != null) {
			projectIds.addAll(filter.getAssociated());
		}

		ProjectSampleJoinSpecification filterSpec = new ProjectSampleJoinSpecification();
		for (AntSearch search : request.getSearch()) {
			filterSpec.add(new SearchCriteria(search.getProperty(), search.getValue(),
					SearchOperation.fromString(search.getOperation())));
		}

		// Get all possible samples with this filter
		// NOTE: THIS IS AN EXPENSIVE OPERATION!!!
		List<Project> projects = (List<Project>) projectService.readMultiple(projectIds);
		List<ProjectSampleTableItem> items = new ArrayList<>();

		Page<ProjectSampleJoin> page = sampleService.getFilteredProjectSamples(projects, filterSpec, 0, MAX_PAGE_SIZE,
				request.getSort());
		while (!page.isEmpty()) {
			items.addAll(formatSamplesForTable(page, locale));

			// Get the next page
			page = sampleService.getFilteredProjectSamples(projects, filterSpec, page.getNumber() + 1, MAX_PAGE_SIZE,
					request.getSort());
		}

		List<String> headers = TABLE_HEADERS.stream()
				.map(header -> messageSource.getMessage(header, new Object[] {}, locale))
				.collect(Collectors.toList());

		String filename = projects.get(projects.size() - 1).getName().replaceAll(" ", "_").toLowerCase();
		if (type.equals("excel")) {
			writeToExcel(response, filename, items, headers, locale);
		} else {
			writeToCSV(response, filename, items, headers, locale);
		}
	}

	/**
	 * Get a list of paired end sequence files for a sample
	 *
	 * @param sample  the {@link Sample} to get the files for.
	 * @param project the {@link Project} the sample belongs to
	 * @return list of paired end sequence files
	 */
	public List<SequencingObject> getPairedSequenceFilesForExportSample(Sample sample, Project project) {
		Collection<SampleSequencingObjectJoin> filePairJoins = sequencingObjectService
				.getSequencesForSampleOfType(sample, SequenceFilePair.class);
		// add project to qc entries and filter any unavailable entries
		List<SequencingObject> filePairs = new ArrayList<>();
		for (SampleSequencingObjectJoin join : filePairJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			filePairs.add(obj);
		}

		return filePairs;
	}

	/**
	 * Get a list of single end sequence files for a sample
	 *
	 * @param sample  the {@link Sample} to get the files for.
	 * @param project the {@link Project} the sample belongs to
	 * @return list of single end sequence files
	 */
	public List<SequencingObject> getSingleEndSequenceFilesForExportSample(Sample sample, Project project) {
		Collection<SampleSequencingObjectJoin> singleFileJoins = sequencingObjectService
				.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class);

		List<SequencingObject> singles = new ArrayList<>();
		for (SampleSequencingObjectJoin join : singleFileJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			singles.add(obj);
		}

		return singles;
	}

	/**
	 * Get a list of fast5 sequence files for a sample
	 *
	 * @param sample the {@link Sample} to get the files for.
	 * @return list of fast5 sequence files
	 */
	public List<SequencingObject> getFast5FilesForExportSample(Sample sample) {
		Collection<SampleSequencingObjectJoin> fast5FileJoins = sequencingObjectService
				.getSequencesForSampleOfType(sample, Fast5Object.class);
		return fast5FileJoins.stream().map(SampleSequencingObjectJoin::getObject).collect(Collectors.toList());
	}

	/**
	 * Get any genome assemblies that are available for a sample
	 *
	 * @param sample the {@link Sample} to get the assemblies for
	 * @return a list of genome assembly files
	 */
	public List<GenomeAssembly> getGenomeAssembliesForExportSample(Sample sample) {
		Collection<SampleGenomeAssemblyJoin> genomeAssemblyJoins = genomeAssemblyService.getAssembliesForSample(sample);

		return genomeAssemblyJoins.stream().map(SampleGenomeAssemblyJoin::getObject).collect(Collectors.toList());
	}

	/**
	 * Format a Page of ProjectSampleJoin's into a format to be consumed by the Ant Design Table.
	 *
	 * @param page   Page of {@link ProjectSampleJoin}
	 * @param locale Current users locale
	 * @return List of {@link ProjectSampleTableItem}
	 */
	private List<ProjectSampleTableItem> formatSamplesForTable(Page<ProjectSampleJoin> page, Locale locale) {
		Map<Project, List<Long>> projectSampleIdsMap = page.getContent()
				.stream()
				.collect(Collectors.groupingBy(ProjectSampleJoin::getSubject,
						Collectors.mapping(join -> join.getObject().getId(), Collectors.toList())));

		Map<Project, Map<Long, Long>> projectSamplesCoverageMap = new HashMap<Project, Map<Long, Long>>();
		projectSampleIdsMap.forEach((project, sampleIds) -> projectSamplesCoverageMap.put(project,
				sampleService.getCoverageForSamplesInProject(project, sampleIds)));

		List<Sample> samples = page.getContent().stream().map(join -> join.getObject()).collect(Collectors.toList());
		Map<Long, List<QCEntry>> sampleQCEntries = sampleService.getQCEntriesForSamples(samples);

		return page.getContent().stream().map(join -> {
			Sample sample = join.getObject();
			Project project = join.getSubject();

			Long coverage = null;
			if (projectSamplesCoverageMap.containsKey(project)
					&& projectSamplesCoverageMap.get(project).containsKey(sample.getId())) {
				coverage = projectSamplesCoverageMap.get(project).get(sample.getId());
			}

			List<QCEntry> qcEntriesForSample = sampleQCEntries.get(sample.getId());
			List<String> quality = new ArrayList<>();
			String qcStatus = null;

			// If the sample has any SequencingObjects we will have at minimum CoverageQCEntry's
			// which can be checked to set QCStatus.
			if (qcEntriesForSample != null) {
				qcEntriesForSample.forEach(entry -> {
					entry.addProjectSettings(project);
					if (entry.getStatus() == QCEntry.QCEntryStatus.NEGATIVE) {
						quality.add(messageSource.getMessage("sample.files.qc." + entry.getType(),
								new Object[] { entry.getMessage() }, locale));
					}
				});
				// set qcStatus based on filtered qcEntries
				if (quality.size() == 0) {
					qcStatus = "pass";
				} else {
					qcStatus = "fail";
				}
			}
			return new ProjectSampleTableItem(join, quality, qcStatus, coverage);
		}).collect(Collectors.toList());
	}

	/**
	 * Write samples table to an Excel file
	 *
	 * @param response {@link HttpServletResponse}
	 * @param filename {@link String} name of the file to download.
	 * @param items    Data to download in the table
	 * @param headers  for the table
	 * @param locale   Current users locale
	 * @throws IOException thrown if file cannot be written
	 */
	private void writeToExcel(HttpServletResponse response, String filename, List<ProjectSampleTableItem> items,
			List<String> headers, Locale locale) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();

		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle dateCellStyle = workbook.createCellStyle();
		String excelDatetimeFormatPattern = DateFormatConverter.convert(locale,
				messageSource.getMessage("generic.datetimeformat", new Object[] {}, locale));
		short dateTimeFormat = creationHelper.createDataFormat().getFormat(excelDatetimeFormatPattern);
		dateCellStyle.setDataFormat(dateTimeFormat);

		// Create the header row
		Row row = sheet.createRow(0);
		for (int i = 0; i < headers.size(); i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(headers.get(i));
		}

		// Add the data to the workbook
		int rowNum = 1;
		for (ProjectSampleTableItem item : items) {
			int cellNum = 0;
			SampleObject sample = item.getSample();
			ProjectObject project = item.getProject();
			row = sheet.createRow(rowNum++);

			Cell sampleNameCell = row.createCell(cellNum++);
			sampleNameCell.setCellValue(sample.getSampleName());

			Cell sampleIdCell = row.createCell(cellNum++);
			sampleIdCell.setCellValue(sample.getId());

			Cell sampleQualityCell = row.createCell(cellNum++);
			sampleQualityCell.setCellValue(StringUtils.join(item.getQuality(), "; "));

			Cell sampleCoverageCell = row.createCell(cellNum++);
			if (item.getCoverage() != null) {
				sampleCoverageCell.setCellValue(item.getCoverage());
			}

			Cell sampleOrganismCell = row.createCell(cellNum++);
			sampleOrganismCell.setCellValue(sample.getOrganism());

			Cell projectNameCell = row.createCell(cellNum++);
			projectNameCell.setCellValue(project.getName());

			Cell projectIdCell = row.createCell(cellNum++);
			projectIdCell.setCellValue(project.getId());

			Cell createdByCell = row.createCell(cellNum++);
			createdByCell.setCellValue(sample.getCollectedBy());

			Cell createdCell = row.createCell(cellNum++);
			createdCell.setCellValue(sample.getCreatedDate());
			createdCell.setCellStyle(dateCellStyle);

			Cell modifiedCell = row.createCell(cellNum);
			modifiedCell.setCellValue(sample.getModifiedDate());
			modifiedCell.setCellStyle(dateCellStyle);
		}

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
		workbook.write(response.getOutputStream());

		workbook.close();
	}

	/**
	 * Write samples table to a CSV file.
	 *
	 * @param response {@link HttpServletResponse}
	 * @param filename {@link String} name of the file to download.
	 * @param items    {@link ProjectSampleTableItem} details about each row of the table Data to download in the table
	 * @param headers  for the table
	 * @param locale   Current users locale
	 * @throws IOException thrown if file cannot be written
	 */
	private void writeToCSV(HttpServletResponse response, String filename, List<ProjectSampleTableItem> items,
			List<String> headers, Locale locale) throws IOException {
		List<String[]> results = new ArrayList<>();
		results.add(headers.toArray(String[]::new));

		for (ProjectSampleTableItem item : items) {
			//			results.add(model.getExportableTableRow().toArray(new String[0]));
			SampleObject sample = item.getSample();
			ProjectObject project = item.getProject();
			String[] row = {
					sample.getSampleName(),
					sample.getId().toString(),
					StringUtils.join(item.getQuality(), "; "),
					item.getCoverage() != null ? item.getCoverage().toString() : "",
					sample.getOrganism(),
					project.getName(),
					project.getId().toString(),
					sample.getCollectedBy(),
					sample.getCreatedDate().toString(),
					sample.getModifiedDate().toString() };
			results.add(row);
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".csv\"");
		response.setContentType("text/csv");
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream());
		CSVPrinter printer = CSVFormat.DEFAULT.print(outputStreamWriter);
		printer.printRecords(results);
		printer.flush();
		outputStreamWriter.close();
	}
}
