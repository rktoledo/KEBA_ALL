package udpServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import kebaObjects.Report1;
import kebaObjects.Report100;
import kebaObjects.Report2;
import kebaObjects.Report3;

public class ReportServer implements Runnable{
	
	private DatagramSocket udpSocket2;
    private static int port2= 7090;
    private static String WallboxIP= "192.168.1.33";

    private boolean isRunning= false;
    private boolean taskRunning= false;
    
    private Report1 report1= new Report1();
    private Report2 report2= new Report2();
    private Report3 report3= new Report3();
    private Report100 report100= new Report100();

    private Thread ReportServerThread;
    
    public ReportServer (){
    }
 
    /*
     * Start the UDP Server
     */
    public void startUDPTask() {
    	if(!isRunning){
    		ReportServerThread = new Thread(this);
    		ReportServerThread.start();
    	}
    }
    
    /*
     * STOP UDP Server
     */
	public void stopUDPTask() { 
    	if (!this.udpSocket2.isClosed()){
    		this.udpSocket2.close();
    	}
    	
    	isRunning= false;
      // Interrupt the thread so it unblocks any blocking call
    	if (ReportServerThread.isInterrupted()) {
			System.out.println("Server : stopped 1");
			if (!taskRunning){
				terminateServers();
			}
    		System.out.println("Server : stopped ");
  		}
    	
    	else {
    		ReportServerThread.interrupt();
    		System.out.println("Server : stopping ");
    		stopUDPTask();
    	}
    }
    
    private void terminateServers(){
    	// Wait until the thread exits
    	try {
    		ReportServerThread.join(2000);
    		System.out.println("Server : Stopped successfully");
    	} catch (InterruptedException ex) {
    		// Unexpected interruption
    		System.out.println("Error : " + ex);
    		ex.printStackTrace();
    		//System.exit(1);
    	}
    }


    public void run() {
    	isRunning= true;
    	createUDPSocket2("ReportServer");
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
				createUDPSocket2("report100");
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
	        	String sentence = "report " + reportnumber;
	        	sendData = sentence.getBytes();
	        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port2);
	        	udpSocket2.send(sendPacket);
	        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        	udpSocket2.receive(receivePacket);
	        	
	        	String modifiedSentence = new String(receivePacket.getData());
	        	
	        	reportI.createFromString(modifiedSentence);
	        	System.out.println("ReportI new ID= " + reportI.getSessionID());
			} catch (Exception e) {
				createUDPSocket2("reportI");
				e.printStackTrace();
				System.out.println(e);
			}
	    taskRunning= false;
    	
    	return reportI;
    }
    
    public Report1 report1(){
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
				createUDPSocket2("report1");
				e.printStackTrace();
				System.out.println(e);
			}
	    taskRunning= false;
	    return report1;
    }
    
    public Report2 report2(){
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
				createUDPSocket2("report2");
				e.printStackTrace();
				System.out.println(e);
			}
	    	return report2;
    }
    
    public Report3 report3(){
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
				createUDPSocket2("report3");
				e.printStackTrace();
				System.out.println(e);
			}
	    taskRunning= false;
	    return report3;
    } 
    
    private void createUDPSocket2(String where){
    	System.out.println("Socket 2 createed at " + where);
    	if (udpSocket2== null || udpSocket2.isClosed()){
			try {
				System.out.println("Socket2 created: Reason: is null1");
				this.udpSocket2 = new DatagramSocket(port2);
				System.out.println("Socket2 created: Reason: is null2");
			} catch (SocketException e) {
				//TODO message that server cannot be started due to port 7090 used
				System.out.println("Error createUDP2 1");
				stopTaskOnError();
				e.printStackTrace();
			}
		}
    }
    
    public void stopTaskOnError(){
    	if (isRunning){
    		//stopUDPTask();
    	}
    }
    
    public boolean getServerState(){
    	return this.isRunning;
    }
}
