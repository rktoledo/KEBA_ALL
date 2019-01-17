package gui.controller;

import java.awt.event.MouseListener;

import kebaObjects.ChargeInfo;

import gui.view.ChargeInfoView;

public class ChargeInfoController{
	private ChargeInfoView view;
	
	public ChargeInfoController(ChargeInfo chargeInfo){
		//this.charging.setChargeTime(chargeObject.getChargeInfo().getChargeTime());
		this.view = new ChargeInfoView(chargeInfo);

		updateInfo(chargeInfo);
		//System.out.println("Initial value Epres= " + charging.getChargeedEnergy());
	}
	
	public ChargeInfoView getView(){
		return this.view;
	}
	
	public void addZoomListener(MouseListener listener){
		this.view.getView().addMouseListener(listener);
	}
	
	public void updateInfo(ChargeInfo chargeInfo){
		if (chargeInfo != null){
			//System.out.println("LIC uüdate Epres: " + charge.getChargingData().getEpres());			
			this.view.updateView(chargeInfo);
		}
	}
}
