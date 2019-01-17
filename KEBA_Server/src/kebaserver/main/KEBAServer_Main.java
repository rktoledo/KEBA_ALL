package kebaserver.main;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import db.DbDAOFactory;
import dbDAOInterface.KebaDbDAO;
import dbMySQL.MySqlKebaDAO;
import fileHandling.FileHandler;

import keba.rmiinterface.KEBAServerInterface;
import keba.rmiinterface.KEBADataInterface2;

import networkSettings.ServerNetworkInformation;

public class KEBAServer_Main{
	
	public KEBAServer_Main(){
		super();
	}
         
    public static void main(String[] args) throws Exception {
    	ServerNetworkInformation serverNetworkInfo= new ServerNetworkInformation();
		int port= 1099;
        
        try {
        	KebaDbDAO db= DbDAOFactory.getDbDAOFactory("mysql").getKEBADAO();
        	
        	KEBAServer kebaServer= new KEBAServer(db);
        	
        	FileHandler fileHandler = FileHandler.getInstance();
        	
        	KEBAData kebaData= new KEBAData(db, fileHandler, kebaServer);
            
            String ip= serverNetworkInfo.getServerIP();
            System.out.println("Server on IP: " + ip);
            
            System.setProperty("java.rmi.server.hostname","RSK");
            
            KEBAServerInterface kebaInterfaceStub= (KEBAServerInterface) UnicastRemoteObject.exportObject(kebaServer, 0);
            KEBADataInterface2 kebaDataInterfaceStub= (KEBADataInterface2) UnicastRemoteObject.exportObject(kebaData, 0);
            
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(port);
            if (registry==null)
            {
             System.out.println("getRegistry");
             registry = java.rmi.registry.LocateRegistry.getRegistry(port);
            }
           
            registry.rebind(KEBAServerInterface.SERVICE_NAME, kebaInterfaceStub);
            registry.rebind(KEBADataInterface2.SERVICE_NAME, kebaDataInterfaceStub);
            
            String[] boundNames = registry.list();
            System.out.println("REGISTRY: " + registry.toString());
            for (String name : boundNames)
            {
            System.out.println("REGISTRY : " + name);
            }
            
            System.out.println("CODEBASE: " + System.getProperty("java.rmi.server.codebase"));
            // registry.bind("KEBA_UDPServer", udpServerStub);

            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
}
