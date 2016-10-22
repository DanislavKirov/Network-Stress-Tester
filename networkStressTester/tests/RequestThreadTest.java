package networkStressTester.tests;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import networkStressTester.Constants;
import networkStressTester.src.RequestThread;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.matchers.Times.exactly;

public class RequestThreadTest {

	private static List<String> request = new ArrayList<>();
	private static String expectedResponse = null;
	private static final CyclicBarrier barrier = new CyclicBarrier(1);
	private static ClientAndServer mockServer;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try (BufferedReader requestReader = new BufferedReader(new FileReader(Constants.getRequestFilePath()));
		BufferedReader responseReader = new BufferedReader(new FileReader(Constants.getExpectedResponseFilePath()))) {
			String line;
			while ((line = requestReader.readLine()) != null) {
				request.add(line);
			}
			expectedResponse = responseReader.readLine();
		} catch (IOException e) {
			request = null;
			expectedResponse = null;
		}	
	}
		
	@Before
	public void setUpMockServer() {
	    mockServer = startClientAndServer(1080);
	    mockServer
	    	.when(
	    			request()
	    					.withMethod("GET")
	    					.withPath("/")
	    					.withHeader(
	    							new Header("Host", "java.voidland.org")
	    					),
	    			exactly(1)
	    	)
	    	.respond(
	    			response()
	    					.withStatusCode(200)
	    	);
	}
	
	@Test
	public void requestShouldBeSuccessful() {
		RequestThread thread = new RequestThread("localhost", 1080, request, barrier);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		assertFalse(thread.isFailed());
		assertEquals(expectedResponse, thread.getResponse());
		assertNotEquals(0, thread.getTime());
	}

	@Test
	public void requestShouldNotBeSuccessful() {
		List<String> wrongRequest = Stream.of("Wrong", "request").collect(Collectors.toList());
		RequestThread thread = new RequestThread("localhost", 1080, wrongRequest, barrier);
		
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		assertFalse(thread.isFailed());
		assertNotEquals(expectedResponse, thread.getResponse());
		assertNotEquals(0, thread.getTime());
	}

	@After
	public void stopMockServer() {
	    mockServer.stop();
	}
}
