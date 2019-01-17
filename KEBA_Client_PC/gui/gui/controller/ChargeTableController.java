package gui.controller;

import keba.rmiinterface.KEBADataInterface2;
import gui.view.ChargeTableView;

public class ChargeTableController {
	private ChargeTableView chargeView;
	private KEBADataInterface2 serverData;

	public ChargeTableController(KEBADataInterface2 serverData){
		this.serverData= serverData;
		this.chargeView= new ChargeTableView();
	}
	
	public ChargeTableView getChargeView() {
		return this.chargeView;
	}
}
