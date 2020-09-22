package Servers;

public class StartServers {

	public static void main(String[] args) throws Exception {

		AsianGameServer.startServer();
		NorthAmericanGameServer.startServer();
		EuropeanGameServer.startServer();

	}

}
