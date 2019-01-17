package kebaObjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChargeInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int chargeID;
	private int sessionID;
	private int chargeTime;
	private int energyStart;
	private int chargedEnergy;
	private int energyEnd;
	private int pluggedTime;
	private boolean isComplete;
	private boolean isUsable;
	
	private boolean isChargeFileAvailable;
	private String chargeFilePath;
	
	private boolean isStateFileAvailable;
	private String stateFilePath;
	
	private LocalDateTime pluginTime;
	private LocalDateTime plugoutTime;
	private boolean isEndSetByReport;
	
	private boolean isPlugged;
	private boolean isCharging;

	
	public ChargeInfo(){
	}
	
	public ChargeInfo(int chargeID, int sessionID, LocalDateTime startDateTime, LocalDateTime endDateTime, 
				int chargeTime, boolean isComplete, boolean isUsable, int chargedEnergy, 
				Boolean isSessionIDSet){
		this.chargeID= chargeID;
		this.sessionID= sessionID;
		this.chargeTime= chargeTime;
		this.isComplete= isComplete;
		this.isUsable= isUsable;
		this.chargedEnergy= chargedEnergy;
		this.pluginTime= startDateTime;
		this.plugoutTime= endDateTime;
	}
	
	public void setChargeInfo(Report100 rep100){
		if (rep100!= null){
			this.sessionID= rep100.getSessionID();
			this.chargedEnergy= rep100.getEpres();
			this.pluginTime= rep100.getStarted();
			this.plugoutTime= rep100.getEnded();
			this.energyStart= rep100.getEstart();
			this.energyEnd= rep100.getEpres()+rep100.getEstart();
			this.chargedEnergy= rep100.getEpres();
			this.pluggedTime= rep100.getPluggedSeconds();
		}
	}

	public int getChargeID() {
		return chargeID;
	}

	public int getSessionID() {
		return sessionID;
	}
	
	public String getSessionIDString() {
		return Integer.toString(sessionID);
	}

	public int getChargeTime() {
		return chargeTime;
	}

	public int getChargedEnergy() {
		return chargedEnergy;
	}
	
	public String getChargedEnergyString() {
		BigDecimal result;
		if (chargedEnergy<10000){
			result= round((((float)chargedEnergy)/10),0);
			return result.toString() + "Wh";
		}
		else {
			result= round((((float)chargedEnergy)/10000),3);
			return result.toString() + "kWh";
		}
	}
	
	private static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);       
        return bd;
    }

	public boolean isComplete() {
		return isComplete;
	}

	public boolean isUsable() {
		return isUsable;
	}

	public boolean isChargeFileAvailable() {
		return isChargeFileAvailable;
	}

	public boolean isStateFileAvailable() {
		return isStateFileAvailable;
	}

	public LocalDateTime getStartDateTime() {
		return pluginTime;
	}
	
	public String getStartDateTimeString() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.pluginTime);
	}
	
	/*public String getStartDateTimeString() {
		return pluginTime;
	}*/

	public LocalDateTime getEndDateTime() {
		return plugoutTime;
	}

	public void setChargeID(int chargeID) {
		this.chargeID = chargeID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public void setChargeTime(int chargeTime) {
		this.chargeTime = chargeTime;
	}

	public void setChargedEnergy(int chargedEnergy) {
		this.chargedEnergy = chargedEnergy;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public void setUsable(boolean isUsable) {
		this.isUsable = isUsable;
	}

	public void setChargeFileAvailable(boolean isChargeFileAvailable) {
		this.isChargeFileAvailable = isChargeFileAvailable;
	}

	public String getChargeFilePath() {
		return chargeFilePath;
	}

	public void setChargeFilePath(String chargeFilePath) {
		this.chargeFilePath = chargeFilePath;
	}

	public void setStateFileAvailable(boolean isStateFileAvailable) {
		this.isStateFileAvailable = isStateFileAvailable;
	}

	public String getStateFilePath() {
		return stateFilePath;
	}

	public void setStateFilePath(String stateFilePath) {
		this.stateFilePath = stateFilePath;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.pluginTime = startDateTime;
	}
	
	public void setStartDateTime(String startDateTime) {
		this.pluginTime = null;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.plugoutTime = endDateTime;
	}
	
	public void setEndDateTime(String endDateTime) {
		this.plugoutTime = null;
	}
	

	public String toString(int seconds){
		int days= seconds/(24*60*60);
		seconds= seconds-days*(24*60*60);
		int hours= seconds/(60*60);
		seconds= seconds-hours*(60*60);
		int min= seconds/60;
		seconds= seconds-min*60;
		String str= days + " days, " + hours + " hours " + min + " minutes " + seconds + " seconds";
		return str;
	}

	public int getPluggedTime() {
		return pluggedTime;
	}

	public void setPluggedTime(int pluggedTime) {
		this.pluggedTime = pluggedTime;
	}	

	public int getEnergyStart() {
		return energyStart;
	}

	public void setEnergyStart(int energyStart) {
		this.energyStart = energyStart;
	}

	public int getEnergyEnd() {
		return energyEnd;
	}

	public void setEnergyEnd(int energyEnd) {
		this.energyEnd = energyEnd;
	}

	public boolean isPlugged() {
		return isPlugged;
	}
	
	public String isPluggedString() {
		if (this.isPlugged){
			return "Plugged";
		}
		else {
			return "Unplugged";
		}
	}

	public void setPlugged(boolean isPlugged) {
		this.isPlugged = isPlugged;
	}

	public boolean isCharging() {
		return isCharging;
	}
	
	public String isChargingString() {
		if (this.isCharging){
			return "Charging";
		}
		else {
			return "Not charging";
		}
	}
	
	public String isCompleteString() {
		if (this.isComplete){
			return "Yes";
		}
		else {
			return "No";
		}
	}
	
	public String getEndDateTimeString() {
		if (this.isComplete()){
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.plugoutTime);
		}
		else {
			return "N/A";
		}
	}

	public void setCharging(boolean isCharging) {
		this.isCharging = isCharging;
	}

	public boolean isEndSetByReport() {
		return isEndSetByReport;
	}

	public void setEndSetByReport(boolean isEndSetByReport) {
		this.isEndSetByReport = isEndSetByReport;
	}
	
}
