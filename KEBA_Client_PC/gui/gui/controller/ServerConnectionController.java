package gui.controller;

import gui.model.KEBAClientSettings;
import gui.model.ServerConnectionState;
import gui.view.ServerConnectionView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Observable;

import keba.rmiinterface.KEBADataInterface2;
import keba.rmiinterface.KEBAServerInterface;

public class ServerConnectionController extends Observable {

	private ServerConnectionView serverConnectionView;
	
	private KEBAServerInterface udpServerStub;
	private KEBADataInterface2 db;
	
	private KEBAClientSettings serverSettings;
	private ServerConnectionState isConnected;
	
	protected ServerConnectionController() throws RemoteException{
		super();
		this.serverConnectionView= new ServerConnectionView();
		this.serverSettings= KEBAClientSettings.getInstance();
		isConnected= new ServerConnectionState();
		isConnected.setIsConnected(false);
		
		addListener();
		showView();
		updateView();
	}

	public void showView(){
        this.serverConnectionView.setVisible(true);
    }
	
	public ServerConnectionView getView(){
		return this.serverConnectionView;
	}
	
	public Boolean isConnected(){
		return isConnected.getIsConnected();
	}
	
	
	private void connectToServer(){
		String host = serverSettings.HOSTNAME;
		System.out.println("SCCont: host to connect= " + host);
        
		try {        	
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            System.out.println("SCCont: registry= " + registry.toString());
            
            String[] boundNames = registry.list();
            for (String name : boundNames)
            {
            	System.out.println("SCCont: REGISTRY : " + name);
            }
           
            udpServerStub = (KEBAServerInterface) registry.lookup(KEBAServerInterface.SERVICE_NAME);            
            db = (KEBADataInterface2) registry.lookup(KEBADataInterface2.SERVICE_NAME);
            isConnected.setIsConnected(true);
        } catch (Exception e) {
            System.out.println("SCCont: Client exception: " + e.getMessage());
            System.out.println("SCCont: Server not started");
            isConnected.setIsConnected(false);
            //TODO Pop up with message "Server not started or reachable" + grey out button
            //e.printStackTrace();
        }
	}
	
	public KEBAServerInterface getServerStub(){
		return this.udpServerStub;
	}
	
	public KEBADataInterface2 getDatabaseStub(){
		return this.db;
	}
	
	
	private void addListener() {
		this.serverConnectionView.setServerConnectionListener(new ServerConnectionListener());
	}
	
	class ServerConnectionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("SCCont: Button connect pressed");
			if (isConnected.getIsConnected()){
				isConnected.setIsConnected(false);
				setChanged();
				notifyObservers(isConnected);
				System.out.println("SCCont: Disconnected from Server= " + isConnected);
			}
			else {
				connectToServer();
				setChanged();
				notifyObservers(isConnected);
				System.out.println("SCCont: Connected to Server= " + isConnected.getIsConnected());
			}
		}
	}
	
	public void disconnect(){
		isConnected.setIsConnected(false);
		setChanged();
		notifyObservers(isConnected);
	}

	public void updateView(){
		this.serverConnectionView.setConnectionState(isConnected.getIsConnected());
	}
}
