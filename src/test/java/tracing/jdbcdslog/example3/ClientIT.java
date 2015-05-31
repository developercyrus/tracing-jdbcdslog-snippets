package tracing.jdbcdslog.example3;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;

import static org.junit.Assert.assertEquals;

public class ClientIT {
	static Logger logger = Logger.getLogger(ClientIT.class);

	@Before
	public void setup() {
		URL propURL = ClientIT.class.getResource("log4j.properties");
		String path = new File(propURL.getFile()).getParent();
		System.out.println(path);
		// for variable ${log4jpath} inside log4j.properties
		System.setProperty("log4jpath", path);
		PropertyConfigurator.configure(propURL);
	}

	@Test
	public void test1() throws Exception {
		UserTransaction utx = new UserTransactionImp();
		try {
			utx.begin();	
			this.insert();			
			utx.commit();
		} catch (Exception e) {			
			try {
				utx.rollback();
				System.out.println("rollbak success");
				e.printStackTrace();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}          
		} 

	}

	public void insert() throws SQLException {
		DataSource ds = getDataSource();
		Connection conn = ds.getConnection();
		Statement s = conn.createStatement();
		
		/*
			it throws exception:		 	
		 	"java.sql.SQLException: XAER_RMFAIL: The command cannot be executed when global transaction is in the  ACTIVE state"
			
			Create table statements have an implicit commit (in MySQL at least) so the first table creation tries to end the transaction. 
			The connection is, however, in the active state which doesn’t allow for implicit commits and causes the above exception to be thrown. 
			
			http://www.naturalborncoder.com/java/java-ee/2011/07/21/local-transaction-already-has-1-non-xa-resource/

		 */
		//s.executeUpdate("create table cheese(name varchar(20), strength int)");
		s.execute("insert into cheese(name, strength) VALUES('Peter', 2)");
		s.execute("insert into cheese(name, strength) VALUES('Mary', 5)");
		s.close();
		conn.close();
	}

	public DataSource getDataSource() {
		/*
			it throws exception			
			Cannot initialize AtomikosDataSourceBean java.sql.SQLException: targetDS doesn't have setLogWriter() method

			fix it by creating 3 separate classes, based on item 1
			1. MyAtomikosDataSourceBean.java
			2. MyAtomikosXAConnectionFactory.java
			3. Reflections.java
			
			reference:
			1. http://sgq0085.iteye.com/blog/2039534
								
		 */
		MyAtomikosDataSourceBean ds = new MyAtomikosDataSourceBean();
		// unique resource name for transaction recovery configuration
		ds.setUniqueResourceName("jdbc/mysql");		
		/*
		  	replace the class name 
		  	from "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource"
		  	to "org.jdbcdslog.XADataSourceProxy"
		  	
		  	https://code.google.com/p/jdbcdslog/wiki/UserGuide#Setup_logging_JDBC_DataSource_proxy
		 */
		ds.setXaDataSourceClassName("org.jdbcdslog.XADataSourceProxy");
		Properties p = new Properties();
		p.setProperty("serverName", "localhost");
		p.setProperty("portNumber", "3306");
		p.setProperty("databaseName", "tracing_jdbcdslog_example3");
		p.setProperty("user", "root");
		p.setProperty("password", "");
		//	remember to change "targetDS"
		p.setProperty("URL", "jdbc:mysql://localhost:3306/tracing_jdbcdslog_example3?targetDS=com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		ds.setXaProperties(p);

		return ds;
	}

}