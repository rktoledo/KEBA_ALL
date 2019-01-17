package gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import kebaObjects.ChargeInfo;


public class ChargeInfoView extends RPanel{
	private static final long serialVersionUID = 1L;
	
	private Font f = new Font( "Arial", Font.PLAIN, 15 );
	private Font f_it = new Font( "Arial", Font.ITALIC, 15 );
	
	private JLabel title1= new JLabel("Session ID:", SwingConstants.LEFT);
	private JLabel title2= new JLabel("Start of Charging:", SwingConstants.LEFT);
	private JLabel title3= new JLabel("End of Charging:", SwingConstants.LEFT);
	private JLabel title4= new JLabel("Time plugged:", SwingConstants.LEFT);
	private JLabel title5= new JLabel("Chargingtime:", SwingConstants.LEFT);
	private JLabel title6= new JLabel("Charged Energy:", SwingConstants.LEFT);
	private JLabel title7= new JLabel("Charge completed:", SwingConstants.LEFT);
	private JLabel title8= new JLabel("Plug state:", SwingConstants.LEFT);
	private JLabel title9= new JLabel("Charging:", SwingConstants.LEFT);
	
	
	private JLabel sessionID;
	private JLabel start;
	private JLabel end;
	private JLabel timePlugged;
	private JLabel timeCharged;
	private JLabel complete;
	private JLabel energy;
	private JLabel plugState;
	private JLabel chargeState;
	
	private String name;
	private JPanel view;

	private int gapHorizontal= 30;
	//private int gapVertical= 20;
	
	
	public ChargeInfoView(ChargeInfo chargeInfo){		
		name= "loadinginfo";
		this.setLayout(new BorderLayout());
		this.setName(name);
		
		TitledBorder border= new TitledBorder("Charge information");
		border.setTitleJustification(TitledBorder.CENTER);
	    border.setTitlePosition(TitledBorder.TOP);
	    border.setBorder(BorderFactory.createEtchedBorder());
	    this.setBorder(border);
		
		this.view= new JPanel();
		this.view.setName(name);
		this.view.setLayout(new GridBagLayout());
		
		DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		JPanel panel= new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		panel.setLayout(layout);
		
		this.sessionID= new JLabel(Integer.toString(chargeInfo.getSessionID()));
		//this.start= new JLabel(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(loading.getStartDateTime()));
		//this.start= new JLabel(formatter.format(loading.getStartDateTime().atZone(europeZoneID)));
		this.start= new JLabel(formatter.format(chargeInfo.getStartDateTime()));
		if (chargeInfo.isComplete()){
			this.end= new JLabel(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(chargeInfo.getEndDateTime()));
		}
		else {
			this.end= new JLabel("N/A");			
		}
		
		
		this.timePlugged= new JLabel(chargeInfo.toString(chargeInfo.getPluggedTime()));
		this.timePlugged.setMinimumSize(new JLabel("555 days, 55 hours, 55 minutes, 55     seconds ").getMinimumSize());
		this.timeCharged= new JLabel(chargeInfo.toString(chargeInfo.getChargeTime()/60) + " min");
		this.energy= new JLabel(Float.toString(((float)chargeInfo.getChargedEnergy())/10000) + "kWh");
		this.complete= new JLabel(chargeInfo.isCompleteString());
		this.plugState= new JLabel(chargeInfo.isPluggedString());
		this.chargeState= new JLabel(chargeInfo.isChargingString());
		
		
		this.sessionID.setFont(f_it);
		this.start.setFont(f_it);
		this.end.setFont(f_it);
		this.timePlugged.setFont(f_it);
		this.timeCharged.setFont(f_it);
		this.energy.setFont(f_it);
		this.complete.setFont(f_it);
		this.plugState.setFont(f_it);
		this.chargeState.setFont(f_it);
		
		this.sessionID.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.start.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.end.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.timePlugged.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.timeCharged.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.energy.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.complete.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.plugState.setBorder(new EmptyBorder(0, 0, 0, 5));
		this.chargeState.setBorder(new EmptyBorder(0, 0, 0, 5));
		
		
		this.title1.setFont(f);
		this.title2.setFont(f);
		this.title3.setFont(f);
		this.title4.setFont(f);
		this.title5.setFont(f);
		this.title6.setFont(f);
		this.title7.setFont(f);
		this.title8.setFont(f);
		this.title9.setFont(f);
		
		SequentialGroup verticalGroup=layout.createSequentialGroup();
        
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title1).addGap(gapHorizontal).addComponent(sessionID)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title2).addGap(gapHorizontal).addComponent(start)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title3).addGap(gapHorizontal).addComponent(end)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title4).addGap(gapHorizontal).addComponent(timePlugged)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title5).addGap(gapHorizontal).addComponent(timeCharged)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title6).addGap(gapHorizontal).addComponent(energy)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title7).addGap(gapHorizontal).addComponent(complete)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title8).addGap(gapHorizontal).addComponent(plugState)
                );
        verticalGroup.addGroup(layout.createParallelGroup()
                .addComponent(title9).addGap(gapHorizontal).addComponent(chargeState)
                );
        
        SequentialGroup horizontalGroup=layout.createSequentialGroup();
        
        /*
         * 	private JLabel sessionID;
			private JLabel start;
			private JLabel end;
			private JLabel timePlugged;
			private JLabel timeCharged;
			private JLabel energy;
			private JLabel complete;
			private JLabel plugState;
         */
        
        horizontalGroup.addGroup(layout.createParallelGroup()
        		.addComponent(title1)
        		.addComponent(title2)
        		.addComponent(title3)
        		.addComponent(title4)
        		.addComponent(title5)
        		.addComponent(title6)
        		.addComponent(title7)
        		.addComponent(title8)
        		.addComponent(title9));
        
        horizontalGroup.addGroup(layout.createParallelGroup()
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal)
        		.addGap(gapHorizontal));
        
        horizontalGroup.addGroup(layout.createParallelGroup()
        		.addComponent(sessionID)
        		.addComponent(start)
        		.addComponent(end)
        		.addComponent(timePlugged)
        		.addComponent(timeCharged)
        		.addComponent(energy)
        		.addComponent(complete)
        		.addComponent(plugState)
        		.addComponent(chargeState));
        
        layout.setHorizontalGroup(horizontalGroup);
        
        layout.setVerticalGroup(verticalGroup);
		
        this.view.add(panel);
        this.add(view, BorderLayout.CENTER);
	}
	
	public JPanel getView(){
		return this.view;
	}
	
	
	
	public void updateView(ChargeInfo chargeInfo){
		this.sessionID.setText(chargeInfo.getSessionIDString());
		this.start.setText(chargeInfo.getStartDateTimeString());
		this.energy.setText(chargeInfo.getChargedEnergyString());
		this.end.setText(chargeInfo.getEndDateTimeString());
		this.timePlugged.setText(chargeInfo.toString(chargeInfo.getPluggedTime()));
		this.timeCharged.setText(chargeInfo.toString(chargeInfo.getChargeTime()));
		this.complete.setText(chargeInfo.isCompleteString());
		this.plugState.setText(chargeInfo.isPluggedString());
		this.chargeState.setText(chargeInfo.isChargingString());
	}
}
