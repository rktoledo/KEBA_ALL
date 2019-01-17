package dbDAOInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;

import kebaObjects.ChargeInfo;

/**
 * Interface fuer die Erzeugung der Datenbank "FLADAN" und deren Tabellen
 * 
 * @author Ron Peyer
 * @version 1.0
 * @since Jul.2018
 *
 */
public interface KebaDbDAO{
    
	// Database actions
    boolean createDB();
    boolean createTable(String query);
    

	int chargeIDofSessionID(int sessionID);
    
    // Session ID
    void setSessionID(int chargeID, int sessionID); 
    int getSessionID(int chargeID);
    
    // Create Charge
    ChargeInfo createCharge(LocalDateTime chargeStart, int sessionID);
    
    // Charge start time
    LocalDateTime getChargeStart(int chargeID);
    
    // Charge end time
    void setChargeEnd(int chargeID, LocalDateTime chargeEnd);
    LocalDateTime getChargeEnd(int chargeID);
    
    void setChargeEndbyReport(int chargeID, boolean isEndSetByReport);
   
    // Charged Time
    void setChargeTime(int chargeID, int chargeTime);
    int getChargeTime(int chargeID);
    
    // Charged Energy
    void setChargedEnergy(int chargeID, int chargedEnergy);
    int getChargedEnergy(int chargeID);
    
    
    // Charge File Path 
    void setChargeFilePath(int chargeID, String chargeFilePath);
    String getChargeFilePath(int chargeID);
    String getChargeFilePathbySessionID(int sessionID);
    
    // Charge state file
    void setStateFilePath(int chargeID, String stateFilePath);
    String getStateFilePath(int chargeID); 
    String getStateFilePathbySessionID(int sessionID);
    
    // Terminate a charge
    void terminateCharge(int chargeID, LocalDateTime chargeEnd, int chargeTime, String chargeFilePath, 
    		String stateFilePath, int chargedEnergy);
    void terminateCharge(ChargeInfo chargeInfo);
    
    void updateCharge(ChargeInfo chargeInfo);
    
    // Session ID
    boolean checkIfSessionIDExists(int sessionID);
    int getLatestSessionID();

    // Charge ID
	int getLatestChargeID();
	int getChargeID(int sessionID);

	// Get Charge Info
	ArrayList<ChargeInfo> getChargingsFrom(LocalDateTime chargeStartRange);
	ArrayList<ChargeInfo> getChargingsTo(LocalDateTime chargeEndRange);
	ArrayList<ChargeInfo> getChargingsFromTo(LocalDateTime chargeStartRange, LocalDateTime chargeEndRange);
	ArrayList<ChargeInfo> getAllChargings();
	ChargeInfo getCharge(int chargeID);
	ChargeInfo getLatestCharge();
    
}
