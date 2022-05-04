package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntPagination;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntSort;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareSamplesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UISampleServiceTest {
	private UISampleService service;
	private ProjectService projectService;
	private final User USER_1 = new User("test", "test@nowhere.com", "PW1@3456", "Test", "Tester", "1234567890");
	private final Sample SAMPLE_1 = new Sample("SAMPLE_01");

	private final Long SAMPLE_ID = 313L;
	private final String SAMPLE_ORGANISM = "Salmonella";
	private final String SAMPLE_DESCRIPTION = "This is a project about interesting stuff";
	private final Long PROJECT_ID_1 = 1L;
	private final Long PROJECT_ID_2 = 2L;
	private Project PROJECT_1 = new Project("PROJECT 1");
	private Project PROJECT_2 = new Project("PROJECT 2");
	private final ProjectSamplesTableRequest request = new ProjectSamplesTableRequest();
	private final ProjectSampleJoinSpecification specification = new ProjectSampleJoinSpecification();

	@BeforeEach
	public void setUp() {
		SampleService sampleService = mock(SampleService.class);
		projectService = mock(ProjectService.class);
		UpdateSamplePermission updateSamplePermission = mock(UpdateSamplePermission.class);
		SequencingObjectService sequencingObjectService = mock(SequencingObjectService.class);
		GenomeAssemblyService genomeAssemblyService = mock(GenomeAssemblyService.class);
		MessageSource messageSource = mock(MessageSource.class);
		UICartService cartService = mock(UICartService.class);
		service = new UISampleService(sampleService, projectService, updateSamplePermission, sequencingObjectService,
				genomeAssemblyService, messageSource, cartService);

		// DATA
		SAMPLE_1.setId(SAMPLE_ID);
		SAMPLE_1.setDescription(SAMPLE_DESCRIPTION);
		SAMPLE_1.setOrganism(SAMPLE_ORGANISM);
		USER_1.setSystemRole(Role.ROLE_ADMIN);
		PROJECT_1.setId(PROJECT_ID_1);
		PROJECT_2.setId(PROJECT_ID_2);
		AntPagination pagination = new AntPagination(0, 10);
		request.setPagination(pagination);
		request.setSearch(ImmutableList.of());
		request.setOrder(ImmutableList.of(new AntSort("modifiedDate", "desc")));

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		Sample sample2 = new Sample("SAMPLE_02");
		Sample sample3 = new Sample("SAMPLE_03");
		List<ProjectSampleJoin> joins = ImmutableList.of(new ProjectSampleJoin(PROJECT_1, SAMPLE_1, true),
				new ProjectSampleJoin(PROJECT_2, sample2, true), new ProjectSampleJoin(PROJECT_2, sample3, true));
		Page<ProjectSampleJoin> page = new PageImpl<>(joins);

		when(sampleService.read(1L)).thenReturn(SAMPLE_1);
		when(updateSamplePermission.isAllowed(authentication, SAMPLE_1)).thenReturn(true);
		when(sampleService.getFilteredProjectSamples(anyList(), any(ProjectSampleJoinSpecification.class), anyInt(),
				anyInt(), any(Sort.class))).thenReturn(page);
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
		request.setRemove(false);
		request.setLocked(false);
		service.shareSamplesWithProject(request, Locale.CANADA);
	}

	@Test
	public void testGetPagedProjectSamples() {
		AntTableResponse<ProjectSampleTableItem> response = service.getPagedProjectSamples(1L, request, Locale.CANADA);
		assertEquals(3, response.getTotal(), "Should return 3 items");
		List<?> items = response.getContent();
		items.forEach((item) -> {
			assertTrue(item instanceof ProjectSampleTableItem, "Should return ProjectSampleTableItems");
		});
	}
}
