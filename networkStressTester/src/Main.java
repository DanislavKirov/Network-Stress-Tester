package networkStressTester.src;

import networkStressTester.Constants;

public class Main {

	public static void main(String[] args) {		
		NetworkStresser ns = new NetworkStresser(Constants.getHost(), Constants.getPort());
		ns.readRequest(Constants.getRequestFilePath());
		ns.readExpectedResponse(Constants.getExpectedResponseFilePath());
		ns.stress();
	}

}
