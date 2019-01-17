package remote.db;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import keba.rmiinterface.KEBADataInterface2;

public class RemoteDatabase {
	
	private static RemoteDatabase instance = null;
	private KEBADataInterface2 db;
	
	/**
	 * Singleton-Konstruktor des eine einmalige Instanz zurueckliefert
	 * @return
	 */
	public static RemoteDatabase getInstance() {
		if (instance == null) {
			instance = new RemoteDatabase();
		}
		return instance;
	}
	
	private RemoteDatabase(){
		this.db= setRemoteObjects();
	}
	
	public KEBADataInterface2 getDB(){
		return this.db;
	}
	
	private KEBADataInterface2 setRemoteObjects(){
		String host = "RSK";
        
    	KEBADataInterface2 dbStub= null;
        
		try {        	
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            dbStub = (KEBADataInterface2) registry.lookup(KEBADataInterface2.SERVICE_NAME);
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            System.out.println("Server not started");
            e.printStackTrace();
        }
		return dbStub;
	}
}
