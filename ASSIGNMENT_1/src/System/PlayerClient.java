package System;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import Servers.IPlayerClient;
import Helper.logManager;

public class PlayerClient extends UnicastRemoteObject implements Runnable, IPlayerClient {
	logManager playerClientLogManager = null;
	static final String PLAYERCLIENT = "playerClient_LogReport";

	public PlayerClient() throws RemoteException {
		super();
		playerClientLogManager = new logManager();
	}

	private boolean validateUserName(String userName) {
		if (userName.length() >= 6 && userName.length() <= 15) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validatePassword(String password) {
		if (password.length() >= 6) {

			return true;
		} else {
			return false;
		}
	}

	public boolean validateString(String name) {

		if ((name.length() >= 1) && name.matches("^[a-zA-Z\\s]+")) {

			return true;
		} else {
			return false;

		}

	}

	private boolean validateIpAddress(String ipAddress) {

		try {
			String[] addressPart = ipAddress.split("\\.");

			if (addressPart.length == 4) {
				int firstPart = Integer.parseInt(addressPart[0]);
				int secondPart = Integer.parseInt(addressPart[1]);
				int thridPart = Integer.parseInt(addressPart[2]);
				int fourthPart = Integer.parseInt(addressPart[3]);

				if (!(firstPart == 132 || firstPart == 93 || firstPart == 182)) {
					return false;
				}
				if (!(secondPart >= 0 && secondPart <= 255)) {
					return false;
				}
				if (!(thridPart >= 0 && thridPart <= 255)) {
					return false;
				}
				if (!(fourthPart >= 0 && fourthPart <= 255)) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;

		}

	}

	private int validateAge(String ageString) {
		int age = 0;

		try {
			age = Integer.parseInt(ageString);
		} catch (Exception ex) {

			return 0;
		}

		if (age >= 1) {

			return age;
		} else {
			return 0;
		}
	}

	private IPlayerClient getServerObject(String currentIpAddress) {
		boolean status = false;
		IPlayerClient gameServer = null;
		String ipAddress = "";
		while (!(status)) {
			ipAddress = currentIpAddress;
			status = validateIpAddress(ipAddress);

			if (status == false) {
				System.out.println("Please enter valid Ip Address like below");
				System.out.println(
						"132.xxx.xxx.xxx : IP-addresses starting with 132 indicate a North-American geolocation.");
				System.out.println("93.xxx.xxx.xxx : IP-addresses starting with 93 indicate an European geo-location.");
				System.out.println("182.xxx.xxx.xxx : IP-addresses starting with 182 indicate an Asian geo-location.");

			}
		}
		// Got IP Address from the console
		String currentIPAddress = ipAddress.toString();
		String[] ipValue = currentIPAddress.split("\\.");
		String firstPartAddress = ipValue[0].substring(0, ipValue[0].length());
		int firstPart_IPAddress = Integer.parseInt(firstPartAddress);

		String gameServerName = "";
		int port = 0;
		if (firstPart_IPAddress == 132) {
			gameServerName = "NorthAmericanServer";
			port = 3001;
		} else if (firstPart_IPAddress == 93) {
			gameServerName = "EuropeanServer";
			port = 3002;
		} else if (firstPart_IPAddress == 182) {
			gameServerName = "AsianServer";
			port = 3003;
		}

		try {
			gameServer = (IPlayerClient) java.rmi.Naming.lookup("rmi://localhost:" + port + "/" + gameServerName);
			status = true;
		} catch (Exception ex) {
			gameServer = null;
			System.out.println(gameServerName + "Server is busy");
		}

		return gameServer;

	}

	public static void main(String[] args) throws Exception {

		PlayerClient playerClient = new PlayerClient();

		(new Thread(playerClient)).start();
	}

	private void playerSignOut(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the User Name:");
			userName = sc.nextLine();
			userNameStatus = validateUserName(userName.trim());
			if (userNameStatus == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			ipAddressStatus = validateIpAddress(currentIpAddress.trim());
			if (ipAddressStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean status = checkPlayerSignOutStatus(userNameStatus, ipAddressStatus);
		String playerSignOutString = "";

		if (status) {

			playerSignOutString = playerSignOut(userName, currentIpAddress);
		} else {
			System.out.println("Player Sign Out Failed due to Player Sign Out Details are not valid");

		}

		if (!playerSignOutString.equals("true")) {
			System.out.println("Player Sign In Failed due to Server");
		}

	}

	private void playerSigIn(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the User Name:");
			userName = sc.nextLine();
			userNameStatus = validateUserName(userName.trim());
			if (userNameStatus == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the Password:");
			password = sc.nextLine();
			passwordStatus = validatePassword(password.trim());
			if (passwordStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			ipAddressStatus = validateIpAddress(currentIpAddress.trim());
			if (ipAddressStatus == false) {
				System.out.println("Entered Ip Address is not valid");
			}
		}

		boolean status = checkPlayerSignInStatus(userNameStatus, passwordStatus, ipAddressStatus);
		String playerSigInString = "";
		if (status) {

			playerSigInString = playerSignIn(userName, password, currentIpAddress);

		} else {
			System.out.println("Player Sign In Failed due to Player Sign In Details are not valid");

		}

		if (!playerSigInString.equals("true")) {
			System.out.println("Player Sign In Failed due to Server");
		}

	}

	private void createPlayerOperation(Scanner sc) {
		sc.nextLine();
		System.out.println("-------Player Account---------");

		boolean firstNameStatus = false;
		String firstName = "";
		while (!(firstNameStatus)) {
			System.out.println("Please Enter the First Name:");
			firstName = sc.nextLine();
			firstNameStatus = validateString(firstName.trim());
			if (firstNameStatus == false) {
				System.out.println("Entered First name is not valid");
			}
		}

		boolean lastNameStatus = false;
		String lastName = "";
		while (!(lastNameStatus)) {
			System.out.println("Please Enter the Last Name:");
			lastName = sc.nextLine();
			lastNameStatus = validateString(lastName.trim());
			if (lastNameStatus == false) {
				System.out.println("Entered Last name is not valid");
			}
		}

		int age = 0;
		String ageString = "";
		while (age == 0) {
			System.out.println("Please Enter the Age:");
			ageString = sc.nextLine();
			age = validateAge(ageString.trim());
			if (age == 0) {
				System.out.println("Entered Age is not valid");
			}
		}

		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the User Name:");
			userName = sc.nextLine();
			userNameStatus = validateUserName(userName.trim());
			if (userNameStatus == false) {
				System.out.println("Entered User name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the Password:");
			password = sc.nextLine();
			passwordStatus = validatePassword(password.trim());
			if (passwordStatus == false) {
				System.out.println("Entered Password is not valid");
			}
		}

		boolean ipAddressStatus = false;
		String currentIpAddress = "";
		while (!(ipAddressStatus)) {
			System.out.println("Please Enter the IP Address:");
			currentIpAddress = sc.nextLine();
			ipAddressStatus = validateIpAddress(currentIpAddress.trim());
			if (ipAddressStatus == false) {
				System.out.println("Entered Ip Address is not valid");
			}
		}

		boolean status = checkPlayerDetailsStatus(firstNameStatus, lastNameStatus, age, userNameStatus, passwordStatus,
				ipAddressStatus);

		boolean createPlayerStatus = false;
		if (status) {

			createPlayerStatus = createPlayerAccount(firstName, lastName, age, userName, password, currentIpAddress);

		} else {
			System.out.println("Create Player Account Failed due to Players Details are not valid");
		}
		if (!createPlayerStatus) {
			System.out.println("Create Player Account Failed due to Server");
		}

	}

	public boolean createPlayerAccount(String firstName, String lastName, int age, String userName, String password,
			String currentIpAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase().toUpperCase(), "---------------------------------------------\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Operation Performed : Create Player Account" + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Time Performed :" + timeNow + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "IPAddress Performed :" + currentIpAddress + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "userName Performed :" + userName + "\r\n");
		// Get the server object based on IP Address
		IPlayerClient gameServer = getServerObject(currentIpAddress);

		boolean createPlayerStatus = false;

		try {
			createPlayerStatus = gameServer.createPlayerAccount(firstName, lastName, age, userName, password,
					currentIpAddress);
		}

		catch (Exception ex) {
			System.out.println("Player Creation Player Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Creation of Player un-successfully" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Creation of Player Failed due to issue in Server");

		}
		if (createPlayerStatus) {
			System.out.println("Create Player Account Done !");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Account is successfully created !" + "\r\n");

		} else {
			System.out.println("Same User Name is already present");
			System.out.println("Create Player Account Failed in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Account is not successfully created !" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Due to Duplicate username already exists in the Server." + "\r\n");
		}
		return createPlayerStatus;

	}

	public String playerSignIn(String userName, String password, String currentIpAddress) {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "---------------------------------------------\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Operation Performed :  Player Sign In" + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Time Performed :" + timeNow + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "IPAddress Performed :" + currentIpAddress + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "userName Performed :" + userName + "\r\n");
		IPlayerClient gameServer = getServerObject(currentIpAddress);
		String playerSigInString = "";
		try {
			playerSigInString = gameServer.playerSignIn(userName, password, currentIpAddress);
		} catch (Exception ex) {
			System.out.println("Player Sign In Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed In" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Player Sign In Failed due to issue in Server");

		}

		if (playerSigInString.equals("true")) {
			System.out.println("Player successfully Signed In");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player successfully Signed In" + "\r\n");

		} else {
			System.out.println("Player un-successfully Signed In");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed In" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "ERROR Message: " + playerSigInString);

		}
		return playerSigInString;

	}

	public String playerSignOut(String userName, String currentIpAddress) {

		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);

		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "---------------------------------------------\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Operation Performed : Player Sign Out" + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "Time Performed :" + timeNow + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "IPAddress Performed :" + currentIpAddress + "\r\n");
		playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "userName Performed :" + userName + "\r\n");
		// Get the server object based on IP Address
		String playerSignOutString = "";
		try {
			IPlayerClient gameServer = getServerObject(currentIpAddress);

			playerSignOutString = gameServer.playerSignOut(userName, currentIpAddress);
		}

		catch (Exception ex) {
			System.out.println("Player Sign Out Failed due to issue in Server");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed Out" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"ERROR Message: " + "Player Sign Out Failed due to issue in Server");

		}
		if (playerSignOutString.equals("true")) {
			System.out.println("Player successfully Signed Out");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player successfully Signed Out" + "\r\n");

		} else {
			System.out.println("Player un-successfully Signed Out");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(),
					"OUTPUT Message: " + "Player un-successfully Signed Out" + "\r\n");
			playerClientLogManager.insertMessageWithFile(userName.toUpperCase(), "ERROR Message: " + playerSignOutString);

		}

		return playerSignOutString;
	}

	private int displayOptions(Scanner sc) {
		// Display Options
		System.out.println("Please Select the one of the below options");
		System.out.println("1.Create Player Account");
		System.out.println("2.Player SignIn");
		System.out.println("3.Player SignOut");
		System.out.println("4.Close the Player Client Application");

		// Taking Option
		int option = 0;
		try {
			option = sc.nextInt();
		} catch (Exception ex) {
			System.out.println("Please enter only integer value");
			sc.nextLine();
		}
		return option;
	}

	private boolean checkPlayerDetailsStatus(boolean firstNameStatus, boolean lastNameStatus, int age,
			boolean userNameStatus, boolean passwordStatus, boolean ipAddressStatus) {

		if (firstNameStatus && lastNameStatus && userNameStatus && passwordStatus && ipAddressStatus && age > 0) {
			return true;
		} else {
			return false;
		}

	}

	private boolean checkPlayerSignInStatus(boolean userNameStatus, boolean passwordStatus, boolean ipAddressStatus) {

		if (userNameStatus && passwordStatus && ipAddressStatus) {
			return true;
		} else {
			return false;
		}

	}

	private boolean checkPlayerSignOutStatus(boolean userNameStatus, boolean ipAddressStatus) {

		if (userNameStatus && ipAddressStatus) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void run() {

		Scanner sc = new Scanner(System.in);

		// 1.Getting from the client way

		boolean optionStatus = true;
		while (optionStatus) {
			int option = displayOptions(sc);

			switch (option) {

			case 1:
				createPlayerOperation(sc);
				break;
			case 2:
				playerSigIn(sc);
				break;
			case 3:
				playerSignOut(sc);
				break;
			case 4:
				System.exit(0);
				break;

			default:
				System.out.println("Please enter either 1 or 2 or 3 or 4");

			}
		}

	}

}
