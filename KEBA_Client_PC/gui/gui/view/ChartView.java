package gui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class ChartView extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JPanel summaryPanel;
	private JPanel zoomedPanel;
	private CardLayout cardLayout;
	
	private JPanel energyGraph;
	private JPanel voltageGraph;
	private JPanel currentGraph;
	private JPanel infoView;
	
	private JPanel noConnectionPanel;
	
	private TitledBorder border;
	
	public ChartView(JPanel energyGraph, JPanel voltageGraph, JPanel currentGraph, JPanel infoView, boolean currentCharge){
		cardLayout= new CardLayout();
		this.setLayout(cardLayout);
		setBorder(currentCharge);
		
		summaryPanel= new JPanel();
		summaryPanel.setLayout(new GridLayout(2, 2));
		
		zoomedPanel= new JPanel();
		zoomedPanel.setLayout(new BorderLayout());
		
		this.energyGraph= energyGraph;
		this.voltageGraph= voltageGraph;
		this.currentGraph= currentGraph;
		this.infoView= infoView;
		
		this.noConnectionPanel= new JPanel(new BorderLayout());
		
		JLabel noConnectionLabel= new JLabel("No connection to Server");
		this.noConnectionPanel.add(noConnectionLabel, BorderLayout.CENTER);
		
		summaryPanel.add(this.energyGraph);
		summaryPanel.add(this.infoView);
		summaryPanel.add(this.voltageGraph);
		summaryPanel.add(this.currentGraph);
		
		this.add(summaryPanel, "Summary");
		this.add(zoomedPanel, "Zoomed");
		this.add(this.noConnectionPanel, "Start");
		
		this.cardLayout.show(this, "Summary");
	}
	
	public void setSummaryPanel(JPanel energyGraph, JPanel voltageGraph, JPanel currentGraph, JPanel infoView){
		this.energyGraph= energyGraph;
		this.voltageGraph= voltageGraph;
		this.currentGraph= currentGraph;
		this.infoView= infoView;
		
		summaryPanel.add(this.energyGraph);
		summaryPanel.add(this.infoView);
		summaryPanel.add(this.voltageGraph);
		summaryPanel.add(this.currentGraph);
		
		this.cardLayout.show(this, "Summary");
	}
	
	public void setZoomedView(JPanel panelToZoom){
		zoomedPanel.add(panelToZoom);
		this.cardLayout.show(this, "Zoomed");
	}
	
	public void setStartView(){
		this.cardLayout.show(this, "Start");
	}
	
	public void addNewGraphs(JPanel sumEnergyGraph, JPanel sumVoltageGraph, JPanel sumCurrentGraph, 
			JPanel energyGraph, JPanel voltageGraph, JPanel currentGraph){
		
		summaryPanel.remove(this.energyGraph);
		summaryPanel.remove(this.voltageGraph);
		summaryPanel.remove(this.currentGraph);
		
		this.energyGraph= sumEnergyGraph;
		this.voltageGraph= sumVoltageGraph;
		this.currentGraph= sumCurrentGraph;
		
		summaryPanel.add(this.energyGraph);
		summaryPanel.add(this.voltageGraph);
		summaryPanel.add(this.currentGraph);
		
		summaryPanel.revalidate();
		summaryPanel.repaint();
		
		this.revalidate();
		this.repaint();
	}
	
	private void setBorder(boolean currentCharge){
		if (currentCharge) border= new TitledBorder("Current Charge details");
		else border= new TitledBorder("Charge details");
    	
    	border.setTitleJustification(TitledBorder.CENTER);
	    border.setTitlePosition(TitledBorder.TOP);
	    border.setBorder(BorderFactory.createEtchedBorder());
	    this.setBorder(border);
    }
	
	public void changeBackground(boolean isCharging){
		if (isCharging){
			this.setBackground(Color.blue.brighter());
		}
		else {
			this.setBackground(UIManager.getColor("Panel.background"));
		}
		//this.revalidate();
		
	}
}
