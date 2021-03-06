package Servers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import DataStructure.Account;
import Helper.logManager;

public class EuropeanGameServer extends UnicastRemoteObject implements IPlayerClient, IAdministratorClient, Runnable {
	logManager europeanGameServerLogManager = null;
	static final String EUROPEAN = "EuropeanGameServer_LogReport";
	static Hashtable<String, List<Account>> ht = new Hashtable<String, List<Account>>();
	protected EuropeanGameServer() throws RemoteException {
		super();
		europeanGameServerLogManager = new logManager(EUROPEAN);
		europeanGameServerLogManager.insertMessage("European Server is started !!!!!!!!!!!!" + "\r\n");
		
		// Default Data Stored
		List<Account> accountsofN = new ArrayList<Account>();	
		String firstUsernamekey="N";
		Account account1= new Account("Isaac", "Newton", 84, "NEWTONI", "NEWTONGRAVITY", "93.1.2.3");
		accountsofN.add(account1);
		ht.put(firstUsernamekey, accountsofN);
		
		List<Account> accountsofE = new ArrayList<Account>();
		String secondUsernamekey="E";
		Account account2= new Account("Albert", "Einstein", 76, "EINSTEINA", "Relativity", "93.1.2.3");
		accountsofE.add(account2);
		ht.put(secondUsernamekey, accountsofE);
		
	}

	

	public static void startServer() throws Exception {
		EuropeanGameServer obj = new EuropeanGameServer();
		LocateRegistry.createRegistry(3002);
		Naming.rebind("rmi://localhost:3002" + "/EuropeanServer", obj);
		System.out.println("European Server is started !!!!!!!!!!!!");
		(new Thread(obj)).start();

	}

	public String Client(int serverPort, String serverName) {
		DatagramSocket aSocket = null;
		String resp = null;

		try {

			aSocket = new DatagramSocket();
			byte[] m = "HELLO".getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
			aSocket.send(request);

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			byte[] data = reply.getData();
			resp = new String(data);
		}

		catch (Exception ex) {
			System.out.println("Exception in the " + serverName);
			resp = null;
		} finally {
			if (aSocket != null) {
				aSocket.close();
			}
		}
		return resp;
	}

	public boolean createPlayerAccount(String firstName, String lastName, int age, String userName, String password,
			String ipAddress) {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		europeanGameServerLogManager.insertMessage("-------------------------------------------------------" + "\r\n");
		europeanGameServerLogManager.insertMessage("Operation Performed : Create Player Account" + "\r\n");
		europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
		europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
		europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");
		
		synchronized(this)
		{
			Account account = new Account(firstName, lastName, age, userName, password, ipAddress);
			userName=userName.toUpperCase();
			String key = userName.substring(0, 1);
			List<Account> accounts = null;

			for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

			{
				if (listOfAccounts.getKey().equals(key)) {

					accounts = listOfAccounts.getValue();

					for (Account curr : accounts) {
						if (curr.userName.equalsIgnoreCase(userName)) {
							System.out.println(
									"Account is not successfully created ! Due to duplicate username already exists");
							europeanGameServerLogManager
									.insertMessage("OUTPUT Message: " + "Account is not successfully created !" + "\r\n");
							europeanGameServerLogManager.insertMessage("ERROR Message: "
									+ "Due to Duplicate username already exists in the European Server." + "\r\n");
							return false;
						}
					}
					accounts.add(account);
				}
			}
			if (accounts == null) {
				accounts = new ArrayList<Account>();
				accounts.add(account);
			}
			ht.put(key, accounts);
			System.out.println("Account is successfully created !");
		}
		
		europeanGameServerLogManager.insertMessage("OUTPUT Message: " + "Account is successfully created !" + "\r\n");
		return true;
	}

	public String playerSignIn(String userName, String password, String ipAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		europeanGameServerLogManager.insertMessage("-------------------------------------------------------" + "\r\n");
		europeanGameServerLogManager.insertMessage("Operation Performed : Player Sign In" + "\r\n");
		europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
		europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
		europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");
		
		synchronized(this)
		{
			userName=userName.toUpperCase();
			String signInString = isAccountValidSignIn(userName, password, ipAddress);
			if (signInString.equals("true")) {
				System.out.println("Player successfully Signed In");
				europeanGameServerLogManager.insertMessage("OUTPUT Message: " + "Player successfully Signed In" + "\r\n");

				return signInString;
			} else {
				System.out.println("Player un-successfully Signed In");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Player un-successfully Signed In" + "\r\n");
				europeanGameServerLogManager.insertMessage("ERROR Message: " + signInString);
				return signInString;
			}

		}
		
	}

	public String getEuropeanPlayerStatus() {
		synchronized(this)
		{
			int europeOnline = 0;
			int europeOffline = 0;
			for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

			{

				for (Account account : listOfAccounts.getValue()) {
					String status = account.status;

					if (status.equals("ONLINE")) {
						europeOnline = europeOnline + 1;
					} else if (status.equals("OFFLINE")) {
						europeOffline = europeOffline + 1;
					}

				}

			}

			return "EU: " + europeOnline + " online, " + europeOffline + " offline";
		}


	}

