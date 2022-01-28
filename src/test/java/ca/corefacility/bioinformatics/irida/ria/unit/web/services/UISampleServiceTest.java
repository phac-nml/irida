package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareMetadataRestriction;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareSamplesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UISampleServiceTest {
	private UISampleService service;
	private ProjectService projectService;
	private final User USER_1 = new User("test", "test@nowhere.com", "PW1@3456", "Test", "Tester", "1234567890");
	private final Sample SAMPLE_1 = new Sample("SAMPLE_01");

	private final Long SAMPLE_ID = 313L;
	private final String SAMPLE_ORGANISM = "Salmonella";
	private final String SAMPLE_DESCRIPTION ="This is a project about interesting stuff";

	@BeforeEach
	public void setUp() {
		SampleService sampleService = mock(SampleService.class);
		projectService = mock(ProjectService.class);
		UpdateSamplePermission updateSamplePermission = mock(UpdateSamplePermission.class);
		SequencingObjectService sequencingObjectService = mock(SequencingObjectService.class);
		GenomeAssemblyService genomeAssemblyService = mock(GenomeAssemblyService.class);
		MetadataTemplateService metadataTemplateService = mock(MetadataTemplateService.class);
		MessageSource messageSource = mock(MessageSource.class);
		UICartService cartService = mock(UICartService.class);
		service = new UISampleService(sampleService, projectService, updateSamplePermission, sequencingObjectService,
				genomeAssemblyService, metadataTemplateService, messageSource, cartService);

		// DATA
		SAMPLE_1.setId(SAMPLE_ID);
		SAMPLE_1.setDescription(SAMPLE_DESCRIPTION);
		SAMPLE_1.setOrganism(SAMPLE_ORGANISM);
		USER_1.setSystemRole(Role.ROLE_ADMIN);
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		when(sampleService.read(1L)).thenReturn(SAMPLE_1);
		when(updateSamplePermission.isAllowed(authentication, SAMPLE_1)).thenReturn(true);
	}

	@Test
	public void testGetSampleDetails() {
		SampleDetails details = service.getSampleDetails(1L);
		final Sample sample = details.getSample();
		assertEquals(SAMPLE_ORGANISM, sample.getOrganism(), "Should return the proper samples organism");
		assertEquals(SAMPLE_DESCRIPTION, sample.getDescription(), "Should return the proper samples description");
		assertEquals(SAMPLE_ID, sample.getId(), "Should return the proper samples identifier");
	}

	@Test
	public void testShareSamplesWithProject() throws Exception {
		final Long CURRENT_PROJECT_ID = 1L;
		final Project CURRENT_PROJECT = new Project("CURRENT_PROJECT");
		CURRENT_PROJECT.setId(CURRENT_PROJECT_ID);
		when(projectService.read(CURRENT_PROJECT_ID)).thenReturn(CURRENT_PROJECT);

		final Long TARGET_PROJECT_ID = 2L;
		final Project TARGET_PROJECT = new Project("TARGET_PROJECT");
		TARGET_PROJECT.setId(TARGET_PROJECT_ID);
		when(projectService.read(TARGET_PROJECT_ID)).thenReturn(TARGET_PROJECT);

		ShareSamplesRequest request = new ShareSamplesRequest();
		request.setCurrentId(CURRENT_PROJECT_ID);
		request.setTargetId(TARGET_PROJECT_ID);
		request.setSampleIds(ImmutableList.of(SAMPLE_ID));
		request.setRestrictions(ImmutableList.of(new ShareMetadataRestriction(1L, "LEVEL_1")));
		request.setRemove(false);
		request.setLocked(false);
		service.shareSamplesWithProject(request, Locale.CANADA);
	}
}
