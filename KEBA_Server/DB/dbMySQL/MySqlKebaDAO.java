package dbMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import kebaObjects.ChargeInfo;
import dbDAOInterface.KebaDbDAO;
import dbStrings.StringsForDB;

@SuppressWarnings("static-access")
public class MySqlKebaDAO implements KebaDbDAO{
    
    private static StringsForDB sdb;
    
    private Connection conn = null;
    
    /** The String for Database creation. */
    private static final String createDB = "CREATE DATABASE IF NOT EXISTS " + sdb.keba;
    
    
    /** The Strings for Table Logins creation. */
    // Logins-Table
    private static final String createTableChargings = "CREATE TABLE IF NOT EXISTS " + sdb.tableChargings + " ("
			+ sdb.chargeID + " INT NOT NULL AUTO_INCREMENT, "
			+ sdb.sessionID + " INT DEFAULT 0, "
			+ sdb.pluginDateTime + " TIMESTAMP DEFAULT now(), "
			+ sdb.plugoutDateTime + " TIMESTAMP DEFAULT now(), "
			+ sdb.plugoutSetByReport + " BOOLEAN DEFAULT FALSE, "
			+ sdb.plugTime + " INT DEFAULT NULL, "
			+ sdb.chargeTime + " INT DEFAULT NULL, "
			+ sdb.energyStart + " INT DEFAULT NULL, "
			+ sdb.energyEnd + " INT DEFAULT NULL, "
			+ sdb.energyCharged + " INT DEFAULT NULL, "
			+ sdb.chargeFilePath + " VARCHAR(100) DEFAULT NULL, "
			+ sdb.statefilePath + " VARCHAR(100) DEFAULT NULL, "
			+ sdb.chargingComplete + " BOOLEAN DEFAULT TRUE, "
			
			+ sdb.chargingUsable + " BOOLEAN DEFAULT TRUE, "
			
			+ "PRIMARY KEY (" + sdb.chargeID + "));";
	
       
    public MySqlKebaDAO(){
    	//System.out.println("In constructor of MySQLKEBADAO");
    	//Class.forName("com.mysql.cj.jdbc.Driver");
		//conn = DriverManager.getConnection(DBURL, USER, PASS);
    	boolean res= createDB();
    	//System.ouMySqlKebaDAOB res= " + res);
        //if (res){
        createTable(createTableChargings);
     	   //System.out.println("c1 " + createTable(createTableChargings, sdb.tableChargings));
        
    }
    
    public boolean createDB(){
		boolean res= false;
    	
        try {
        	conn = MySqlDAOFactory.createConnection();
        	PreparedStatement stmt= conn.prepareStatement(createDB);
        	res=  stmt.execute();
            //System.out.println("t2" + s.executeUpdate("show databases like 'keba'"));
        	stmt.close();
            conn.close();
        } catch (SQLException e) {
        	res= false;
        	System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
        }		
        return res;
	}
	
	/** Returns:
	 *  false when table exists already,
	 *  true when table was created new. 
	 */
    /*public boolean createTable(String command, String tablename){
    	boolean res;
    	//System.out.println("MYSQLCREATEDB: command= " + command);
    	//System.out.println("MYSQLCREATEDB: tableName= " + tablename);
        PreparedStatement preparedStatement = null;
        try {
            conn = MySqlDAOFactory.createConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tablename, null);
            if (tables.next()){
            	// Table exists. No action required.
            	res= false;
            }
            else {
            	preparedStatement = conn.prepareStatement(command);
            	//System.out.println("MYSQLCREATEDB: preparedStatement= " + preparedStatement);
            	preparedStatement.execute();
                res= true;
            }
            conn.close();
        } catch (SQLException e) {
        	res= false;
            //System.out.println("e3 " + e.getMessage());
        } finally {
            /*try {
                conn.close();
            } catch (Exception cse) {
            	//System.out.println("e5 " + cse.getMessage());
            }
        }
        return res;
    }*/
    
