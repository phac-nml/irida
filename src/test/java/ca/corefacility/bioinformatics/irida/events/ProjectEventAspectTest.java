package ca.corefacility.bioinformatics.irida.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import ca.corefacility.bioinformatics.irida.events.annotations.LaunchesProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;

public class ProjectEventAspectTest {
	private ProjectEventAspect projectEventAspect;
	private ProjectEventHandler eventHandler;

	private AnnotatedClass annotatedClass;

	@Before
	public void setup() {
		eventHandler = mock(ProjectEventHandler.class);
		projectEventAspect = new ProjectEventAspect(eventHandler);
		annotatedClass = new AnnotatedClass();
		AspectJProxyFactory proxyFactory = new AspectJProxyFactory(annotatedClass);
		proxyFactory.addAspect(projectEventAspect);

		annotatedClass = proxyFactory.getProxy();

	}

	@Test
	public void testHandleProjectEvent() {
		String arg = "test";
		annotatedClass.returningMethod(arg);

		ArgumentCaptor<MethodEvent> eventCaptor = ArgumentCaptor.forClass(MethodEvent.class);
		verify(eventHandler).delegate(eventCaptor.capture());
		MethodEvent value = eventCaptor.getValue();

		assertEquals(TestProjectEvent.class, value.getEventClass());
		assertEquals(arg, value.getArgs()[0]);
		assertNotNull(value.getReturnValue());

	}

	@Test
	public void testHandleNonReturningProjectEvent() {
		String arg = "test";
		annotatedClass.nonReturningMethod(arg);

		ArgumentCaptor<MethodEvent> eventCaptor = ArgumentCaptor.forClass(MethodEvent.class);
		verify(eventHandler).delegate(eventCaptor.capture());
		MethodEvent value = eventCaptor.getValue();

		assertEquals(TestProjectEvent.class, value.getEventClass());
		assertEquals(arg, value.getArgs()[0]);

	}

	private static class AnnotatedClass {

		public AnnotatedClass() {
		}

		@LaunchesProjectEvent(TestProjectEvent.class)
		public void nonReturningMethod(String arg1) {
		}

		@LaunchesProjectEvent(TestProjectEvent.class)
		public String returningMethod(String arg1) {
			return "return";
		}
	}

	private static class TestProjectEvent extends ProjectEvent {

		@Override
		public String getLabel() {
			return "A wicked event";
		}
	}
}
