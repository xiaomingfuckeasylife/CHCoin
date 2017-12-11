package nxt.db;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

import nxt.BlockchainProcessorImpl;
import nxt.util.Logger;
import nxt.util.StrKit;

/**
 * 
 * @author clark
 * 
 *         2017年12月7日 上午10:47:32
 * 
 *         init database tables.
 * 
 */
public class DbVersion {

	public static void init() {
		try {
			Connection conn = Db.beginTransaction();
			int nextUpdate = -1;
			try {
				// if the version table is not exist will initialize the data
				// base
				Map<String, Object> map = Db.executeQuery(conn, "select next_update from version");
				if (map.size() == 0 || map.size() > 1) {
					throw new RuntimeException("Invalid version table");
				}
				nextUpdate = Integer.valueOf(map.get("next_update") + "");
			} catch (Exception ex) {
				Logger.logMessage("initialize an ampty database");
				Db.executeUpdate(conn,
						"create table version(id int auto_increment primary key , next_update int not null)");
				Db.executeUpdate(conn, "insert version(next_update) values(1)");
				Db.commitTransaction();
			}
			Logger.logMessage(
					"Database update may take a while if needed , current db version " + (nextUpdate - 1) + "...");
			updateSql(conn);
		} catch (Exception ex) {
			Db.rollbackTransaction();
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			Db.endTransaction();
		}
	}

	private static final String SCHEMA_NAME="schema.sql";
	
	/**
	 * update sql schema
	 */
	private static void updateSql(Connection conn) {
		
		try (InputStream is = DbVersion.class.getClassLoader().getSystemResourceAsStream(SCHEMA_NAME);) {
			InputStream tmpIs = is;
			if (is == null) {
				try {
					tmpIs = new FileInputStream(System.getProperty(SCHEMA_NAME));
				} catch (Exception ex) {
					throw new RuntimeException("can not find schema.sql ");
				}
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(tmpIs, "utf-8"));
			String index = null;
			String currSql = "";
			while ((index = reader.readLine()) != null) {
				if (index.indexOf("--") != -1) {
					continue;
				}
				if (StrKit.rmvPrefixBlank(index).trim().endsWith(";")) {
					currSql += index;
					Db.executeUpdate(conn, currSql);
					Db.executeUpdate(conn, "update version set next_update = next_update + 1");
					currSql = "";
				} else {
					currSql += index;
				}
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
		BlockchainProcessorImpl.getInstance().forceScanAtStart();
	}
}
