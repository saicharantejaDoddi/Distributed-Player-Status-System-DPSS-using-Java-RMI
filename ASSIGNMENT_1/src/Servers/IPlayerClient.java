package Servers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPlayerClient extends Remote {
	public boolean createPlayerAccount(String firstName, String lastName, int age, String userName, String password,
			String ipAddress) throws RemoteException;

	public String playerSignIn(String userName, String password, String ipAddress) throws RemoteException;

	public String playerSignOut(String userName, String ipAddress) throws RemoteException;

}
