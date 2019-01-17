package gui.view;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import kebaObjects.ChargeInfo;


public class ChargeTableView extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private DefaultTableModel tableModel;
    private JTable table;
    
    public ChargeTableView(){
    	initForm();
    }
    
    private void initForm(){
    	String[] columns = new String[] {
                "Charge ID", "Session ID", "Start", "End", "Time [s]",
                "Energy", "Usable", "Complete", "Charge file", "State file"
            };
            
            tableModel = new DefaultTableModel(columns, 0);
            
            table = new JTable(tableModel);
            
            JScrollPane scrollFrame = new JScrollPane(table);
            table.setAutoscrolls(true);
            //scrollFrame.setPreferredSize(new Dimension( 800,300));
            this.setLayout(new GridLayout(0,1));
            this.add(scrollFrame);
    }
    
    public void add(ArrayList<ChargeInfo> chargeInfos){
    	
    	for (int i = 0; i < chargeInfos.size(); i++){
    		   int chargeiID = chargeInfos.get(i).getChargeID();
    		   int sessionID = chargeInfos.get(i).getSessionID();
    		   String start = chargeInfos.get(i).getStartDateTime().toString();
    		   String end = "";
    		   if (chargeInfos.get(i).getEndDateTime()!= null){
    			   end = chargeInfos.get(i).getEndDateTime().toString();
    		   }
    		   int time = chargeInfos.get(i).getChargeTime();
    		   int energy = chargeInfos.get(i).getChargedEnergy();
    		   String usable;
    		   if (chargeInfos.get(i).isUsable()){
    			   usable= "Yes";
    		   }
    		   else {
    			   usable= "NO";
    		   }
    		   String complete;
    		   if (chargeInfos.get(i).isComplete()){
    			   complete= "Yes";
    		   }
    		   else {
    			   complete= "NO";
    		   }
    		   
    		   String chargeFilePath;
    		   if (chargeInfos.get(i).isChargeFileAvailable()){
    			   chargeFilePath= chargeInfos.get(i).getChargeFilePath();
    		   }
    		   else {
    			   chargeFilePath= "N/A";
    		   }
    		   
    		   String stateFilePath;
    		   if (chargeInfos.get(i).isStateFileAvailable()){
    			   stateFilePath= chargeInfos.get(i).getStateFilePath();
    		   }
    		   else {
    			   stateFilePath= "N/A";
    		   }
    		   
    		   Object[] data = {chargeiID, sessionID, start, end, time, energy, 
    		                               usable, complete, chargeFilePath, stateFilePath};

    		   tableModel.addRow(data);

    		}
    }
    
    public void add(ChargeInfo chargeing){
    	
    	int chargeiID = chargeing.getChargeID();
    	int sessionID = chargeing.getSessionID();
    	String start = chargeing.getStartDateTime().toString();
    	String end = chargeing.getEndDateTime().toString();
    	int time = chargeing.getChargeTime();
    	int energy = chargeing.getChargedEnergy();
    	String usable;
    	if (chargeing.isUsable()){
    		usable= "Yes";
    	
    	}
    	else {
    		usable= "NO";
    	}
    	String complete;
    	if (chargeing.isComplete()){
    		complete= "Yes";
    	}
    	else {
    		complete= "NO";
    	}
    	
    	String chargeFilePath;
    	if (chargeing.isChargeFileAvailable()){
    		chargeFilePath= chargeing.getChargeFilePath();
    	}
    	else {
    		chargeFilePath= "N/A";
    	}
    	
    	String stateFilePath;
    	if (chargeing.isStateFileAvailable()){
    		stateFilePath= chargeing.getStateFilePath();
    	}
    	else {
    		stateFilePath= "N/A";
    	}
    	
    	Object[] data = {chargeiID, sessionID, start, end, time, energy, 
    			usable, complete, chargeFilePath, stateFilePath};
    	
    	tableModel.addRow(data);
    	
    }
    
    
}
