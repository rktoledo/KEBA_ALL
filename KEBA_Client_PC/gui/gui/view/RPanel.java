package gui.view;

import javax.swing.JPanel;

public class RPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private boolean isZoomed= false;

	public boolean isZoomed() {
		return isZoomed;
	}

	public void setZoomed(boolean isZoomed) {
		this.isZoomed = isZoomed;
	}
	
	
}