    @Override
    public boolean createTable(String query){
    	boolean res= false;
		
        try {
        	conn = MySqlDAOFactory.createConnection();
        	PreparedStatement stmt= conn.prepareStatement(query);
        	res=  stmt.execute();
            //System.out.println("t2" + s.executeUpdate("show databases like 'keba'"));
        	stmt.close();
            conn.close();
        } catch (SQLException e) {
        	res= false;
        	System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
        }		
        return res;
    }
    
    @Override
    public int getChargeID(int sessionID){
    	int chargeID= 0;
    	String query= "SELECT " + sdb.chargeID + 
    			" FROM " + sdb.tableChargings + " WHERE " + sdb.sessionID + "= ?";
    	
		try {
			conn = MySqlDAOFactory.createConnection();
	    	PreparedStatement stmt = conn.prepareStatement(query);
	    	stmt.setInt(1, sessionID);
			//System.out.println("Query= " + stmt);
			
			ResultSet rs = stmt.executeQuery();
			if (rs.next()){
				chargeID= rs.getInt(sdb.chargeID);
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
    	return chargeID;
    }
    
    @Override
    public int getLatestChargeID(){
    	int actualCharge= 0;
    	String query= "SELECT * FROM " + sdb.tableChargings + " WHERE " + sdb.chargingComplete + "= FALSE";
    	
		try {
			conn = MySqlDAOFactory.createConnection();
	    	PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			//System.out.println("Connection= " + conn.toString());
			//System.out.println("Statement= " + preparedStmt.toString());
			
			ResultSet rs= stmt.executeQuery();
			if (rs.next()){
				rs.last();
				actualCharge= rs.getInt(sdb.sessionID);
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
    	return actualCharge;
    }

	

	
	@Override
	public ChargeInfo createCharge(LocalDateTime ldtStart, int sessionID) {
		//System.out.println("createCharging");
		String query = " insert into " 
				+ sdb.tableChargings + " (" 
				+ sdb.sessionID + ", "
				+ sdb.pluginDateTime + ") "
				//+ sdb.chargeEnddate + ", "
				//+ sdb.chargeTime + ", "
				//+ sdb.chargeFilePath + ", "
				//+ sdb.statefilePath + ")"
				//+ " values (?, ?, ?, ?, ?)";
				+ " values (?, ?)";
		
		ChargeInfo charge= new ChargeInfo();
		charge.setStartDateTime(ldtStart);
		charge.setSessionID(sessionID);
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(ldtStart);
			stmt.setInt(1, sessionID);
			stmt.setTimestamp(2, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.executeUpdate();
			ResultSet rs =stmt.getGeneratedKeys();
			rs.next();
			charge.setChargeID(rs.getInt(1));
			//System.out.println("ID of entry= " + currentChargingID);
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return charge;
	}
	
	

	/*@Override
	public LocalDateTime getChargingStart(int chargeID) {
		String query = " SELECT " + sdb.chargeStartDate + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + 
				"= " + chargeID;
		
		LocalDateTime dateTime= null;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = preparedStmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				do { 
					java.sql.Timestamp sqlDateTime= rs.getTimestamp(sdb.chargeStartDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
					if (sqlDateTime != null){
						dateTime= sqlDateTime.toLocalDateTime();
					}
				} 
				while (rs.next()); 
			}
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateTime;
	}*/

	@Override
	public void setChargeEnd(int chargeID, LocalDateTime ldtEnd) {
		String query = " UPDATE " + sdb.tableChargings + 
				" set " + sdb.chargeTime + "= ?" +
				" WHERE " + sdb.chargeID + "= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(ldtEnd);
			stmt.setTimestamp(2, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
	}


	@Override
	public void setChargeTime(int chargeID, int chargeTime) {
		String query = " UPDATE " + sdb.tableChargings + 
				" set " + sdb.chargeTime + "= ?" +
				" WHERE " + sdb.chargeID + "= ?";
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeTime);
			stmt.setInt(2, chargeID);
			
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	@Override
	public void setChargedEnergy(int chargeID, int chargedEnergy) {
		String query = " UPDATE " + sdb.tableChargings + 
				" set " + sdb.energyCharged + "= ?" +
				" WHERE " + sdb.chargeID + "= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargedEnergy);
			stmt.setInt(2, chargeID);
			
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}

	@Override
	public void setChargeFilePath(int chargeID, String chargeFilePath) {
		String query = " UPDATE " + sdb.tableChargings + 
				" set " + sdb.chargeFilePath + "= ?" +
				" WHERE " + sdb.chargeID + "= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, chargeFilePath);
			stmt.setInt(2, chargeID);
			
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}
	
	

	@Override
	public void setStateFilePath(int chargeID, String stateFilePath) {
		String query = " UPDATE " + sdb.tableChargings + 
				" set " + sdb.statefilePath + "= ?" +
				" WHERE " + sdb.chargeID + "= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, stateFilePath);
			stmt.setInt(2, chargeID);
			
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}
	
	@Override
	public void terminateCharge(int chargeID, LocalDateTime chargeEnd, int chargeTime, String chargeFilePath, 
			String stateFilePath, int chargedEnergy) {
		//System.out.println("terminateCharging");
		String query = " update " 
				+ sdb.tableChargings + " set " 
				+ sdb.plugoutDateTime + "=? , "
				+ sdb.chargeTime + "=? , "
				+ sdb.chargeFilePath + "=? , "
				+ sdb.chargingComplete + "=? , "
				+ sdb.energyCharged + "=? , "
				+ sdb.statefilePath + "=? "
				+ " WHERE " +  sdb.chargeID + "= ?";

		// create the mysql insert preparedstatement
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(chargeEnd);
			stmt.setTimestamp(1, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			stmt.setInt(2, chargeTime);
			stmt.setString(3, chargeFilePath);
			stmt.setBoolean(4, true);
			stmt.setInt(5, chargedEnergy);
			stmt.setString(6, stateFilePath);
			stmt.setInt(7, chargeID);
		

			// execute the preparedstatement
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}
	
	@Override
	public void terminateCharge(ChargeInfo chargeInfo) {
		//System.out.println("terminateCharging");
		String query = " update " 
				+ sdb.tableChargings + " set " 
				+ sdb.plugoutDateTime + "=? , "
				+ sdb.chargeTime + "=? , "
				+ sdb.chargeFilePath + "=? , "
				+ sdb.chargingComplete + "=? , "
				+ sdb.energyCharged + "=? , "
				+ sdb.statefilePath + "=? , "
				+ sdb.plugTime + "=? , "
				+ sdb.energyStart + "=? , "
				+ sdb.energyEnd + "=? "
				+ " WHERE " +  sdb.chargeID + "= ?";

		// create the mysql insert preparedstatement
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(chargeInfo.getEndDateTime());
			stmt.setTimestamp(1, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			stmt.setInt(2, chargeInfo.getChargeTime());
			stmt.setString(3, chargeInfo.getChargeFilePath());
			stmt.setBoolean(4, true);
			stmt.setInt(5, chargeInfo.getChargedEnergy());
			stmt.setString(6, chargeInfo.getStateFilePath());
			stmt.setInt(7, chargeInfo.getPluggedTime());
			stmt.setInt(8, chargeInfo.getEnergyStart());
			stmt.setInt(9, chargeInfo.getEnergyEnd());
			stmt.setInt(10, chargeInfo.getChargeID());
		

			// execute the preparedstatement
			System.out.println("Statement= " + stmt);
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateCharge(ChargeInfo chargeInfo) {
		//System.out.println("terminateCharging");
		String query = " update " 
				+ sdb.tableChargings + " set " 
				+ sdb.plugoutDateTime + "=? , "
				+ sdb.chargeTime + "=? , "
				+ sdb.chargeFilePath + "=? , "
				+ sdb.chargingComplete + "=? , "
				+ sdb.energyCharged + "=? , "
				+ sdb.statefilePath + "=? "
				+ sdb.plugTime + "=? "
				+ " WHERE " +  sdb.chargeID + "= ?";

		// create the mysql insert preparedstatement
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(chargeInfo.getEndDateTime());
			stmt.setTimestamp(1, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			stmt.setInt(2, chargeInfo.getChargeTime());
			stmt.setString(3, chargeInfo.getChargeFilePath());
			stmt.setBoolean(4, false);
			stmt.setInt(5, chargeInfo.getChargedEnergy());
			stmt.setString(6, chargeInfo.getStateFilePath());
			stmt.setInt(7, chargeInfo.getPluggedTime());
			stmt.setInt(8, chargeInfo.getEnergyStart());
			stmt.setInt(9, chargeInfo.getEnergyEnd());
			stmt.setInt(10, chargeInfo.getChargeID());
		

			// execute the preparedstatement
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			//System.out.println("ID of entry= " + currentChargingID);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}
	
	@Override
	public void setSessionID(int chargeID, int sessionID) {
		String query = " UPDATE " + sdb.tableChargings + 
				" set " + sdb.sessionID + "= ?" +
				" WHERE " + sdb.chargeID + "= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, sessionID);
			stmt.setInt(2, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}
	
	@Override
	public void setChargeEndbyReport(int chargeID, boolean isEndSetByReport) {
		String query = " UPDATE " + sdb.tableChargings + 
				" set " + sdb.plugoutSetByReport + "= ?" +
				" WHERE " + sdb.chargeID + "= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setBoolean(1, isEndSetByReport);
			stmt.setInt(2, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			stmt.execute();
			
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + query);
			e.printStackTrace();
		}
	}


	

	
	
	
	
	/*
	 * GET Methods
	 * ----------------------------------------------------------------------
	 * @see dbDAOInterface.KebaDbDAO#getStateFilePath(int)
	 */

	@Override
	public String getStateFilePath(int chargeID) {
		String query = " SELECT " + sdb.statefilePath + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		String filePath= null;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				filePath= rs.getString(sdb.statefilePath);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return filePath;
	}
	
	@Override
	public String getStateFilePathbySessionID(int sessionID) {
		String query = " SELECT " + sdb.statefilePath + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.sessionID + "= ?";
		
		String filePath= null;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, sessionID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				filePath= rs.getString(sdb.statefilePath);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return filePath;
	}

	

	@Override
	public boolean checkIfSessionIDExists(int sessionID) {
		String query = " SELECT * FROM " + 
				sdb.tableChargings + 
				" WHERE " + sdb.sessionID  + "= ?";
		
		System.out.println(query);
		
		boolean exists= false;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, sessionID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				exists= false;
			} else { 
				exists= true;
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return exists;
	}

	

	@Override
	public int getSessionID(int chargeID) {
		String query = " SELECT " + sdb.sessionID + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		int val= 0;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				val= rs.getInt(sdb.sessionID);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return val;
	}

	@Override
	public int getChargedEnergy(int chargeID) {
		String query = " SELECT " + sdb.energyCharged + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		int val= 0;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				val= rs.getInt(sdb.energyCharged);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return val;
	}

	@Override
	public String getChargeFilePath(int chargeID) {
		String query = " SELECT " + sdb.chargeFilePath + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		String res= null;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				res= rs.getString(sdb.chargeFilePath);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public String getChargeFilePathbySessionID(int sessionID) {
		String query = " SELECT " + sdb.chargeFilePath + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.sessionID + "= ?";
		
		String res= null;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, sessionID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				res= rs.getString(sdb.chargeFilePath);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public LocalDateTime getChargeStart(int chargeID) {
		String query = " SELECT " + sdb.pluginDateTime + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		LocalDateTime ldt= null;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				java.sql.Timestamp sqlDateTime= rs.getTimestamp(sdb.pluginDateTime, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
				if (sqlDateTime != null){
					ldt= sqlDateTime.toLocalDateTime();
				}
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return ldt;
	}

	@Override
	public LocalDateTime getChargeEnd(int chargeID) {
		String query = " SELECT " + sdb.plugoutDateTime + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		LocalDateTime ldt= null;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				java.sql.Timestamp sqlDateTime= rs.getTimestamp(sdb.plugoutDateTime, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
				if (sqlDateTime != null){
					ldt= sqlDateTime.toLocalDateTime();
				}
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return ldt;
	}

	@Override
	public int getChargeTime(int chargeID) {
		String query = " SELECT " + sdb.chargeTime + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		int val= 0;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				val= rs.getInt(sdb.chargeTime);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return val;
	}

	@Override
	public int getLatestSessionID() {
		String query = " SELECT " + sdb.sessionID + 
				" FROM " + sdb.tableChargings + 
				" ORDER BY " + sdb.sessionID + " DESC LIMIT 1";
		
		int val= 0;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				val= rs.getInt(sdb.sessionID);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return val;
	}

	

	@Override
	public int chargeIDofSessionID(int sessionID) {
		String query = " SELECT " + sdb.chargeID + 
				" FROM " + sdb.tableChargings + 
				" WHERE " + sdb.sessionID + "= ?" +
				" ORDER BY " + sdb.sessionID + " DESC LIMIT 1";
		
		int val= 0;
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, sessionID);
			//System.out.println("Statement= " + preparedStmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next() == false) { 
				//System.out.println("ResultSet in empty in Java"); 
			} else { 
				rs.last();
				val= rs.getInt(sdb.chargeID);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		return val;
	}
	
	
	
	/*
	 * Get ChargeInfo Queries
	 * --------------------------------
	 * 
	 * @see dbDAOInterface.KebaDbDAO#getCharge(int)
	 */
	
	@Override
	public ChargeInfo getCharge(int chargeID){
		ChargeInfo data= new ChargeInfo();
    	
		String query= "SELECT *  FROM " + sdb.tableChargings + 
				" WHERE " + sdb.chargeID + "= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, chargeID);
			//System.out.println("Query= " + query);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()){
				data= getChargeInfoFromResultSet(rs);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
    	return data;
	}
	
	@Override
	public ChargeInfo getLatestCharge(){
		ChargeInfo data= new ChargeInfo();
    	
		//String query= "SELECT * FROM " + sdb.tableChargings + " where " + sdb.sessionID + "= 138 ORDER BY " + sdb.sessionID + " DESC LIMIT 1";
		String query= "SELECT *  FROM " + sdb.tableChargings + 
				" ORDER BY " + sdb.sessionID + " DESC LIMIT 1";
		
		//SELECT * FROM Table ORDER BY ID DESC LIMIT 1

		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			//System.out.println("Query= " + query);
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()){
				rs.last();
				data= getChargeInfoFromResultSet(rs);
			}
			
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		//System.out.println("DB: Return ChargeInfo for SessionID: " + data.getSessionID());
    	return data;
	}
	
	@Override
	public ArrayList<ChargeInfo> getAllChargings(){
		//System.out.println("DB: GetAllChargings called!");
		
		ArrayList<ChargeInfo> datas= new ArrayList<ChargeInfo>();
		ChargeInfo data;
    	
		String query= "SELECT * FROM " + sdb.tableChargings;
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			//System.out.println("Statement= " + stmt.toString());
			
			 ResultSet rs = stmt.executeQuery();
			 
			 while (rs.next()){
				 data= getChargeInfoFromResultSet(rs);
				 datas.add(data);
			 }
			 
			 rs.close();
			 stmt.close();
			 //System.out.println("DB: GetAllChargings rs size= " + rs.getRow());
			 conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		
		//System.out.println("DB: GetAllChargings size of return Array= " + datas.size());
		return datas;
	}

	
	

	

	@Override
	public ArrayList<ChargeInfo> getChargingsFrom(LocalDateTime chargeStartRange) {
		ArrayList<ChargeInfo> datas= new ArrayList<ChargeInfo>();
		ChargeInfo data;
    	
		String query= "SELECT *  FROM " + sdb.tableChargings + 
				" WHERE " + sdb.pluginDateTime + ">= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(chargeStartRange);
			stmt.setTimestamp(1, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			//System.out.println("Statement= " + stmt.toString());
			
			 ResultSet rs = stmt.executeQuery();
			 
			 while (rs.next()){
				 data= getChargeInfoFromResultSet(rs);
				 datas.add(data);
			 }
			 
			 rs.close();
			 stmt.close();
			 //System.out.println("DB: GetAllChargings rs size= " + rs.getRow());
			 conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		
		//System.out.println("DB: GetAllChargings size of return Array= " + datas.size());
		return datas;
	}

	@Override
	public ArrayList<ChargeInfo> getChargingsTo(LocalDateTime chargeEndRange) {
		ArrayList<ChargeInfo> datas= new ArrayList<ChargeInfo>();
		ChargeInfo data;
    	
		String query= "SELECT *  FROM " + sdb.tableChargings + 
				" WHERE " + sdb.pluginDateTime + ">= ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(chargeEndRange);
			stmt.setTimestamp(1, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			//System.out.println("Statement= " + stmt.toString());
			
			 ResultSet rs = stmt.executeQuery();
			 
			 while (rs.next()){
				 data= getChargeInfoFromResultSet(rs);
				 datas.add(data);
			 }
			 
			 rs.close();
			 stmt.close();
			 //System.out.println("DB: GetAllChargings rs size= " + rs.getRow());
			 conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		
		//System.out.println("DB: GetAllChargings size of return Array= " + datas.size());
		return datas;
	}

	@Override
	public ArrayList<ChargeInfo> getChargingsFromTo(LocalDateTime chargeStartRange,
			LocalDateTime chargeEndRange) {
		ArrayList<ChargeInfo> datas= new ArrayList<ChargeInfo>();
		ChargeInfo data;
    	
		String query= "SELECT *  FROM " + sdb.tableChargings + 
				" WHERE " + sdb.pluginDateTime + " BETWEEN ? " + 
				" AND ?";
		
		try {
			conn = MySqlDAOFactory.createConnection();
			PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(chargeStartRange);
			stmt.setTimestamp(1, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			
			sqlDate = java.sql.Timestamp.valueOf(chargeEndRange);
			stmt.setTimestamp(2, sqlDate, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
			//System.out.println("Statement= " + stmt.toString());
			
			 ResultSet rs = stmt.executeQuery();
			 
			 while (rs.next()){
				 data= getChargeInfoFromResultSet(rs);
				 datas.add(data);
			 }
			 
			 rs.close();
			 stmt.close();
			 //System.out.println("DB: GetAllChargings rs size= " + rs.getRow());
			 conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MSQL DB: stmt= "  + createDB);
			e.printStackTrace();
		}
		
		//System.out.println("DB: GetAllChargings size of return Array= " + datas.size());
		return datas;
	}    
	
	
	
	
	
	
	
	
	private ChargeInfo getChargeInfoFromResultSet(ResultSet rs) throws SQLException{
		ChargeInfo data= new ChargeInfo();
		
		data.setChargeID(rs.getInt(sdb.chargeID));
		data.setSessionID(rs.getInt(sdb.sessionID));
		data.setChargeTime(rs.getInt(sdb.chargeTime));
		data.setChargedEnergy(rs.getInt(sdb.energyCharged));
		data.setComplete(rs.getBoolean(sdb.chargingComplete));
		data.setUsable(rs.getBoolean(sdb.chargingUsable));
		data.setPluggedTime(rs.getInt(sdb.plugTime));
		data.setEnergyStart(rs.getInt(sdb.energyStart));
		data.setEnergyEnd(rs.getInt(sdb.energyEnd));
		data.setEndSetByReport(rs.getBoolean(sdb.plugoutSetByReport));
		
			 
		java.sql.Timestamp sqlDateTime= rs.getTimestamp(sdb.pluginDateTime, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
		if (sqlDateTime != null){
			data.setStartDateTime(sqlDateTime.toLocalDateTime());
		}
			 	
		sqlDateTime= rs.getTimestamp(sdb.plugoutDateTime, Calendar.getInstance(TimeZone.getTimeZone("Europe/Zurich")));
		if (sqlDateTime != null){
			data.setEndDateTime(sqlDateTime.toLocalDateTime());
		}
			
		if (rs.getString(sdb.chargeFilePath) != null) {
			data.setChargeFileAvailable(true);
			data.setChargeFilePath(rs.getString(sdb.chargeFilePath));
		}
		
		if (rs.getString(sdb.statefilePath) != null) {
			data.setStateFileAvailable(true);
			data.setStateFilePath(rs.getString(sdb.statefilePath));
		}
		return data;
	}

}
