package fileHandlerInterface;

import kebaObjects.ChargeData;
import org.json.simple.JSONObject;

public interface FileHandlerInterface {
	ChargeData getChargeFile(String filepath);	
	JSONObject getStateFile(String filepath);
}
