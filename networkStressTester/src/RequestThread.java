package networkStressTester.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;


public class RequestThread extends Thread {
	
	private String host;
	private int port;
	private List<String> request;
	private static CyclicBarrier barrier;
	
	private String response = null;
	private long time = 0;
	private AtomicBoolean failed = new AtomicBoolean(false);
	
	public RequestThread(String host, int port, List<String> request, CyclicBarrier barrier) {
		this.host = host;
		this.port = port;
		this.request = new ArrayList<>(request);
		RequestThread.barrier = barrier;
	}
	
	public void run() {
		try (Socket socket = new Socket(host, port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream());) {
			request.forEach(out::println);
			barrier.await();
			out.flush();
			
			long t = System.currentTimeMillis();
			response = in.readLine();
			time = System.currentTimeMillis() - t;
			
		} catch (IOException | InterruptedException | BrokenBarrierException e) {
			failed.set(true);
			time = 0;
			response = null;
			barrier.reset();
		}
	}

	public String getResponse() {
		return response;
	}

	public long getTime() {
		return time;
	}
	
	public boolean isFailed() {
		return failed.get();
	}
}
