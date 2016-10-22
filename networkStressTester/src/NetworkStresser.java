package networkStressTester.src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class NetworkStresser {
	
	private String host;
	private int port;

	private List<String> request = null;
	private String expectedResponse = null;
	private int numberOfThreads = 0;
	private List<RequestThread> threads = new ArrayList<>();
	private long maxTime = 0;
	
	public NetworkStresser(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void readRequest(String requestFilePath) {
		request = new ArrayList<>();
		try (BufferedReader requestReader = new BufferedReader(new FileReader(requestFilePath))) {
			String line;
			while ((line = requestReader.readLine()) != null) {
				request.add(line);
			}
		} catch (IOException e) {
			request = null;
		}
	}
	
	public void readExpectedResponse(String expectedResponseFilePath) {
		try (BufferedReader responseReader = new BufferedReader(new FileReader(expectedResponseFilePath))) {
			expectedResponse = responseReader.readLine();
		} catch (IOException e) {
			expectedResponse = null;
		}
	}
	
	public void stress() {
		if (request == null) {
			System.err.println("Missing request!");
			return;
		}
		if (expectedResponse == null) {
			System.err.println("Missing expected response!");
			return;
		}

		boolean allSuccessful = true;
		while (allSuccessful) {
			System.out.println("Successful: " + Integer.toString(numberOfThreads));
			numberOfThreads += 20;
			allSuccessful = sendRequests();
		}
		
		while (!allSuccessful) {
			System.out.println("NOT Successful: " + Integer.toString(numberOfThreads));
			numberOfThreads -= 1;
			if (numberOfThreads == 0) {
				System.err.println("Something's wrong! Try again later.");
				return;
			}
			allSuccessful = sendRequests();
		}
		
		maxTime = findMaxTime(threads);
		System.out.println("Successful: " + Integer.toString(numberOfThreads));
		System.out.println("Max time: " + Long.toString(maxTime));
	}

	private boolean sendRequests() {
		threads.clear();
		CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
		for (int i = 0; i < numberOfThreads; i++) {
			threads.add(new RequestThread(host, port, request, barrier));
		}
		
		threads.forEach(Thread::start);
		
		try {
			for (RequestThread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			System.err.println("An error has occured. " + e.getMessage());
			return false;
		}
		
		if (threads.get(0).isFailed()) {
			return false;
		}
			
		for (RequestThread thread : threads) {
			if (thread.getResponse() == null || !expectedResponse.equals(thread.getResponse())) {
				return false;
			}
		}
		return true;
	}
	
	private long findMaxTime(List<RequestThread> threads) {
		long max = 0;
		for (RequestThread thread : threads) {
			if (thread.getTime() > max) {
				max= thread.getTime();
			}
		}		
		return max;
	}
	
	public List<String> getRequest() {
		return request;
	}
	
	public String getExpectedResponse() {
		return expectedResponse;
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}
	
	public long getMaxTime() {
		return maxTime;
	}

}
 
