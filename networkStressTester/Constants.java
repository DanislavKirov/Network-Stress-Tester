package networkStressTester;

public class Constants {

	private static final String host = "java.voidland.org";
	private static final int port = 80;
	private static final String requestFilePath = "/home/danislav/java/NetworkStressTester/request.txt";
	private static final String expectedResponseFilePath = "/home/danislav/java/NetworkStressTester/response.txt";
	
	public static String getHost() {
		return host;
	}

	public static int getPort() {
		return port;
	}

	public static String getRequestFilePath() {
		return requestFilePath;
	}

	public static String getExpectedResponseFilePath() {
		return expectedResponseFilePath;
	}
	
}
