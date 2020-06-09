package ca.corefacility.bioinformatics.irida.model;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;

public class SampleTest {
	private static final String MESSAGES_BASENAME = "ValidationMessages";
	private Validator validator;
	
	private Sample sample1;
	private Sample sample2;

	private MetadataTemplateField field1;
	private MetadataTemplateField field2;

	private MetadataEntry entry1;
	private MetadataEntry entry2;

	private PipelineProvidedMetadataEntry pipelineEntry1;
	private PipelineProvidedMetadataEntry pipelineEntry2;

	private AnalysisSubmission submission1;

	@Before
	public void setUp() {
		Configuration<?> configuration = Validation.byDefaultProvider().configure();
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename(MESSAGES_BASENAME);
		configuration.messageInterpolator(new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator(
				MESSAGES_BASENAME)));
		ValidatorFactory factory = configuration.buildValidatorFactory();
		validator = factory.getValidator();
		
		sample1 = new Sample("newsample");

		field1 = new MetadataTemplateField("field1", "text");
		entry1 = new MetadataEntry("entry1", "text");

		field2 = new MetadataTemplateField("field2", "text");
		entry2 = new MetadataEntry("entry2", "text");

		Map<MetadataTemplateField, MetadataEntry> metadataMap = Maps.newHashMap();
		metadataMap.put(field1, entry1);
		metadataMap.put(field2, entry2);

		sample1.setMetadata(metadataMap);

		sample2 = new Sample("newsample2");

		submission1 = AnalysisSubmission.builder(UUID.randomUUID()).inputFiles(Sets.newHashSet(new SequenceFilePair()))
				.build();
		submission1.setId(1L);

		pipelineEntry1 = new PipelineProvidedMetadataEntry("pipelineEntry1", "text", submission1);
		pipelineEntry2 = new PipelineProvidedMetadataEntry("pipelineEntry2", "text", submission1);

		Map<MetadataTemplateField, MetadataEntry> metadataMap2 = Maps.newHashMap();
		metadataMap2.put(field1, pipelineEntry1);
		metadataMap2.put(field2, pipelineEntry2);

		sample2.setMetadata(metadataMap2);
	}
	
	@Test
	public void testNullSampleName() {
		Sample s = new Sample();
		s.setSampleName(null);

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);

		assertEquals("Wrong number of violations.", 2, violations.size());
	}
	
	@Test
	public void testEmptySampleName() {
		Sample s = new Sample();
		s.setSampleName("");

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);
		assertEquals("Wrong number of violations.", 1, violations.size());
	}

	@Test
	public void testInvalidSampleName() {
		Sample s = new Sample();
		s.setSampleName("This name has a single quote ' and spaces and a period.");

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);
		assertEquals("Wrong number of violations.", 3, violations.size());
	}

	@Test
	public void testBlacklistedCharactersInSampleName() {
		testBlacklists(ValidProjectName.ValidProjectNameBlacklist.BLACKLIST);
		testBlacklists(ValidSampleName.ValidSampleNameBlacklist.BLACKLIST);
	}

	@Test
	public void testMergeMetadataSuccess() {
		Map<MetadataTemplateField, MetadataEntry> inputMetadata = Maps.newHashMap();
		MetadataEntry entry = new MetadataEntry("entry2", "text");

		inputMetadata.put(field1, entry);

		sample1.mergeMetadata(inputMetadata);

		assertEquals("Metadata map does not have correct number of entries", 2, sample1.getMetadata().size());
		assertEquals("Updated metadata entry does not match", entry, sample1.getMetadata().get(field1));
		assertEquals("Non-updated metadata entry does not match", this.entry2, sample1.getMetadata().get(field2));
	}

	@Test
	public void testMergeMetadataPipelineSuccess() {
		Map<MetadataTemplateField, MetadataEntry> inputMetadata = Maps.newHashMap();

		AnalysisSubmission submission = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		submission.setId(2L);

		PipelineProvidedMetadataEntry entry = new PipelineProvidedMetadataEntry("entry2", "text", submission);

		inputMetadata.put(field1, entry);

		sample2.mergeMetadata(inputMetadata);

		assertEquals("Metadata map does not have correct number of entries", 2, sample2.getMetadata().size());
		assertEquals("Updated metadata entry does not match", entry, sample2.getMetadata().get(field1));

		PipelineProvidedMetadataEntry actualEntry = (PipelineProvidedMetadataEntry) sample2.getMetadata().get(field1);

		assertEquals("Updated metadata entry does not point to correct submission", Long.valueOf(2),
				actualEntry.getSubmission().getId());
		assertEquals("Non-updated metadata entry does not match", this.pipelineEntry2,
				sample2.getMetadata().get(field2));
	}

	@Test
	public void testMergeMetadataPipelineDifferentEntryTypes() {
		Map<MetadataTemplateField, MetadataEntry> inputMetadata = Maps.newHashMap();

		AnalysisSubmission submission = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair())).build();
		submission.setId(2L);

		MetadataEntry entry = new MetadataEntry("entry2", "text");

		inputMetadata.put(field1, entry);

		sample2.mergeMetadata(inputMetadata);

		assertEquals("Metadata map does not have correct number of entries", 2, sample2.getMetadata().size());
		assertEquals("Updated metadata entry does not match", entry, sample2.getMetadata().get(field1));
		assertEquals("Non-updated metadata entry does not match", this.pipelineEntry2,
				sample2.getMetadata().get(field2));
	}

	private void testBlacklists(char[] blacklist) {
		for (char c : blacklist) {
			Sample s = new Sample();
			s.setSampleName("ATLEAST3" + c);
			Set<ConstraintViolation<Sample>> violations = validator.validate(s);
			assertEquals("Wrong number of violations.", 1, violations.size());
		}
	}
}
