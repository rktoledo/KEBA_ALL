package gui.controller;

import java.awt.event.MouseListener;

import javax.swing.JPanel;

import kebaObjects.ChargeData;

import gui.view.XY_Graph;

public class XY_GraphController {
	
	private XY_Graph view;
	private ChargeData loading;
	private String type;
	
	public XY_GraphController(ChargeData loading, String type, String unit){
		this.loading= loading;
		this.type= type;
		this.view= new XY_Graph(this.loading, this.type, unit);
	}
	
	public JPanel getView(){
		return view;
	}
	
	public void addZoomListener(MouseListener listener){
		this.view.getGraph().addMouseListener(listener);
	}
	
	public void updateInfo(ChargeData loading){
		this.loading= loading;
		this.view.updateGraph(this.loading);
	}
	
	public JPanel getChart(){
		return view.getGraph();
	}
	
	public void newDataset(ChargeData loading){
		this.loading= loading;
		view.newGraph(this.loading);
	}
}
