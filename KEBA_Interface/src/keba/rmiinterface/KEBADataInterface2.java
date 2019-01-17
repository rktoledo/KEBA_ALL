package keba.rmiinterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import kebaObjects.ChargeInfo;
import kebaObjects.ChargeObject;


public interface KEBADataInterface2 extends Remote{
	
	public static final String SERVICE_NAME = "KEBADataInterface2";
	
	ChargeInfo getChargeInfo(int chargeID) throws RemoteException;
	ArrayList<ChargeInfo> getAllChargings() throws RemoteException;
	ArrayList<ChargeInfo> getChargingsFrom(LocalDateTime chargesFrom) throws RemoteException;
	ArrayList<ChargeInfo> getChargingsTo(LocalDateTime chargesTo) throws RemoteException;
	ArrayList<ChargeInfo> getChargingsFromTo(LocalDateTime chargesFrom, LocalDateTime chargesTo) throws RemoteException;
	ChargeInfo getCharge(int chargeID) throws RemoteException;
	ChargeInfo getActualCharge() throws RemoteException;
	
	ChargeObject getChargeObject(int chargeID) throws RemoteException;
	ChargeObject getLatestChargeObject() throws RemoteException;
}
