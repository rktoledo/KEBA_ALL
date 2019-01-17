package keba.rmiinterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

import kebaObjects.ChargeObject;
import kebaObjects.Report100;
import kebaObjects.Report1;
import kebaObjects.Report2;
import kebaObjects.Report3;
import kebaObjects.KEBAInfo;
import kebaObjects.KEBAStateInfo;


public interface KEBAServerInterface extends Remote{

	public static final String SERVICE_NAME = "KEBAServerInterface";
	
	void startServer() throws RemoteException;
	void stopServer() throws RemoteException;
	boolean getServerState() throws RemoteException;
	Report100 report100() throws RemoteException;
	Report100 report(String rep) throws RemoteException;
	Report1 report1() throws RemoteException;
	Report2 report2() throws RemoteException;
	Report3 report3() throws RemoteException;
	KEBAInfo kebaInfo() throws RemoteException;
	KEBAStateInfo kebaStateInfo() throws RemoteException;
	
	boolean isChargeOngoing() throws RemoteException;
	ChargeObject getChargeObject() throws RemoteException;
	
	public boolean addRemoteObserver(RemoteObserver obs) throws RemoteException;
	void deleteRemoteObserver(RemoteObserver obs) throws RemoteException;
}
