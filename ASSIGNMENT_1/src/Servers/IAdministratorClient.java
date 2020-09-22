package Servers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAdministratorClient extends Remote {
	public String getPlayerStatus(String userName, String password, String ipAddress) throws RemoteException;
}
