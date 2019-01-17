package gui.controller;

import gui.view.ChartView;
import gui.view.RPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

import javax.swing.JPanel;

import keba.rmiinterface.KEBADataInterface2;
import kebaObjects.ChargeInfo;
import kebaObjects.ChargeObject;

public class ChartController {

	//private KEBADataInterface2 serverData;
	
	private ChartView chartView;
	
	private XY_GraphController energyGraphController;
	private XY_GraphController voltageGraphController;
	private XY_GraphController currentGraphController;
	private ChargeInfoController chargeInfoController;
	
	//private ChargeInfo chargeInfo= null;
	
	//private ChargeObject chargeObject;

	public ChartController(ChargeObject chargeObject, boolean currentCharge){
		createGraphs(chargeObject);
		
		this.chargeInfoController = new ChargeInfoController(chargeObject.getChargeInfo());
		
		this.chartView= new ChartView(energyGraphController.getView(), voltageGraphController.getView(), 
				currentGraphController.getView(), chargeInfoController.getView(), currentCharge);
		
		addMouseListener(new BoxListener());
	}
	
	public void addMouseListener(BoxListener listener){
		this.energyGraphController.addZoomListener(listener);
		this.voltageGraphController.addZoomListener(listener);
		this.currentGraphController.addZoomListener(listener);
		this.chargeInfoController.addZoomListener(listener);
	}
	public ChartView getChartView() {
		return this.chartView;
	}
	
	public void createGraphs(ChargeObject chargeObject){
		this.energyGraphController= new XY_GraphController(chargeObject.getChargeData(), "Energy", "Energy [kWh]");
		this.voltageGraphController= new XY_GraphController(chargeObject.getChargeData(), "Voltage", "Voltage [V]");
		this.currentGraphController= new XY_GraphController(chargeObject.getChargeData(), "Current", "Current [A]");
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
					chartView.setSummaryPanel(energyGraphController.getView(), voltageGraphController.getView(), 
							currentGraphController.getView(), chargeInfoController.getView());
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
