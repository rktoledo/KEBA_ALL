package fileHandling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import kebaObjects.ChargeData;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fileHandlerInterface.FileHandlerInterface;


/**
 * Diese Klasse handhabt s�mtliche Datei-Verwaltungen auf dem PC
 * @author Ron Peyer
 * @version 1.0
 * @since March. 2018
 *
 */
public class FileHandler implements FileHandlerInterface{
		
	private static FileHandler instance = null;
	
	/**
	 * Singleton-Konstruktor des eine einmalige Instanz zur�ckliefert
	 * @return
	 */
	public static FileHandler getInstance() {
		if (instance == null) {
			instance = new FileHandler();
		}
		return instance;
	}
	
	public String getDir(String fileType) {
		return generateDir(fileType);
	}
	
	private String generateDir(String fileType){
		String directory= "/opt/keba/logs/"+ fileType;
		Boolean dirCreated= new File(directory).mkdir();
		if (dirCreated) {
			System.out.println("FILEHANDLER: dir= " + directory);
		}
		else {
			System.out.println("FH Not possible to create folder: " + directory);
		}
		return directory;
	}
	
	public void writeJsontoFile(JSONObject jsonObj, String filePath) {;
		try {
			FileWriter thisFile= new FileWriter(filePath);
			thisFile.write(jsonObj.toString());
			thisFile.flush();
			thisFile.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean overwriteFile(JSONObject obj, String filePath){
		File fold=new File(filePath);
		fold.delete();
		File fnew=new File(filePath);
		
		Boolean update= false;
		try {
		    FileWriter f2 = new FileWriter(fnew, false);
		    f2.write(obj.toString());
		    f2.flush();
		    f2.close();
		    update = true;
		} catch (IOException e) {
		    e.printStackTrace();
		} 
		return update;
	}
	

	public JSONObject getJsonFromFile(String file){
		
		JSONParser parser= new JSONParser();
		JSONObject jsonObj= null;
		
		try {
			FileReader fileReader= new FileReader(file);
			if (fileReader!= null){
				Object p = parser.parse(fileReader);
				if(p instanceof JSONObject){
				    //System.out.println("FH JSONObject");
				    jsonObj = (JSONObject)p;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			jsonObj= new JSONObject();
			System.out.println("FH New JSONObject created as file is empty!");
		}		
		return jsonObj;
	}
	
	public File getFile(String file){
		return new File(file);
	}
	
	/**
	 * Generiert eine Datei
	 * @param filename Name der zu erstellenden Datei
	 */
	public String generateFile(String fileName, String fileType){
		
		String filePath= "/home/linaro/keba/logs/" + fileType + "/" + fileName; //for Linux
		//String newFileName= "C:\\KEBAServer\\Logs\\" + fileType + "\\" + filename;		// for Windows
		System.out.println("FH Try to create file " + filePath); 
		FileWriter file2= null;
		try {
			file2= new FileWriter(filePath);
			file2.close();
			//System.out.println("genTempFile " + file2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	@Override
	public ChargeData getChargeFile(String filepath){
		JSONParser parser= new JSONParser();
		JSONObject jsonObj= null;
		try {
			jsonObj = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			//System.err.println("JSON ParseException: " + e.getCause());
			e.printStackTrace();
		}		
		return getObject(jsonObj);
	}

	@Override
	public JSONObject getStateFile(String filepath){
		JSONParser parser= new JSONParser();
		JSONObject jsonObj= null;
		try {
			jsonObj = (JSONObject) parser.parse(new FileReader(filepath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			//System.err.println("JSON ParseException: " + e.getCause());
			e.printStackTrace();
		}
		return jsonObj;
	}

	public ChargeData getChargeData(String filepath){
		JSONParser parser= new JSONParser();
		JSONObject jsonObj= null;
		
		if (filepath!=null){
			try {
				jsonObj = (JSONObject) parser.parse(new FileReader(filepath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				//System.err.println("JSON ParseException: " + e.getCause());
				e.printStackTrace();
			}
			ChargeData loading= new ChargeData(jsonObj);
			return loading;
		}
		else return null;
	}
	
	private ChargeData getObject(JSONObject jsonObj){
		return new ChargeData(jsonObj);
	}

}
