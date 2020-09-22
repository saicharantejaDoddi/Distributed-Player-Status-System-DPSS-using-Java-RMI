package System;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import Servers.IAdministratorClient;
import Helper.logManager;

public class AdministratorClient implements IAdministratorClient, Runnable {

	static final String ADMIN = "Admin_LogReport";
	logManager adminLogManager = new logManager(ADMIN);

	public AdministratorClient() {

	}

	public static void main(String[] args) {
		AdministratorClient admin = new AdministratorClient();
		(new Thread(admin)).start();
	}

	public void run() {

		Scanner sc = new Scanner(System.in);

		boolean optionStatus = true;
		while (optionStatus) {
			int option = displayOptions(sc);

			switch (option) {

			case 1:
				getAdminStatus(sc);
				break;

			case 2:
				System.exit(0);
				break;
			default:
				System.out.println("Please enter either 1 or 2");

			}
		}
	}

	private int displayOptions(Scanner sc) {
		// Display Options
		System.out.println("Please Select the one of the below options");
		System.out.println("1.Get Admin Status ");
		System.out.println("2.Close the Admin Client Application");

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

	private IAdministratorClient getServerObject(String currentIpAddress) {
		boolean status = false;
		IAdministratorClient gameServer = null;
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
			gameServer = (IAdministratorClient) java.rmi.Naming
					.lookup("rmi://localhost:" + port + "/" + gameServerName);
			status = true;
		} catch (Exception ex) {
			gameServer = null;
			System.out.println(gameServerName + "Server is busy");
		}

		return gameServer;

	}

	private void getAdminStatus(Scanner sc) {
		sc.nextLine();
		boolean userNameStatus = false;
		String userName = "";
		while (!(userNameStatus)) {
			System.out.println("Please Enter the  Name:");
			userName = sc.nextLine();
			userNameStatus = validateAdminName(userName.trim());
			if (userNameStatus == false) {
				System.out.println("Entered name is not valid");
			}
		}

		boolean passwordStatus = false;
		String password = "";
		while (!(passwordStatus)) {
			System.out.println("Please Enter the  Password:");
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
				System.out.println("Please enter valid Ip Address like below");
				System.out.println(
						"132.xxx.xxx.xxx : IP-addresses starting with 132 indicate a North-American geolocation.");
				System.out.println("93.xxx.xxx.xxx : IP-addresses starting with 93 indicate an European geo-location.");
				System.out.println("182.xxx.xxx.xxx : IP-addresses starting with 182 indicate an Asian geo-location.");

			}
		}

		boolean status = checkAdminSignInStatus(userNameStatus, passwordStatus, ipAddressStatus);
		String AdminSigInString = "";
		if (status) {

			AdminSigInString = getPlayerStatus(userName, password, currentIpAddress);

		} else {

			System.out.println("Admin Sign In Failed due to Admin Sign In Details are not valid");
		}
		System.out.println(AdminSigInString);
	}

	// Main Method for the Admin

	public String getPlayerStatus(String userName, String password, String ipAddress) {
		String currentIpAddress = ipAddress.trim();
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String timeNow = formatedDate.format(localDateTime);
		adminLogManager.insertMessage("---------------------------------------------\r\n");
		adminLogManager.insertMessage("Operation Performed :  getPlayerStatus" + "\r\n");
		adminLogManager.insertMessage("Time Performed :" + timeNow + "\r\n");
		adminLogManager.insertMessage("IPAddress Performed :" + currentIpAddress + "\r\n");
		adminLogManager.insertMessage("UserName Performed :" + userName + "\r\n");

		String AdminSigInString = "";
		try {
			currentIpAddress = ipAddress.trim();
			IAdministratorClient gameServer = getServerObject(currentIpAddress);
			AdminSigInString = gameServer.getPlayerStatus(userName, password, currentIpAddress);
			adminLogManager.insertMessage("OUTPUT Message: " + AdminSigInString + "\r\n");
			System.out.println("Admin Sign In successfully in Server");

		} catch (Exception ex) {
			adminLogManager.insertMessage("OUTPUT Message: " + "Admin Sign In Failed due to issue in Server" + "\r\n");
			adminLogManager.insertMessage("ERROR Message: " + AdminSigInString + "\r\n");
			System.out.println("Admin Sign In Failed due to issue in Server");

		}

		return AdminSigInString;
	}

	// Main Method for the Admin Done

	// Validations of Admin,Password,Ip Address
	private boolean validateAdminName(String userName) {
		if (userName.equalsIgnoreCase("Admin")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean validatePassword(String password) {
		if (password.equals("Admin")) {

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

	private boolean checkAdminSignInStatus(boolean userNameStatus, boolean passwordStatus, boolean ipAddressStatus) {

		if (userNameStatus && passwordStatus && ipAddressStatus) {
			return true;
		} else {
			return false;
		}

	}

	// Validation Done

}
