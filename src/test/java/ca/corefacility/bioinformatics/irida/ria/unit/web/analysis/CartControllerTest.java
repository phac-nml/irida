package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartRequest;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartRequestSample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.dto.CartResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {
	SampleService sampleService;
	ProjectService projectService;
	UserService userService;
	MessageSource messageSource;

	CartController controller;
	SequencingObjectService sequencingObjectService;
	Cart cart;

	private Long projectId;
	Set<Long> sampleIds;
	private Project project;
	private Set<Sample> samples;

	@Before
	public void setup() {
		sampleService = mock(SampleService.class);
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		messageSource = mock(MessageSource.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		cart = new Cart(projectService, messageSource);

		controller = new CartController(sampleService, userService, projectService, sequencingObjectService, cart);

		testData();
	}

	@Test
	public void testAddProjectSample() {
		CartRequest request = new CartRequest(1L,
				ImmutableSet.of(new CartRequestSample(1L, "sample1"), new CartRequestSample(2L, "sample2"),
						new CartRequestSample(3L, "sample3")));
		CartResponse response = controller.addSamplesToCart(request, Locale.ENGLISH);
		assertEquals("Should have 3 samples in the cart", 3, response.getCount());
		assertNull("Should be no duplicated", response.getDuplicate());
		assertNull("Should be no existing", response.getExisting());

		// Try adding a sample the second time.
		CartRequest secondRequest = new CartRequest(1L, ImmutableSet.of(new CartRequestSample(1L, "sample1"), new CartRequestSample(4L, "sample4")));
		CartResponse secondsResponse = controller.addSamplesToCart(secondRequest, Locale.ENGLISH);
		assertNotNull("Should indicate that there was an existing sample added", secondsResponse.getExisting());
		assertEquals("Should now be 4 samples in the cart", 4, secondsResponse.getCount());
		assertNull("Should be no duplicated", response.getDuplicate());

		// Try adding a sample with a duplicate sample name, but different ID.
		CartRequest thirdRequest = new CartRequest(1L, ImmutableSet.of(new CartRequestSample(5L, "sample1")));
		CartResponse thirdResponse = controller.addSamplesToCart(thirdRequest, Locale.ENGLISH);
		assertNotNull("Should give a duplicate sample name message", thirdResponse);
		assertEquals("Should not have added the diplicate to the cart", 4, thirdResponse.getCount());
	}

	@Test
	public void testRemoveProjectSamples() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);

		Set<Long> subIds = Sets.newHashSet(sampleIds.iterator().next());

		Map<String, Object> addProjectSample = controller.removeProjectSamples(projectId, subIds);

		assertTrue((boolean) addProjectSample.get("success"));

		verify(projectService).read(projectId);
		verify(sampleService).getSamplesInProject(project, new ArrayList<>(subIds));

		selected = controller.getSelected();

		assertEquals(1, selected.keySet().size());
		Project projectKey = selected.keySet().iterator().next();
		assertEquals(project, projectKey);
		for (Sample s : selected.get(projectKey)) {
			assertFalse(subIds.contains(s.getId()));
		}
	}

	@Test
	public void testRemoveProjectSample() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);
		Sample sample = samples.iterator().next();
		Map<String, Object> removeProjectSample = controller.removeProjectSample(projectId, sample.getId());

		assertTrue((boolean) removeProjectSample.get("success"));

		selected = controller.getSelected();
		assertEquals(1, selected.keySet().size());
		assertFalse(selected.get(project).contains(sample));

	}

	@Test
	public void testRemoveAllProjectSamples() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);

		Map<String, Object> addProjectSample = controller.removeProjectSamples(projectId, sampleIds);

		assertTrue((boolean) addProjectSample.get("success"));

		verify(projectService).read(projectId);
		verify(sampleService).getSamplesInProject(project, Lists.newArrayList(sampleIds));

		selected = controller.getSelected();

		assertFalse("project should have been removed because all samples were removed", selected.containsKey(project));
	}

	@Test
	public void testClearCart() {
		Map<String, Object> clearCart = controller.clearCart();
		assertTrue((boolean) clearCart.get("success"));

		Map<Project, Set<Sample>> selected = controller.getSelected();
		assertTrue(selected.isEmpty());
	}

	@Test
	public void testAddProject() {
//		Map<String, Object> addProject = controller.addProject(projectId);
//		assertTrue((boolean) addProject.get("success"));
//
//		List<Join<Project, Sample>> joins = new ArrayList<>();
//		samples.forEach((s) -> {
//			joins.add(new ProjectSampleJoin(project, s, true));
//		});
//		when(sampleService.getSamplesForProject(project)).thenReturn(joins);
//
//		verify(projectService).read(projectId);
//		verify(sampleService).getSamplesForProject(project);
//
//		Map<Project, Set<Sample>> selected = controller.getSelected();
//		assertEquals(1, selected.keySet().size());
//		Project projectKey = selected.keySet().iterator().next();
//		assertEquals(project, projectKey);
//		for (Sample s : selected.get(projectKey)) {
//			assertTrue(sampleIds.contains(s.getId()));
//		}
	}

	@Test
	public void testRemoveProject() {
		controller.removeProject(projectId);
		verify(projectService).read(projectId);
	}

	@Test
	public void testGetCartMap() {
		RequestAttributes ra = new ServletRequestAttributes(new MockHttpServletRequest());
		RequestContextHolder.setRequestAttributes(ra);

		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);

		Map<String, Object> cartMap = controller.getCartMap();
		assertTrue(cartMap.containsKey("projects"));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> pList = (List<Map<String, Object>>) cartMap.get("projects");
		Map<String, Object> projectMap = pList.iterator().next();

		assertTrue(projectMap.containsKey("samples"));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sList = (List<Map<String, Object>>) projectMap.get("samples");
		for (Map<String, Object> map : sList) {
			assertTrue(map.containsKey("id"));
			assertTrue(map.containsKey("label"));
		}
	}

	private void testData() {
		projectId = 1L;
		sampleIds = Sets.newHashSet(2L, 3L);

		project = new Project("project");
		project.setId(projectId);
		samples = new HashSet<>();

		when(projectService.read(projectId)).thenReturn(project);
		for (Long id : sampleIds) {
			Sample sample = new Sample("sample" + id);
			sample.setId(id);
			samples.add(sample);
			when(sampleService.getSampleForProject(project, id)).thenReturn(new ProjectSampleJoin(project,sample, true));
		}
		final ArrayList<Long> ids = new ArrayList<>(sampleIds);

		when(messageSource.getMessage("cart.in-cart", new Object[] {}, Locale.ENGLISH)).thenReturn(
				"Sample already in cart");

		when(messageSource.getMessage("cart.excluded", new Object[] { "sample1" }, Locale.ENGLISH)).thenReturn(
				"Sample name is already in the cart, no duplicate names please.");

		when(sampleService.getSamplesInProject(project, ids)).thenReturn(new ArrayList<>(samples));
		ArrayList<Long> subIds = Lists.newArrayList(sampleIds.iterator().next());
		when(sampleService.getSamplesInProject(project, subIds)).thenReturn(samples.stream().filter(x -> Objects.equals(
				x.getId(), subIds.get(0))).collect(
				Collectors.toList()));
	}
}
