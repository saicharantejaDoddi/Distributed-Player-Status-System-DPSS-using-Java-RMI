package DataStructure;

public class Account {
	public String firstName;
	public String lastName;
	public int age;
	public String password;
	public String ipAddress;
	public String userName;
	public String status;

	public Account(String firstName, String lastName, int age, String userName, String password, String ipAddress) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.userName = userName;
		this.password = password;
		this.ipAddress = ipAddress;
		this.status = "OFFLINE";
	}

}