	public String playerSignOut(String userName, String ipAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		europeanGameServerLogManager.insertMessage("-------------------------------------------------------" + "\r\n");
		europeanGameServerLogManager.insertMessage("Operation Performed : Player Sign Out" + "\r\n");
		europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
		europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
		europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");
		
		synchronized(this)
		{
			userName=userName.toUpperCase();
			String signOutString = isAccountValidSignOut(userName, ipAddress);
			if (signOutString.equals("true")) {
				System.out.println("Player successfully Signed Out");
				europeanGameServerLogManager.insertMessage("OUTPUT Message: " + "Player successfully Signed Out" + "\r\n");

				return signOutString;
			} else {
				System.out.println("Player un-successfully Signed Out");
				europeanGameServerLogManager
						.insertMessage("OUTPUT Message: " + "Player un-successfully Signed Out" + "\r\n");
				europeanGameServerLogManager.insertMessage("ERROR Message: " + signOutString);
				return signOutString;
			}
		}
		
	}

	public String isAccountValidSignIn(String userName, String password, String ipAddress) {

		String key = userName.substring(0, 1);
		List<Account> accounts;

		for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

		{
			if (listOfAccounts.getKey().equals(key)) {
				accounts = listOfAccounts.getValue();

				for (Account account : accounts) {
					if (account.userName.equalsIgnoreCase(userName)) {
						if (account.password.equals(password)) {

							if (account.status.equals("OFFLINE")) {
								System.out.println("UserName and Password is correct.");
								account.status = "ONLINE";
								return "true";
							} else if (account.status.equals("ONLINE")) {
								System.out.println("UserName and Password is correct.But Status is already ONLINE!");
								return "Status is already ONLINE!\r\n";

							}
						} else {
							System.out.println("Password is wrong!");
							return "Password is wrong!\r\n";

						}
					}
				}

			}
		}
		System.out.println("UserName is not found in the current Server!");
		return "UserName is not found in the current Server!\r\n";

	}

	public String isAccountValidSignOut(String userName, String ipAddress) {
		String key = userName.substring(0, 1);
		List<Account> accounts;

		for (Entry<String, List<Account>> listOfAccounts : ht.entrySet())

		{
			if (listOfAccounts.getKey().equals(key)) {
				accounts = listOfAccounts.getValue();

				for (Account account : accounts) {
					if (account.userName.equalsIgnoreCase(userName)) {
						if (account.status.equals("ONLINE")) {
							System.out.println("UserName and Password is correct.");
							account.status = "OFFLINE";
							return "true";

						} else if (account.status.equals("OFFLINE")) {
							System.out.println("UserName and Password is correct.Status is already OFFLINE!");
							return "Status is already OFFLINE!\r\n";

						} else {
							System.out.println("UserName is wrong!");
							return "UserName is wrong!\r\n";

						}
					}
				}

			}
		}
		System.out.println("UserName is not found in the current Server!");
		return "UserName is not found in the current Server!\r\n";

	}

	public String getPlayerStatus(String userName, String password, String ipAddress) throws RemoteException {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		europeanGameServerLogManager.insertMessage("-------------------------------------------------------" + "\r\n");
		europeanGameServerLogManager.insertMessage("Operation Performed : getPlayerStatus" + "\r\n");
		europeanGameServerLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
		europeanGameServerLogManager.insertMessage("IPAddress Performed :" + ipAddress + "\r\n");
		europeanGameServerLogManager.insertMessage("userName Performed :" + userName + "\r\n");

		synchronized(this)
		{
			String message3 = Client(9001, "NorthAmerican Game Server");
			String message2 = Client(9003, "Asian Game Server");
			String message1 = getEuropeanPlayerStatus();
			String totalMessage = "";
			if (message3 == null) {
				System.out.println("Exception in the NorthAmerican Game Server");
				totalMessage = "ERROR MESSAGE:Exception in the NorthAmerican Game Server" + "\r\n";
			}
			if (message2 == null) {
				System.out.println("Exception in the Asian Game Server");
				totalMessage = totalMessage + "ERROR MESSAGE:Exception in the Asian Game Server" + "\r\n";
			}
			if (message1 == null) {
				System.out.println("Exception in the European Game Server");
				totalMessage = totalMessage + "ERROR MESSAGE:Exception in the European Game Server" + "\r\n";
			}

			if (message1 != null && message2 != null && message3 != null) {
				totalMessage = message1.trim() + "." + message2.trim() + "." + message3.trim() + ".";
				totalMessage = "OUTPUT Message: " + totalMessage + "\r\n";
				europeanGameServerLogManager.insertMessage(totalMessage);
			} else {
				europeanGameServerLogManager.insertMessage(totalMessage);
			}

			return totalMessage;
		}
		

	}

	public void run() {
		System.out.println("UDP Port for European Game Server Started");
		DatagramSocket aSocket = null;

		try {

			aSocket = new DatagramSocket(9002);
			byte[] buffer = new byte[1000];

			while (true) {

				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String dataValue = getEuropeanPlayerStatus();

				byte[] data = dataValue.getBytes();
				DatagramPacket reply = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
				aSocket.send(reply);
			}

		} catch (Exception ex) {
			System.out.println("Exception");
		}

		finally {

			if (aSocket != null) {
				aSocket.close();
			}

		}

	}

}
