package kebaserver.main;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import dbDAOInterface.KebaDbDAO;

import fileHandling.FileHandler;

import keba.rmiinterface.KEBADataInterface2;
import kebaObjects.ChargeData;
import kebaObjects.ChargeInfo;
import kebaObjects.ChargeObject;

public class KEBAData implements KEBADataInterface2{
	
	private KebaDbDAO db;
	private FileHandler fileHandler;
	private KEBAServer kebaServer;
	
	public KEBAData(KebaDbDAO db, FileHandler fileHandler, KEBAServer kebaServer){
		this.db= db;
		this.fileHandler= fileHandler;
		this.kebaServer= kebaServer;
	}

	@Override
	public ChargeInfo getChargeInfo(int chargeID) throws RemoteException {
		return db.getCharge(chargeID);
	}

	@Override
	public ArrayList<ChargeInfo> getAllChargings() throws RemoteException {
		return db.getAllChargings();
	}

	@Override
	public ArrayList<ChargeInfo> getChargingsFrom(LocalDateTime chargesFrom)
			throws RemoteException {
		return db.getChargingsFrom(chargesFrom);
	}

	@Override
	public ArrayList<ChargeInfo> getChargingsTo(LocalDateTime chargesTo)
			throws RemoteException {
		return db.getChargingsTo(chargesTo);
	}

	@Override
	public ArrayList<ChargeInfo> getChargingsFromTo(LocalDateTime chargesFrom,
			LocalDateTime chargesTo) throws RemoteException {
		return db.getChargingsFromTo(chargesFrom, chargesTo);
	}

	@Override
	public ChargeInfo getCharge(int chargeID) throws RemoteException {
		return db.getCharge(chargeID);
	}

	@Override
	public ChargeInfo getActualCharge() throws RemoteException {
		return db.getLatestCharge();
	}

	@Override
	public ChargeObject getChargeObject(int chargeID) throws RemoteException {
		ChargeInfo info= db.getCharge(chargeID);
		ChargeData data= fileHandler.getChargeData(info.getChargeFilePath());
		ChargeObject obj= new ChargeObject(info, data);
		return obj;
	}

	@Override
	public ChargeObject getLatestChargeObject() throws RemoteException {
		ChargeObject obj;
		if (kebaServer.isChargeOngoing()){
			obj= kebaServer.getChargeObject();
		}
		else {
			ChargeInfo info= db.getLatestCharge();
			ChargeData data= fileHandler.getChargeData(info.getChargeFilePath());
			obj= new ChargeObject(info, data);
		}
		return obj;
	}
}
