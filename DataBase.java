
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.io.*;

public class DataBase {
	private static DataBase _instance;

//static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

	// update USER, PASS and DB URL according to credentials provided by the
	// website:
	// https://remotemysql.com/
	// in future move these hard coded strings into separated config file or even
	// better env variables
	static private final String DB = "SmFGAHPAE1";
//	static private final String DB_URL = "https://remotemysql.com/phpmyadmin/" + DB + "?useSSL=false";
	static private final String DB_URL = "jdbc:mysql://remotemysql.com/" + DB + "?useSSL=false";

	static private final String USER = "SmFGAHPAE1";
	static private final String PASS = "PrfUzkZFEX";
	static private Connection conn;

	private DataBase() {
	}

	public static DataBase getInstance() {
		if (_instance == null) {
			_instance = new DataBase();
			try {
				Class.forName(JDBC_DRIVER);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("<<<DATABASE>> CONNECTED TO DB");
		}
		return _instance;
	}

	public ArrayList<Item> get_flowers() {

		ArrayList<Item> catalog = new ArrayList<Item>();
		try {
			ResultSet rs = this.get_TableResultSet("Item");

			while (rs.next()) {
				String Name = rs.getString("Name");
				double Price = rs.getInt("Price");
				String Kind = rs.getString("Kind");
				String Color = rs.getString("Color");
				String Size = (rs.getString("Size"));
				String id = (rs.getString("ID"));
				catalog.add(new Item(Name, Price, Kind, Color, Size, id));
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}
		return catalog;

	}

	public String add_to_DB(Object object) {

		try {
			String table = object.getClass().getName();
			ResultSet rs = this.get_TableResultSet(table);

			boolean id_flag = false;

			if (this.exists_in_DB(object).equals("TRUE"))
				return table + " " + object.toString() + " ID IN DB";
			switch(table)
			{
			case "Item" :
				{	
					Item item = (Item) object;
					PreparedStatement stmt1 = conn.prepareStatement(
							"INSERT INTO Item(Name, Price, Kind,Color,Size,ID,Image) VALUES (?, ?, ?,?,?,?,?)");
					stmt1.setString(1, item.getName());
					stmt1.setInt(2, (int) item.getPrice());
					stmt1.setString(3, item.getKind());
					stmt1.setString(4, item.getColor());
					stmt1.setInt(5, 11);
					stmt1.setInt(6, Integer.valueOf(item.getId()));
					stmt1.setString(7, "NOTYET");
					stmt1.executeUpdate();
				}
		
			}
			rs = this.get_TableResultSet(table);

			boolean id_flag2 = false;

			while (rs.next()) {
				String id = (rs.getString("ID"));
				if (id.equals(object.toString()))
					id_flag2 = true;
			}
			if (this.exists_in_DB(object).equals("TRUE"))
				return table + " " + object.toString() + " Added to DB";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "FAIL-UNKNOWN";

	}

	public String delet_from_DB(Object object) {
		try {

			boolean id_flag = false;
			String table = object.getClass().getName();
			ResultSet rs = this.get_TableResultSet(table);

			while (rs.next()) {
				String id = (rs.getString("ID"));
				if (id.equals(object.toString())) {
					id_flag = true;
					PreparedStatement st = conn.prepareStatement("DELETE FROM " + table + " WHERE ID = ?");
					st.setInt(1, Integer.valueOf(object.toString()));
					st.executeUpdate();
				}
			}
			if (!id_flag)
				return "FAIL-NO-ID IN";
			else {
				if (this.exists_in_DB(object).equals("FALSE"))
					return table + " " + object.toString() + " DELETED FROM DB";
				else
					System.out.println("<<<DATABASE>>FAIL - CHECK DB");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "UNKNOWN-FAIL";
	}

	public String exists_in_DB(Object object) {
		String table = object.getClass().getName();
		try {
			ResultSet rs = this.get_TableResultSet(table);
			while (rs.next()) {
				String id = (rs.getString("ID"));
				if (id.equals(object.toString()))
					return "TRUE";
			}
			return "FALSE";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "FAIL-UNKNOWN";
	}

	private ResultSet get_TableResultSet(String table) throws SQLException {
		Statement stmt;
		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

		PreparedStatement prep_stmt = conn.prepareStatement("SELECT * FROM " + table);
		ResultSet rs = prep_stmt.executeQuery();
		return rs;
	}

	protected void finalize() throws SQLException {
		System.out.println("<<<DATABASE>> Closing Connection");
		conn.close();
	}
}