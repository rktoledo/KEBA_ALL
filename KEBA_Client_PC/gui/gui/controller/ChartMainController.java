package gui.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

import javax.swing.JPanel;

import gui.model.KEBAClientSettings;
import gui.view.ChartView;
import gui.view.RPanel;
import keba.rmiinterface.KEBADataInterface2;
import keba.rmiinterface.KEBAServerInterface;
import kebaObjects.ChargeInfo;
import kebaObjects.ChargeObject;
public class ChartMainController {
	
	private KEBADataInterface2 serverData;
	
	private ChartView chartView;
	
	private XY_GraphController sumEnergyGraphController;
	private XY_GraphController sumVoltageGraphController;
	private XY_GraphController sumCurrentGraphController;
	private ChargeInfoController sumChargeInfoController;
	
	//private ChargeInfo chargeInfo= null;
	
	/*private XY_GraphController energyGraphController;
	private XY_GraphController voltageGraphController;
	private XY_GraphController currentGraphController;
	private ChargeInfoController chargeInfoController;

	private JPanel energyGraph;
	private JPanel voltageGraph;
	private JPanel currentGraph;
	private ChargeInfoView infoView;*/
	
	private ChargeObject chargeObject;
	
	private KEBAClientSettings settings;
	

	public ChartMainController(ChargeObject chargeObject, boolean currentCharge){
		this.chargeObject= chargeObject;
		
		settings= KEBAClientSettings.getInstance();
		
		createGraphs(chargeObject);
		
		this.sumChargeInfoController = new ChargeInfoController(chargeObject.getChargeInfo());
		
		this.chartView= new ChartView(sumEnergyGraphController.getView(), sumVoltageGraphController.getView(), 
				sumCurrentGraphController.getView(), sumChargeInfoController.getView(), currentCharge);
		
		addMouseListener(new BoxListener());
	}
	
	/*public void getData(){
		try {
			//if (!chargeInfo.isComplete()){
			// TODO	Information should be available without the need of UDP Server as 
			// it should not be used for new charges if server is not started.
			this.chargeObject= this.serverData.getLatestChargeObject();

			//}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void addMouseListener(BoxListener listener){
		this.sumEnergyGraphController.addZoomListener(listener);
		this.sumVoltageGraphController.addZoomListener(listener);
		this.sumCurrentGraphController.addZoomListener(listener);
		this.sumChargeInfoController.addZoomListener(listener);
	}
	
	public void setConnectionState(Boolean isConnected){
		
	}
	

	public ChartView getChartView() {
		return this.chartView;
	}
	
	public void createGraphs(ChargeObject chargeObject){
		this.sumEnergyGraphController= new XY_GraphController(chargeObject.getChargeData(), "Energy", "Energy [kWh]");
		this.sumVoltageGraphController= new XY_GraphController(chargeObject.getChargeData(), "Voltage", "Voltage [V]");
		this.sumCurrentGraphController= new XY_GraphController(chargeObject.getChargeData(), "Current", "Current [A]");
	}
	
	public void createNewGraphs(ChargeObject chargeObject){
		this.sumEnergyGraphController.newDataset(chargeObject.getChargeData());
		this.sumVoltageGraphController.newDataset(chargeObject.getChargeData());
		this.sumCurrentGraphController.newDataset(chargeObject.getChargeData());
	}
	
	private void updateGraphs(ChargeObject chargeObject){
		this.sumEnergyGraphController.updateInfo(chargeObject.getChargeData());
		this.sumVoltageGraphController.updateInfo(chargeObject.getChargeData());
		this.sumCurrentGraphController.updateInfo(chargeObject.getChargeData());
	}
	
	
	public void updateGUI(ChargeObject chargeObj){
		if (chargeObj.getChargeInfo().getSessionID()!= this.chargeObject.getChargeInfo().getSessionID()){
			//getData();
			createNewGraphs(chargeObj);
		}
		else {
			updateGraphs(chargeObj);
		}
		//System.out.println("CMC uüdate Epres: " + charge.getChargeData().getEpres());
		this.sumChargeInfoController.updateInfo(chargeObj.getChargeInfo());
		this.chartView.changeBackground(chargeObj.getChargeInfo().isCharging());
		//this.chargeInfoController.updateInfo(charge);
		
	}
	
	
	/*
	 * 	MouseAdapter to enlarge tiles of a summary-view or minimize it afterwards
	 */
		class BoxListener extends MouseAdapter{
			
			public void mouseClicked(MouseEvent me)
	    	{
				JPanel clickedBox =(JPanel)me.getSource(); // get the reference to the box that was clicked 
				RPanel sourcePanel= (RPanel)clickedBox.getParent();
				System.out.println("CMC: " + sourcePanel.getName());
				if (sourcePanel.isZoomed()){
					sourcePanel.setZoomed(false);
					chartView.setSummaryPanel(sumEnergyGraphController.getView(), sumVoltageGraphController.getView(), 
							sumCurrentGraphController.getView(), sumChargeInfoController.getView());
				}
				else {
					sourcePanel.setZoomed(true);
					chartView.setZoomedView(sourcePanel);
				}
				System.out.println("CMC: " + sourcePanel.isZoomed());
				//chartView.changeView((JPanel)clickedBox);
				//chartView.changeCurrentCard(clickedBox.getName());              
	       }
		}
}
