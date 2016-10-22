package networkStressTester.tests;

import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.matchers.Times.exactly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import networkStressTester.Constants;
import networkStressTester.src.NetworkStresser;

public class NetworStresserTest {

	private NetworkStresser ns = new NetworkStresser("localhost", 1080);
	private static ClientAndServer mockServer;
	
	@Test
	public void requestShouldBeNull() {
		ns.readRequest("notExistingPath");
		assertNull(ns.getRequest());		
	}
	
	@Test
	public void requestShoudBeRead() {
		ns.readRequest(Constants.getRequestFilePath());
		assertNotNull(ns.getRequest());
		
		List<String> request = new ArrayList<>();
		try (BufferedReader requestReader = new BufferedReader(new FileReader(Constants.getRequestFilePath()))) {
					String line;
					while ((line = requestReader.readLine()) != null) {
						request.add(line);
					}
				} catch (IOException e) {
					request = null;
				}			
		assertTrue(request.equals(ns.getRequest()));
	}
	
	@Test
	public void expectedResponseShouldBeNull() {
		ns.readExpectedResponse("notExistingPath");
		assertNull(ns.getExpectedResponse());		
	}
	
	@Test
	public void expectedResponseShouldBeRead() {
		ns.readExpectedResponse(Constants.getExpectedResponseFilePath());
		assertNotNull(ns.getExpectedResponse());

		String expectedResponse = null;
		try (BufferedReader responseReader = new BufferedReader(new FileReader(Constants.getExpectedResponseFilePath()))) {
					expectedResponse = responseReader.readLine();
				} catch (IOException e) {
					expectedResponse = null;
				}			
		assertEquals(expectedResponse, ns.getExpectedResponse());
	}
	
	@Ignore
	public void mockServerShouldBreakAt20threads() {
	    mockServer = startClientAndServer(1080);
	    mockServer
	    	.when(
	    			request()
	    					.withMethod("GET")
	    					.withPath("/")
	    					.withHeader(
	    							new Header("Host", "java.voidland.org")
	    					),
	    			exactly(20)
	    	)
	    	.respond(
	    			response()
	    					.withStatusCode(200)
	    	);
	    
	    ns.readRequest(Constants.getRequestFilePath());
	    ns.readExpectedResponse(Constants.getExpectedResponseFilePath());
	    ns.stress();
	    
	    assertEquals(20, ns.getNumberOfThreads());
	}
}
