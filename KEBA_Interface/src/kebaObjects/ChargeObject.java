package kebaObjects;

import java.io.Serializable;
import java.util.Observable;

public class ChargeObject extends Observable implements Serializable{
	private static final long serialVersionUID = 7815040133943067596L;
	
	private ChargeInfo chargeInfo;
	private ChargeData chargeData;	
	
	public ChargeObject(){
	}
	
	public ChargeObject(ChargeInfo chargeInfo, ChargeData chargeData){
		this.setChargeInfo(chargeInfo);
		this.setChargeData(chargeData);
	}

	public ChargeData getChargeData() {
		return chargeData;
	}

	public void setChargeData(ChargeData chargeData) {
		this.chargeData = chargeData;
		setChanged();
		notifyObservers(this);
	}

	public ChargeInfo getChargeInfo() {
		return chargeInfo;
	}

	public void setChargeInfo(ChargeInfo chargeInfo) {
		this.chargeInfo = chargeInfo;
		setChanged();
		notifyObservers(this);
	}
}
