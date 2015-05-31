package tracing.jdbcdslog.example1;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdbcdslog.ConnectionLoggingProxy;
import org.junit.Before;
import org.junit.Test;

import tracing.jdbcdslog.example1.MysqlConnection;
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
    	MysqlConnection m = new MysqlConnection();
    	Connection loggingConnection = ConnectionLoggingProxy.wrap(m.getConnection());    	
		Statement stmt = loggingConnection.createStatement();
		
		stmt.execute("create table cheese(name varchar(20), strength int)");
		stmt.execute("insert into cheese(name, strength) VALUES('Peter', 2)");
		stmt.execute("insert into cheese(name, strength) VALUES('Mary', 5)");
    }   
}