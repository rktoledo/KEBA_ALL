package dbStrings;

/**Klasse mit statischen Strings die so von der Datenbank verwendet
 * werden k�nnen.
 * 
 * <p> Die einzelnen Datenbankabfragen (Query) werden mit verweisen auf
 * diese statischen Strings zusammengesetzt. So wird sichergestellt, dass
 * eine Abfrage der Daten auch sicher gefunden wird.
 * 
 * <p> W�rden die Queries direkt als String geschrieben, so best�nde 
 * die Gefahr, dass durch ein Schreibfehler (z.B. Gross- / Kleinschreibung)
 * eine Abfrage abgebrochen wird weil der gesuche Wert von der Datenbank 
 * nicht gefunden werden kann.
 * 
 * @author Ron Peyer
 * @version 1.0
 * @since March 2018
 * 
 */
public class StringsForDB {
	
	/** Strings f�r die Datenbank und deren Tabellen. */
	public static String keba= "keba";
	public static String tableChargings= "Chargings";
	
	public static String userid = "userid";
	public static String userName = "username";
	public static String passwd = "passwd";
	
	public static String chargeID= "chargeID";
	public static String pluginDateTime= "plugin";
	public static String plugoutDateTime= "plugout";
	public static String chargeTime= "chargeTime";
	public static String plugTime= "plugTime";
	public static String energyCharged= "energyCharged";
	public static String chargeFilePath= "chargeFile";
	public static String statefilePath= "stateFile";
	public static String chargingComplete = "isComplete";
	public static String sessionID= "sessionID";
	public static String chargingUsable= "isUsable";
	public static String energyStart= "energyStart";
	public static String energyEnd= "energyEnd";
	public static String plugoutSetByReport= "endSetByKEBA";
}
