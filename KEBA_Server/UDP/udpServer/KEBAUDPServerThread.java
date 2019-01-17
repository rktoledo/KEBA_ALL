package udpServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.Observable;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kebaObjects.ChargeData;
import kebaObjects.ChargeInfo;
import kebaObjects.ChargeObject;
import kebaObjects.Report1;
import kebaObjects.Report100;
import kebaObjects.Report2;
import kebaObjects.Report3;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import dbDAOInterface.KebaDbDAO;

import fileHandling.FileHandler;

public class KEBAUDPServerThread extends Observable implements Runnable{
	
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Boolean schedulerIsRunning= false;
	
	private DatagramSocket udpSocket;
	private DatagramSocket udpSocket2;
    private static int port= 7092;
    private static int port2= 7090;
    private static String WallboxIP= "192.168.1.33";
    private int clientPollTime= 2000;	

    private LocalDateTime ldt;
    
    //private String chargingLogFile= null;
    private JSONObject chargingLog= new JSONObject();
    private JSONArray chargingLogTime= new JSONArray();
    private JSONArray chargingLogPower= new JSONArray();
    private JSONArray chargingLogVoltage= new JSONArray();
    private JSONArray chargingLogCurrent= new JSONArray();
    
    //private String chargingLogFile= null;
    private JSONObject chargingLogTemp= null;
    private JSONArray chargingLogTimeTemp= null;
    private JSONArray chargingLogPowerTemp= null;
    private JSONArray chargingLogVoltageTemp= null;
    private JSONArray chargingLogCurrentTemp= null;
    
    //private String stateLogFile= null;
    private JSONObject stateLog= new JSONObject();
    private JSONArray stateLogTime= new JSONArray();
    private JSONArray stateLogEvent= new JSONArray();
    
  //private String stateLogFile= null;
    private JSONObject stateLogTemp= null;
    private JSONArray stateLogTimeTemp= null;
    private JSONArray stateLogEventTemp= null;
    
    private String sessionStart= null;
    private boolean fileOpen= false;
    private boolean isRunning= false;
    private boolean chargeOngoing= false;
    private boolean taskRunning= false;
    
    private Report1 report1= new Report1();
    private Report2 report2= new Report2();
    private Report3 report3= new Report3();
    private Report100 report100= new Report100();

    private KebaDbDAO database;
    //private int currentChargeID;
    
    private ChargeInfo chargeInfo;
    private ChargeData chargeData;
    private ChargeObject chargeObject;
    
    private Thread UDPServerThread;
    private ReportServer reportServer;
    
    private Timer timer;
    
    public boolean isChargeOngoing(){
    	return this.chargeOngoing;
    }
    
    public ChargeObject getChargeObject(){
    	return this.chargeObject;
    }
    
    public KEBAUDPServerThread (KebaDbDAO database){	
    	this.chargeObject= new ChargeObject();
    	this.database= database;
    	this.reportServer= new ReportServer();
    }
 
    /*
     * Start the UDP Server
     */
    public void startUDPTask() {
    	if(!isRunning){
    		UDPServerThread = new Thread(this);
    		UDPServerThread.start();
    		reportServer.startUDPTask();
    	}
    }
    
