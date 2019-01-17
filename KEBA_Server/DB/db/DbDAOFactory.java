package db;

import dbDAOInterface.KebaDbDAO;
import dbMySQL.MySqlDAOFactory;

/**
 * Interface für die Datenbank "KEBAServer WALLBOX"
 * @author Ron Peyer
 * @version 1.0
 * @since March 2018
 *
 */
public abstract class DbDAOFactory {
	
    /** Abstract method for the CreateDbDAO. */
    public abstract KebaDbDAO getKEBADAO(); 
    /** Abstract method for the DataDAO. */
    
    public static DbDAOFactory getDbDAOFactory (String type){ 
        if (type.equalsIgnoreCase("mysql")){
            return new MySqlDAOFactory();
        }else{
            return null;
        }
    }
}
