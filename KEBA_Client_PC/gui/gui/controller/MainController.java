package gui.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import gui.model.ServerConnectionState;
import gui.model.ServerStateObservable;
import gui.view.ChartView;
import gui.view.InfoView;
import gui.view.ChargeTableView;
import gui.view.MainView;
import gui.view.ServerConnectionView;
import gui.view.ServerStateView;
import gui.view.SettingsView;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import keba.rmiinterface.KEBADataInterface2;
import keba.rmiinterface.KEBAServerInterface;
import keba.rmiinterface.RemoteObserver;
import kebaObjects.ChargeInfo;
import kebaObjects.ChargeObject;
import kebaObjects.Report100;

import remote.db.RemoteDatabase;


public class MainController extends UnicastRemoteObject implements  RemoteObserver, Observer{
	private static final long serialVersionUID = 8026520513242744791L;

	private JFrame mainFrame;
	
	private ServerStateController serverStateController;
	private ServerStateView serverStateView;
	
	private ServerConnectionController serverConnectionController;
	private ServerConnectionView serverConnectionView;
	
	private ChartMainController chartMainController;
	private ChartView chartCard;
	
	private ChargeTableController chargingTableController;
	private ChargeTableView chargingCard;
	
	private InfoController infoController;
	private InfoView infoCard;
	
	private SettingController settingController;
	private SettingsView settingsCard;
	
	private ChartController chartController;
	private ChartView selectedChartCard;
	
	private KEBAServerInterface udpServerStub;
	private KEBADataInterface2 serverData;
	
	private ChargeObject chargeObj;
	
	//private ServerSettings serverSettings;

	//private ChargeInfo chargingData;
	private Boolean isConnected;

	public MainController() throws RemoteException{
		super();
		//this.serverSettings= new ServerSettings();
		//setRemoteObjects();

		try {
			this.serverConnectionController= new ServerConnectionController();
			this.isConnected= this.serverConnectionController.isConnected();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serverConnectionController.addObserver(this);
		
		updateAll();
		
		this.serverStateController= new ServerStateController(this.udpServerStub, this.isConnected);
		
		this.serverConnectionView= serverConnectionController.getView();
		this.serverStateView= serverStateController.getView();
		
		this.mainFrame= new MainView(chartCard, chargingCard, infoCard, settingsCard, 
				serverConnectionView, serverStateView);

		addWindowListener();
		showView();
	}
	
	private void updateAll(){
		//String actualChargeFilePath= null;
		//ChargingObject charging= null;
		
		if (this.isConnected){
			System.out.println("MaCont: isconnected true");
			this.udpServerStub= serverConnectionController.getServerStub();
			System.err.println("MaCont: 1");
			this.serverData= serverConnectionController.getDatabaseStub();
			System.err.println("MaCont: 2");
			
			try {
				System.err.println("MaCont: 3");
				this.udpServerStub.addRemoteObserver(this);
				System.err.println("MaCont: 4");
				this.chargeObj= this.serverData.getLatestChargeObject();
				System.err.println("MaCont: 5");
				//this.chargingData= this.db.getActualCharging();
				//actualChargeFilePath = chargingData.getChargeFilePath();
				//charging= fileHandler.getChargingData(actualChargeFilePath);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.err.println("MaCont: 6");
			this.chargingTableController= new ChargeTableController(this.serverData);
			this.infoController= new InfoController();
			this.settingController= new SettingController();
			this.chartMainController= new ChartMainController(this.chargeObj, true);
			
			System.err.println("MaCont: 7");
			this.chartCard= chartMainController.getChartView();
			this.chargingCard= chargingTableController.getChargeView();
			this.infoCard= infoController.getInfoView();
			this.settingsCard= settingController.getSettingView();
			
			System.err.println("MaCont: 8");
			((MainView)this.mainFrame).setActualCard(chartCard);
			((MainView)this.mainFrame).setChargeCard(chargingCard);
			((MainView)this.mainFrame).setInfoCard(infoCard);
			((MainView)this.mainFrame).setSettingsCard(settingsCard);
			
			System.err.println("MaCont: 9");
			addChargings();
		}
		else{
			removeObserver();
			
			this.udpServerStub= null;
			this.serverData= null;
			
			this.chargingTableController= null;
			this.infoController= null;
			this.settingController= null;
			this.chartMainController= null;
			
			System.out.println("MaCont: isconnected false");
			this.chartCard= null;
			this.chargingCard= null;
			this.infoCard= null;
			this.settingsCard= null;
		}
	}
	
	private void changeView(Boolean isConnected){
		((MainView)this.mainFrame).changeLayoutConnection(isConnected);
	}
	
	public void addWindowListener(){
		WindowAdapter winAdapt= new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(mainFrame, 
		            "Are you sure to close this window?", "Really Closing?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	System.out.println("MaCont: System Exit");
		        	removeObserver();
		            System.exit(0);
		        }
		        else {
		        	// TODO do nothing
		        }
			}
		};
		((MainView) mainFrame).setWinListener(winAdapt);
	}
	