    private void startPollObservers(){
    	System.out.println("Start polling");
    	
    	timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
            	if (isRunning){
            		updateChargeObjects();
            	}
            }
        }, 0, clientPollTime);
    }
    
    public void stopTimer(){
    	if (!isRunning){
    		timer.cancel();
    		timer.purge();
    	}
    }
    
    

    
    /*
     * STOP UDP Server
     */
    @SuppressWarnings("unchecked")
	public void stopUDPTask() { 
    	
    	if (!this.udpSocket.isClosed()){
    		this.udpSocket.close();
    	}
    	
    	reportServer.stopUDPTask();
    	
    	//this.udpSocket= null;
    	//this.udpSocket2= null;
    	
    	/*if (chargeOngoing){
    		terminateCharge();
    	}*/
    	isRunning= false;
      // Interrupt the thread so it unblocks any blocking call
    	if (UDPServerThread.isInterrupted()) {
    		ldt= LocalDateTime.now();
    		stateLogTime.add(ldt.toString());
    		stateLogEvent.add("Server stopped");
    		
    		System.out.println("Server : stopped 2");
			//saveFiles(1);
			System.out.println("Server : stopped 1");
			if (!taskRunning){
				terminateServers();
			}
    		System.out.println("Server : stopped ");
  		}
    	
    	else {
    		UDPServerThread.interrupt();
    		System.out.println("Server : stopping ");
    		stopUDPTask();
    	}
    	
    	
    	
    	/*scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                terminateServers();
                System.out.println("Servers Terminated");
            }}, 1000, TimeUnit.MILLISECONDS);*/
    }
    
    private void terminateServers(){
    	// Wait until the thread exits
    	try {
    		UDPServerThread.join(2000);
    		System.out.println("Server : Stopped successfully");
    	} catch (InterruptedException ex) {
    		// Unexpected interruption
    		System.out.println("Error : " + ex);
    		ex.printStackTrace();
    		System.exit(1);
    	}
   
    	stopTimer();
    	setChanged();
    	notifyObservers(isRunning);
    }


    public void run() {
    	isRunning= true;
    	setChanged();
    	notifyObservers(isRunning);
    	
    	chargeInfo= new ChargeInfo();
    	
    	//createUDPSocket2("run");
    	
    	/*report1();
		report2();
		report3();
		report100();*/
    	report1= reportServer.report1();
    	report2= reportServer.report2();
    	report3= reportServer.report3();
    	report100= reportServer.report100();
    	setChargeEndByReport();
		
		chargeInfo.setChargeID(
				database.getChargeID(
						report100.getSessionID()));		
		
		System.out.println("SessionID= " + report100.getSessionID() + " chargeID= " + chargeInfo.getChargeID());
		
		
		if (chargeInfo.getChargeID() != 0){	
			System.out.println("Get existing files");
			getExistingFiles();	
			if (report2.isPlugged()){
				chargeOngoing= true;
				updateChargeObjects();
			}
			else {
				//terminateCharge();
				chargeInfo= database.getCharge(chargeInfo.getChargeID());
			}
		}
		else {
			ldt = LocalDateTime.now();
			System.out.println("Create new charge call");
    		createNewCharge();
    		updateChargeObjects();
    		
    		System.out.println("CurrentChargeID= " + chargeInfo.getChargeID());
    		System.out.println("latestSessionID= " + report100.getSessionID());
		}
		
		startPollObservers();
		
		System.out.println("isPlugged= " + report2.isPlugged());
    	
		while (isRunning) {
			System.out.println("UDP Server running");
			createUDPSocket();
			//createUDPSocket2("run while is running");
			listen();
		}
    }
    
    
    
    private void listen(){
        String msg;
        System.out.println("Server isRunning= " + isRunning);
        
        if (fileOpen==false && report2.isPlugged()) {
        	ldt = LocalDateTime.now();
        	chargeInfo= new ChargeInfo();
    		createFiles(true);
    		chargeOngoing= true;
    	}
        
        System.out.println("Used chargeFile= " + chargeInfo.getChargeFilePath());
        System.out.println("Used stateFile= " + chargeInfo.getStateFilePath());
        
        while (isRunning) {
            
            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            
            // blocks until a packet is received
            try{
            	udpSocket.receive(packet);
            	
            } catch (IOException ex) {
            	if (udpSocket.isClosed()){
            		saveFiles(1);
            		isRunning= false;
            		setChanged();
            		notifyObservers(isRunning);
            		System.out.println("Socket closed");
            	}
            	else {
            		System.out.println("Server Socket Exception" + ex);
            		ex.printStackTrace();
            	}
            	break;
            }
            msg = new String(packet.getData()).trim();
            msg= msg.substring(1, msg.length()-1);
            
            if (packet.getAddress().getHostAddress().equals(WallboxIP)) {
            	ldt = LocalDateTime.now();
            	parseString(msg);
            }
            else System.out.println("InorrectIP of Wallbox : "+ packet.getAddress().getHostAddress());
        }
    }
    

    
    @SuppressWarnings("unchecked")
	private void parseString(String msg) {
    	String[] receivedMsg;
    	receivedMsg= msg.split(":");
    	String msgArgument= receivedMsg[0];
    	String msgValue= receivedMsg[1];
    	msgValue= msgValue.replaceAll("\\s+","");
    	//System.out.println(msgArgument+ " " + msgValue);
    	//System.out.println(msgValue);
    	
    	//report2();
    	report2= reportServer.report2();
    	chargeInfo.setPlugged(report2.isPlugged());
    	
    	switch (msgArgument) {
    	
    	// State = Current state of the charging station
    	case "\"State\"":
    		switch (Integer.parseInt(msgValue.trim())) {
    		// starting
    		case 0:
    			System.out.println("Starting state 0");
    			if (stateLogTimeTemp==null ){
    				stateLogTime.add(ldt.toString());
    				stateLogEvent.add("Starting");
    			}
    			else {
    				stateLogTimeTemp.add(ldt.toString());
    				stateLogEventTemp.add("Starting");
    			}
    			break;
    		// not ready for charging: e.g. unplugged, X1  or ena not enabled, RFID not enabled
    			/*
    			 * TERMINATE A LOAD alternative
    			 */
    		case 1:
    			System.out.println("report2.getPlug= " + report2.getPlug());
    			if (report2.getPlug() == 3 && chargeOngoing== true){
    				System.out.println("Unplugged on EV");
        			stateLogTime.add(ldt.toString());
            		stateLogEvent.add("Unplugged on EV");
        			terminateCharge();
    			}
    			else {
    				stateLogTime.add(ldt.toString());
    				stateLogEvent.add("Not ready");
    			}
    			break;
    		// ready for charging: Waiting for EV charging request (S2) - NEW LOADING SESSION!
    		case 2:
    			System.out.println("Plugged and ready for charging");
    			if (stateLogTimeTemp==null ){
    				stateLogTime.add(ldt.toString());
    				stateLogEvent.add("Ready - waiting for EV");
    			}
    			else {
    				stateLogTimeTemp.add(ldt.toString());
    				stateLogEventTemp.add("Ready - waiting for EV");
    			}
    			
    			break;
    		// charging
    		case 3:
    			System.out.println("Charging");
    			if (stateLogTimeTemp==null ){
    				stateLogTime.add(ldt.toString());
    				stateLogEvent.add("Charging");
    			}
    			else {
    				stateLogTimeTemp.add(ldt.toString());
    				stateLogEventTemp.add("Charging");
    			}
    			break;
    		// error
    		case 4:
    			System.out.println("Error");
    			if (stateLogTimeTemp==null ){
    				stateLogTime.add(ldt.toString());
    				stateLogEvent.add("Error");
    			}
    			else {
    				stateLogTimeTemp.add(ldt.toString());
    				stateLogEventTemp.add("Error");
    			}
    			break;
    		// Authorization rejected
    		case 5:
    			System.out.println("Auth rejected");
    			stateLogTime.add(ldt.toString());
        		stateLogEvent.add("Authorisation rejected");
        		/* TODO Check why it end in this state after Ena=0
        		stateLogTime.add(ldt.toString());
        		stateLogEvent.add("Unplugged on EV");
    			database.terminateCharging(ldt, chargingLogFile, stateLogFile);
    			saveFiles(1);
    			*/
        		//TODO End of lines t be removed
    			break;
    		default:
    			System.out.println("State default");
    			stateLogTime.add(ldt.toString());
        		stateLogEvent.add("Unknown state");
    			//"Not possible currently"
        		break;
    		}
    		break;
    	// Current condition of charging connection
    	case "\"Plug\"":
    		switch (Integer.parseInt(msgValue.trim())) {
    		// unplugged
    		case 0:		// Not used in stations with fixed cables
    			System.out.println("Unplugged on Wallbox");
    			stateLogTime.add(ldt.toString());
        		stateLogEvent.add("Unplugged on Wallbox");
    			break;
    		// plugged on charging station
    		case 1: // Not used in stations with fixed cables
    			System.out.println("Plugged on Wallbox");
    			stateLogTime.add(ldt.toString());
        		stateLogEvent.add("Plugged on Wallbox");
    			break;
    		// plugged on charging station 		plug locked
    			/*
    			 * TERMINATE A LOAD
    			 */
    		case 3:
    			System.out.println("Unplugged on EV");
    			stateLogTime.add(ldt.toString());
        		stateLogEvent.add("Unplugged on EV");
        		terminateCharge();
    			break;
    		// plugged on charging station 							plugged on EV
    		case 5: // Not used in stations with fixed cables
    			System.out.println("Plug 5");
    			break;
    		// plugged on charging station 		plug locked			plugged on EV
    			/*
    			 * START A LOAD
    			 */
    		case 7:
    			System.out.println("Plugged on EV");
    			if (fileOpen) {
    				saveFiles(1);
    			}
    			
    			createChargingObjectsTemp(true);
    			
    			/*schedulerIsRunning= true;
    			scheduler.schedule(new Runnable() {
    	            @Override
    	            public void run() {
    	            	System.out.println("Create new charge call (parse)");
    	        		createNewCharge();
    	        		updateChargeObjects();
    	        		
    	        		System.out.println("CurrentChargeID= " + chargeInfo.getChargeID());
    	        		System.out.println("latestSessionID= " + report100.getSessionID());
    	            }}, 500, TimeUnit.MILLISECONDS);*/
    			
    			break;
    		default:
    			System.out.println("Default");
    			stateLogTime.add(ldt.toString());
        		stateLogEvent.add("Unknown plug state");
    			// currently not possible
        		break;
    		}
    		break;
    	// State of the potential free Enable input X1
    	case "\"Input\"":
    		stateLogTime.add(ldt.toString());
    		if (msgValue.equals("1")){stateLogEvent.add("External Input ON");}
    		else {stateLogEvent.add("External Input OFF");}
    		break;
    	// Enable state for charging (contains Enable input, RFID, UDP, ...)
    	case "\"Enable sys\"":
    		stateLogTime.add(ldt.toString());
    		if (msgValue.equals("1")){stateLogEvent.add("Enabled per command");}
    		else {stateLogEvent.add("Disbaled per command");}
    		break;
    	// Current preset value via Control pilot in Milliampere
    	case "\"Max curr\"":
    		stateLogTime.add(ldt.toString());
    		stateLogEvent.add("Max Current Set to: " + msgValue);
    		break;
    	// Power consumption of the current charging session in 0.1kWh. Resets with "State=2"
    	case "\"E pres\"":
    		if (chargingLogTimeTemp== null){
    			updateChargingLog(ldt, msgValue);
    		}
    		else {
    			updateChargingLogTemp(ldt, msgValue);
    		}
    		
    		//System.out.println(jsonObject);
    		break;
    	default:
    		// Log to unexpected message received logfile
    		stateLogTime.add(ldt.toString());
    		stateLogEvent.add("Unknown Command received _" + msgArgument + "_" + msgValue + "_");
    		System.out.println("other message received: " + msgArgument);
    	break;
    	}
    	
    	checkReport100();
    	updateChargingFile(1);
    	updateStateFile();
    }
    
    private void terminateCharge(){
    	if (chargeOngoing){
	    	//report2= reportServer.report2();
	    	System.err.println("rep100.getEnded= " + report100.getEnded());
			//database.terminateCharge(chargeInfo.getChargeID(), ldt, 0, chargeInfo.getChargeFilePath(), 
			//		chargeInfo.getStateFilePath(), report100.getEpres());
	    	if (report100.getEnded()!= null){
	    		chargeInfo.setEndDateTime(report100.getEnded());
	    		System.out.println("Correct time set");
	    	}
	    	else {
	    		ldt= LocalDateTime.now();
	    		chargeInfo.setEndDateTime(ldt);
	    		System.out.println("System time set");
	    	}
			database.terminateCharge(chargeInfo);
			saveFiles(1);		
			chargeOngoing= false;
    	}
    }
    
    private void setChargeEndByReport(){
    	Report100 rep101= reportServer.reportI("101");
    	System.err.println("rep101.getEnded= " + rep101.getEnded());
    	System.err.println("rep101.getSessionID= " + rep101.getSessionID());
    	int lastChargeID= database.chargeIDofSessionID(
    			rep101.getSessionID());
    	
    	if (!database.getCharge(lastChargeID).isEndSetByReport() && rep101.getEnded()!= null){
    		database.setChargeEnd(lastChargeID, rep101.getEnded());
    		database.setChargeEndbyReport(lastChargeID, true);
    	}
    }
    
    private void checkReport100(){
    	//report100();
    	report100= reportServer.report100();
    	if (report100.getSessionID()!= chargeInfo.getSessionID()){
    		if (chargeOngoing){
    			terminateCharge();
    		}
    		createNewCharge();
    	}
    }
    
    private void createChargingObjectsTemp(boolean serverStarted){
    	ldt=LocalDateTime.now();
    	chargingLogTemp= new JSONObject();
    	chargingLogTimeTemp= new JSONArray();
		chargingLogPowerTemp= new JSONArray();
		chargingLogVoltageTemp= new JSONArray();
		chargingLogCurrentTemp= new JSONArray();
		System.out.println("New Temp Object created ChargingLog");
		
		stateLogTemp= new JSONObject();
		stateLogTimeTemp= new JSONArray();
		stateLogEventTemp= new JSONArray();
		System.out.println("New Temp Object created StateLog");
    }
    
    private void createNewCharge(){
    	chargeInfo= new ChargeInfo();
    	chargeInfo.setChargeInfo(report100);
    	
    	setChargeEndByReport();
    			
    	createFiles(true);
		
    	if(!database.checkIfSessionIDExists(report100.getSessionID())){
    		//CREATE A NEW CHARGING ENTRY
    		chargeInfo.setChargeID(
    				database.createCharge(
    						chargeInfo.getStartDateTime(),
    						chargeInfo.getSessionID()
    						).getChargeID());
    		System.out.println("Chargefilepath added to DB= " + chargeInfo.getChargeFilePath());
			database.setChargeFilePath(chargeInfo.getChargeID(), chargeInfo.getChargeFilePath());
			System.out.println("Statefilepath added to DB= " + chargeInfo.getStateFilePath());
			database.setStateFilePath(chargeInfo.getChargeID(), chargeInfo.getStateFilePath());
    	}
    	if (schedulerIsRunning){
    		scheduler.shutdown();
    		schedulerIsRunning= false;
    	}
    	
    	chargeOngoing= true;
    }
    
    @SuppressWarnings("unchecked")
	private void createFiles(boolean serverStarted) {
    	//report100();
    	report100= reportServer.report100();
    	//report3();
    	report3= reportServer.report3();
    	
    	sessionStart= dateToFileName(report100.getStarted());
    	chargingLog= new JSONObject();
    	chargingLogTime= new JSONArray();
		chargingLogTime.add(report100.getStarted().toString());
		;
		chargingLogPower= new JSONArray();
		chargingLogPower.add(report100.getEpres());
		
		chargingLogVoltage= new JSONArray();
		chargingLogVoltage.add(report3.getU1());
		
		chargingLogCurrent= new JSONArray();
		chargingLogCurrent.add(report3.getI1());
		System.out.println("New Object created ChargingLog");
    	
    	if (chargingLogTimeTemp!= null && chargingLogTimeTemp.size()>1){
    		for (int i= 1; i<chargingLogTimeTemp.size(); i++){
    			chargingLogTime.add(chargingLogTimeTemp.get(i));
    			chargingLogPower.add(chargingLogPowerTemp.get(i));
    			chargingLogVoltage.add(chargingLogVoltageTemp.get(i));
    			chargingLogCurrent.add(chargingLogCurrentTemp.get(i));
    		}
    	}
    	chargingLog.put("date", chargingLogTime);
    	chargingLog.put("value", chargingLogPower);
    	chargingLog.put("voltage", chargingLogVoltage);
		chargingLog.put("current", chargingLogCurrent);
    	
    	chargingLogTemp= null;
    	chargingLogTimeTemp= null;
    	chargingLogPowerTemp= null;
    	chargingLogVoltageTemp= null;
    	chargingLogCurrentTemp= null;
    	//chargeInfo.setStartDateTime(report100.getStarted());
    	//ldt = LocalDateTime.now();
    	
    	
		stateLog= new JSONObject();
		stateLogTime= new JSONArray();
		stateLogTime.add(report100.getStarted());
		
		stateLogEvent= new JSONArray();
		if (serverStarted) stateLogEvent.add("Server started");
		else stateLogEvent.add("Plugged on EV");
		
		System.out.println("New Object created StateLog");
		
		if (stateLogTimeTemp!= null && stateLogTimeTemp.size()>1){
    		for (int i= 1; i<stateLogTimeTemp.size(); i++){
    			stateLogTime.add(stateLogTimeTemp.get(i));
    			stateLogEvent.add(stateLogEventTemp.get(i));
    		}
    	}
		stateLog.put("date", stateLogTime);
		stateLog.put("state/plug", stateLogEvent);
		
		stateLogTimeTemp= null;
		stateLogEventTemp= null;
		
		createChargeFile();
		createStateFile();
	
		fileOpen= true;
    }
    
    @SuppressWarnings("unchecked")
	private void updateChargingLog(LocalDateTime ldt, String msgValue){
    	chargingLogTime.add(ldt.toString());
		chargingLogPower.add(Integer.parseInt(msgValue.trim()));
		report3= reportServer.report3();
		chargingLogVoltage.add(report3.getU1());
		chargingLogCurrent.add(report3.getI1());
    }
    
    @SuppressWarnings("unchecked")
	private void updateChargingLogTemp(LocalDateTime ldt, String msgValue){
    	chargingLogTimeTemp.add(ldt.toString());
		chargingLogPowerTemp.add(Integer.parseInt(msgValue.trim()));
		report3= reportServer.report3();
		chargingLogVoltageTemp.add(report3.getU1());
		chargingLogCurrentTemp.add(report3.getI1());
    }
    
    private void updateChargeObjects(){
    	if (isRunning){
    		/*report100();
	    	report2();
	    	report3();*/
    		report100= reportServer.report100();
    		report2= reportServer.report2();
    		report3= reportServer.report3();

		   	chargeData= new ChargeData(chargingLog);
		   	chargeData.setSessionID(report100.getSessionID());
		   	//System.out.println("ChargeTime= " + chargeData.getChargeTime());
				    	
		   	chargeInfo.setChargeInfo(report100);
		   	chargeInfo.setPlugged(report2.isPlugged());
		   	chargeInfo.setChargeTime(chargeData.getChargeTime());
		   	chargeInfo.setCharging(report3.getI1()> 0 || report3.getI2()> 0 || report3.getI3()> 0);
				 
		   	chargeObject.setChargeInfo(chargeInfo);
		   	chargeObject.setChargeData(chargeData);
		   	//System.out.println("NOT OBS 3" + chargeInfo.getPluggedTime());
		   	setChanged(); 
			notifyObservers(chargeObject);
    	}
    }
    
    private void createChargeFile(){
    	//ldt = LocalDateTime.now();
    	sessionStart= dateToFileName(report100.getStarted());
    	FileHandler fileHandler= FileHandler.getInstance();
    	chargeInfo.setChargeFilePath(fileHandler.generateFile("chargeInfo"+sessionStart, "charging"));
    	System.out.println("Chargefilepath created= " + chargeInfo.getChargeFilePath());
    	fileHandler.writeJsontoFile(chargingLog, chargeInfo.getChargeFilePath());
    }
    
    private void createStateFile(){
    	//ldt = LocalDateTime.now();
    	sessionStart= dateToFileName(report100.getStarted());
    	FileHandler fileHandler= FileHandler.getInstance();
    	chargeInfo.setStateFilePath(fileHandler.generateFile("state"+sessionStart, "state"));
    	System.out.println("Statefilepath created= " + chargeInfo.getStateFilePath());
    	fileHandler.writeJsontoFile(stateLog, chargeInfo.getStateFilePath());
    }
    
	private void getExistingFiles(){
    	FileHandler fileHandler= FileHandler.getInstance();
    	chargeInfo.setChargeFilePath(database.getChargeFilePath(chargeInfo.getChargeID()));
    	if (chargeInfo.getChargeFilePath()== null){
    		createFiles(true);
    		database.setChargeFilePath(chargeInfo.getChargeID(), chargeInfo.getChargeFilePath());
    	}
    	else {
    		chargingLog= fileHandler.getJsonFromFile(chargeInfo.getChargeFilePath());
        	chargingLogTime= (JSONArray)chargingLog.get("date");
        	chargingLogPower= (JSONArray)chargingLog.get("value"); 
        	chargingLogVoltage= (JSONArray)chargingLog.get("voltage"); 
        	chargingLogCurrent= (JSONArray)chargingLog.get("current"); 
        	fileOpen= true;
    	}
		
    	chargeInfo.setStateFilePath(database.getStateFilePath(chargeInfo.getChargeID()));
    	if (chargeInfo.getStateFilePath()== null){
    		createFiles(true);
    		database.setStateFilePath(chargeInfo.getChargeID(), chargeInfo.getStateFilePath());
    	}
    	else {    	
    		stateLog= fileHandler.getJsonFromFile(chargeInfo.getStateFilePath());
    		stateLogTime= (JSONArray)stateLog.get("date");
    		stateLogEvent= (JSONArray)stateLog.get("state/plug");
    		fileOpen= true;
    	}
    }
    
	@SuppressWarnings("unchecked")
	private void updateChargingFile(int phases){
		//System.out.println("File = " + chargeInfo.getChargeFilePath());
    	FileHandler fileHandler= FileHandler.getInstance();
    	JSONObject chargeLogTemp= fileHandler.getJsonFromFile(chargeInfo.getChargeFilePath());
    	//System.out.println("chargeLogTemp= " + chargeLogTemp);
    	if (chargeLogTemp != null) {
    		chargeLogTemp.put("date", chargingLogTime);
        	chargeLogTemp.put("value", chargingLogPower);
        	chargeLogTemp.put("voltage", chargingLogVoltage);
        	chargeLogTemp.put("current", chargingLogCurrent);
    	}
     	
    	chargingLog= chargeLogTemp;
    	
    	fileHandler.overwriteFile(chargingLog, chargeInfo.getChargeFilePath());
    	
    	updateChargeObjects();
    	//System.out.println("NOT OBS 2" + chargeObject.getChargingData().getSessionID());
		}
	
	@SuppressWarnings("unchecked")
	private void updateStateFile(){
    	FileHandler fileHandler= FileHandler.getInstance();
    	JSONObject stateLogTemp= fileHandler.getJsonFromFile(chargeInfo.getStateFilePath());
    
    	stateLogTemp.put("date", stateLogTime);
    	stateLogTemp.put("state/plug", stateLogEvent);
    	
    	stateLog= stateLogTemp;
    	fileHandler.overwriteFile(stateLog, chargeInfo.getStateFilePath());
    }
	
	private void saveFiles(int phases){
		updateChargingFile(phases);
		updateStateFile();
		fileOpen= false;
	}
    
    private String dateToFileName (LocalDateTime ldt) {
    	Integer ldtYear= ldt.getYear();
    	String ldtYearStr= Integer.toString(ldtYear);
    	Integer ldtMonth= ldt.getMonthValue();
    	String ldtMonthStr= Integer.toString(ldtMonth);
    	if (ldtMonthStr.length()<2) ldtMonthStr= "0"+ldtMonthStr;
    	Integer ldtDay= ldt.getDayOfMonth();
    	String ldtDayStr= Integer.toString(ldtDay);
    	if (ldtDayStr.length()<2) ldtDayStr= "0"+ldtDayStr;
    	Integer ldtHour= ldt.getHour();
    	String ldtHourStr= Integer.toString(ldtHour);
    	if (ldtHourStr.length()<2) ldtHourStr= "0"+ldtHourStr;
    	if (ldtHourStr.length()<2) ldtHourStr= "0"+ldtHourStr;
    	Integer ldtMinute= ldt.getMinute();
    	String ldtMinuteStr= Integer.toString(ldtMinute);
    	if (ldtMinuteStr.length()<2) ldtMinuteStr= "0"+ldtMinuteStr;
    	if (ldtMinuteStr.length()<2) ldtMinuteStr= "0"+ldtMinuteStr;
    	Integer ldtSeconds= ldt.getSecond();
    	String ldtSecondsStr= Integer.toString(ldtSeconds);
    	if (ldtSecondsStr.length()<2) ldtSecondsStr= "0"+ldtSecondsStr;
    	if (ldtSecondsStr.length()<2) ldtSecondsStr= "0"+ldtSecondsStr;
    	String fileName= ldtYearStr+ ldtMonthStr+ ldtDayStr+ ldtHourStr+ ldtMinuteStr+ ldtSecondsStr+".json";
    	return fileName;
    }
    
    public Report100 report100(){
    	taskRunning= true;
	    	try {
	        	InetAddress IPAddress = InetAddress.getByName(WallboxIP);
	        	byte[] sendData = new byte[512];
	        	byte[] receiveData = new byte[512];
	        	String sentence = "report 100";
	        	sendData = sentence.getBytes();
	        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port2);
	        	udpSocket2.send(sendPacket);
	        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        	udpSocket2.receive(receivePacket);
	        	
	        	String modifiedSentence = new String(receivePacket.getData());
	        	report100.createFromString(modifiedSentence);
	        	report100.setPluggedSeconds(System.currentTimeMillis() / 1000l);
	   
			} catch (Exception e) {
				//createUDPSocket2("report100");
				e.printStackTrace();
				System.out.println(e);
			}
	    taskRunning= false;
    	return report100;
    }
    
    public Report100 reportI(String reportnumber){
    	Report100 reportI= new Report100();
    	taskRunning= true;
	    	try {
	        	InetAddress IPAddress = InetAddress.getByName(WallboxIP);
	        	byte[] sendData = new byte[512];
	        	byte[] receiveData = new byte[512];
	        	String sentence = "report 1" + reportnumber;
	        	sendData = sentence.getBytes();
	        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port2);
	        	udpSocket2.send(sendPacket);
	        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        	udpSocket2.receive(receivePacket);
	        	
	        	String modifiedSentence = new String(receivePacket.getData());
	        	
	        	reportI.createFromString(modifiedSentence);
	        	System.out.println("ReportI new ID= " + reportI.getSessionID());
			} catch (Exception e) {
				//createUDPSocket2("reportI");
				e.printStackTrace();
				System.out.println(e);
			}
	    taskRunning= false;
    	
    	return reportI;
    }
    
    public void report1(){
    	taskRunning= true;
	    	try {
	        	InetAddress IPAddress = InetAddress.getByName(WallboxIP);
	        	byte[] sendData = new byte[512];
	        	byte[] receiveData = new byte[512];
	        	String sentence = "report 2";
	        	sendData = sentence.getBytes();
	        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port2);
	        	udpSocket2.send(sendPacket);
	        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        	udpSocket2.receive(receivePacket);
	        	
	        	String modifiedSentence = new String(receivePacket.getData());
	        	report1.createFromString(modifiedSentence);
			} catch (Exception e) {
				//createUDPSocket2("report1");
				e.printStackTrace();
				System.out.println(e);
			}
	    taskRunning= false;
    }
    
    public void report2(){
	    	try {
	        	InetAddress IPAddress = InetAddress.getByName(WallboxIP);
	        	byte[] sendData = new byte[512];
	        	byte[] receiveData = new byte[512];
	        	String sentence = "report 2";
	        	sendData = sentence.getBytes();
	        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port2);
	        	udpSocket2.send(sendPacket);
	        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        	udpSocket2.receive(receivePacket);
	        	
	        	String modifiedSentence = new String(receivePacket.getData());
	        	report2.createFromString(modifiedSentence);
			} catch (Exception e) {
				//createUDPSocket2("report2");
				e.printStackTrace();
				System.out.println(e);
			}
    }
    
    public void report3(){
    	taskRunning= true;
	    	try {
	        	InetAddress IPAddress = InetAddress.getByName(WallboxIP);
	        	byte[] sendData = new byte[512];
	        	byte[] receiveData = new byte[512];
	        	String sentence = "report 3";
	        	sendData = sentence.getBytes();
	        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port2);
	        	
	        	udpSocket2.send(sendPacket);
	        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        	udpSocket2.receive(receivePacket);
	        	
	        	String modifiedSentence = new String(receivePacket.getData());
	        	report3.createFromString(modifiedSentence);
			} catch (Exception e) {
				//createUDPSocket2("report3");
				e.printStackTrace();
				System.out.println(e);
			}
	    taskRunning= false;
    } 
    
    private void createUDPSocket(){
    	if (udpSocket== null || udpSocket.isClosed()){
			try {
				this.udpSocket = new DatagramSocket(port);
				System.out.println("Socket created: Reason: 1");
			} catch (SocketException e) {
				//TODO message that server cannot be started due to port 7092 used
				stopTaskOnError();
				e.printStackTrace();
			}
		}
    }
    
    /*private void createUDPSocket2(String where){
    	System.out.println("Socket 2 createed at " + where);
    	if (udpSocket2== null || udpSocket2.isClosed()){
			try {
				System.out.println("Socket2 created: Reason: is null1");
				this.udpSocket2 = new DatagramSocket(port2);
				System.out.println("Socket2 created: Reason: is null2");
			} catch (SocketException e) {
				//TODO message that server cannot be started due to port 7090 used
				System.out.println("Error createUDP2 1");
				//stopTaskOnError();
				e.printStackTrace();
			}
		}
    }*/
    
    public void stopTaskOnError(){
    	if (isRunning){
    		stopUDPTask();
    	}
    }
    
    public boolean getServerState(){
    	return this.isRunning;
    }

    
    
    // ___________________________________________________________________________________
    // Observer pattern
    //____________________________________________________________________________________
    
    
}
