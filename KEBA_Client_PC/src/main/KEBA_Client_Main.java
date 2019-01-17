package main;

import java.rmi.RemoteException;

import gui.controller.MainController;

public class KEBA_Client_Main{

	public static void main(String[] args) {
		MainController controller;
		try {
			controller = new MainController();
			controller.showView();
			//controller.addLoadings();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