	public void removeObserver(){
		if (this.udpServerStub!= null){
			try {
				this.udpServerStub.deleteRemoteObserver(this);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void showView(){
        this.mainFrame.setVisible(true);
    }
	
	public void addChargings(){
		try {
			ArrayList<ChargeInfo> chargings= RemoteDatabase.getInstance().getDB().getAllChargings();
			this.chargingCard.add(chargings);
			System.out.println("MaCont: Amount of chargings= " + chargings.size());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	// Remote Observer
	@Override
	public void update(Object observable, Object updateMsg)
			throws RemoteException {
		System.out.println("MaCont: Received Rem: updateMessage " + updateMsg);
		
		if (updateMsg instanceof ChargeObject){
			ChargeObject chargeObj= (ChargeObject)updateMsg;
			//Report100 chargeData= charge.getChargingData();
			//System.out.println("MC Rem obs received ChargeObject for sessionid: " + chargeData.getSessionID());
			//System.out.println("MC Rem obs received ChargeObject Epres: " + chargeData.getEpres());
			/*System.out.println("MaCont: GUIREADY= " + guiReady);
			System.out.println("MaCont: chartMainController= " + chartMainController);
			System.out.println("MaCont: charge= " + charge);*/
			if (chartMainController!=null) chartMainController.updateGUI(chargeObj);
		}
		else if (updateMsg instanceof Report100){
			System.out.println("MC Rem obs received Report100 ");
			//chartMainController.updateGUI();
		}
		else if (updateMsg instanceof ServerStateObservable){
			System.out.println("MC Rem obs received Report100 ");
			serverConnectionController.disconnect();
			//chartMainController.updateGUI();
		}
		else {
			//System.out.println("MC Rem obs received Boolean? = " + updateMsg.toString());
			serverStateController.isRunning((Boolean)updateMsg);
		}
	}

	
	// local observer "Connection State changes"
	@Override
	public void update(Observable obs, Object updateMsg) {
		System.out.println("MaCont: updateMsg received= " + updateMsg.getClass());
		if(updateMsg instanceof ServerConnectionState){
			System.out.println("MaCont: update ServerCOnnectionState " + ((ServerConnectionState)updateMsg).getIsConnected());
			System.out.println("MaCont: Debug update 1");
			this.isConnected= ((ServerConnectionState)updateMsg).getIsConnected();
			System.out.println("MaCont: Debug update 2");
			updateAll();
			System.out.println("MaCont: Debug update 3");
			changeView(isConnected);
			System.out.println("MaCont: Debug update 4");
			
			this.serverConnectionController.updateView();
			this.serverStateController.setUdpServerStub(this.udpServerStub);
			this.serverStateController.enableButton(isConnected);
		}
	}	
	
	
	/*private void setRemoteObjects(){
		String host = serverSettings.getHost();
        
		try {        	
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            
            String[] boundNames = registry.list();
            for (String name : boundNames)
            {
            	System.out.println("MaCont: REGISTRY : " + name);
            }
           
            udpServerStub = (KEBAServerInterface) registry.lookup(KEBAServerInterface.SERVICE_NAME);            
            serverData = (KEBADataInterface2) registry.lookup(KEBADataInterface2.SERVICE_NAME);
            fileHandler = (KEBAFileInterface) registry.lookup(KEBAFileInterface.SERVICE_NAME);            
        } catch (Exception e) {
            System.out.println("MaCont: Client exception: " + e.getMessage());
            System.out.println("MaCont: Server not started");
            //TODO Pop up with message "Server not started or reachable" + grey out button
            e.printStackTrace();
        }
	}*/


	/*// Observer
	@Override
	public void update(Observable observable, Object updateMsg) {
		System.out.println("MC Received 2: updateMessage " + updateMsg);
		System.out.println("MC Received 2: updateMessage " + updateMsg.getClass());
		System.out.println("MC Received 2: observable " + observable);
		System.out.println("MC Received 2: observable " + observable.getClass());
	}*/
}
