package tracing.jdbcdslog.example3;


import java.util.Enumeration;
import java.util.Properties;

import javax.sql.XADataSource;

import org.jdbcdslog.ConnectionPoolXADataSourceProxy;
import org.jdbcdslog.XADataSourceProxy;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;

/**
* The preferred class for using Atomikos connection pooling. Use an instance of
* this class if you want to use Atomikos JTA-enabled connection pooling. All
* you need to do is construct an instance and set the required properties as
* outlined below. The resulting bean will automatically register with the
* transaction service (for recovery) and take part in active transactions.
* All SQL done over connections (gotten from this class) will participate in JTA transactions.
*/

public class MyAtomikosDataSourceBean 
extends AbstractDataSourceBean
{
	private static final Logger LOGGER = LoggerFactory.createLogger(MyAtomikosDataSourceBean.class);
	
	
	private static final long serialVersionUID = 1L;
	
	private Properties xaProperties = null;
	private String xaDataSourceClassName;
	private transient XADataSource xaDataSource;
	public MyAtomikosDataSourceBean() 
	{
		this.xaProperties = new Properties();
	}
	
	protected String printXaProperties()
	{
		StringBuffer ret = new StringBuffer();
		if ( xaProperties != null ) {
			Enumeration it = xaProperties.propertyNames();
			ret.append ( "[" );
			boolean first = true;
			while ( it.hasMoreElements() ) {
				if ( ! first ) ret.append ( "," );
				String name = ( String ) it.nextElement();
				String value = xaProperties.getProperty( name);
				ret.append ( name ); ret.append ( "=" ); ret.append ( value );
				first = false;
			}
			ret.append ( "]" );
		}
		return ret.toString();
	}
	
	/**
	 * Gets the properties used to
	 * configure the XADataSource.  
	 */
	
	public Properties getXaProperties()
	{
		return xaProperties;
	}

	/**
	 * Sets the properties (name,value pairs) used to
	 * configure the XADataSource. Required, unless you call setXaDataSource directly.
	 * 
	 * @param xaProperties 
	 * 
	 *
	 */
	public void setXaProperties ( Properties xaProperties ) 
	{
		this.xaProperties = xaProperties;
	}

	/**
	 * Get the XADataSource class name.
	 */
	public String getXaDataSourceClassName() 
	{
		return xaDataSourceClassName;
	}

	/**
	 * Sets the fully qualified underlying XADataSource class name. Required, unless you 
	 * call setXaDataSource directly.
	 * 
	 * @param xaDataSourceClassName
	 */
	public void setXaDataSourceClassName ( String xaDataSourceClassName ) 
	{
		this.xaDataSourceClassName = xaDataSourceClassName;
	}
	
	/**
	 * Gets the configured XADataSource (if any).
	 * @return The instance, or null if none.
	 */
	
	public XADataSource getXaDataSource()
	{
		return xaDataSource;
	}
	
	/**
	 * Sets the XADataSource directly - instead of providing the xaDataSourceClassName and xaProperties.
	 * @param xaDataSource
	 */
	public void setXaDataSource(XADataSource xaDataSource)
	{
		this.xaDataSource = xaDataSource;
	}
	
	
	/*
	protected com.atomikos.datasource.pool.ConnectionFactory doInit() throws Exception 
	{
		if (xaDataSource == null)
		{
			if (xaDataSourceClassName == null)
				throwAtomikosSQLException("Property 'xaDataSourceClassName' cannot be null");
			if (xaProperties == null)
				throwAtomikosSQLException("Property 'xaProperties' cannot be null");
		}
		
		
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo(
				this + ": initializing with [" +
				" xaDataSourceClassName=" + xaDataSourceClassName + "," +
				" uniqueResourceName=" + getUniqueResourceName() + "," +
				" maxPoolSize=" + getMaxPoolSize() + "," +
				" minPoolSize=" + getMinPoolSize() + "," +
				" borrowConnectionTimeout=" + getBorrowConnectionTimeout() + "," +
				" maxIdleTime=" + getMaxIdleTime() + "," +
				" reapTimeout=" + getReapTimeout() + "," +
				" maintenanceInterval=" + getMaintenanceInterval() + "," +
				" testQuery=" + getTestQuery() + "," +
				" xaProperties=" + printXaProperties() + "," +
				" loginTimeout=" + getLoginTimeout() + "," + 
				" maxLifetime=" + getMaxLifetime() +
				"]"
				);
		
		
			if (xaDataSource == null)
			{
				Class xadsClass = null;
				try {
					xadsClass = ClassLoadingHelper.loadClass ( getXaDataSourceClassName() );
				} catch ( ClassNotFoundException nf ) {
					AtomikosSQLException.throwAtomikosSQLException ( "The class '" + getXaDataSourceClassName() +
							"' specified by property 'xaDataSourceClassName' could not be found in the classpath. Please make sure the spelling is correct, and that the required jar(s) are in the classpath." , nf );
					 
				}
				Object driver =  xadsClass.newInstance();
				if ( ! ( driver instanceof XADataSource ) ) {
					AtomikosSQLException.throwAtomikosSQLException (
							 "The class '" + getXaDataSourceClassName() +
								"' specified by property 'xaDataSourceClassName' does not implement the required interface javax.jdbc.XADataSource. Please make sure the spelling is correct, and check your JDBC driver vendor's documentation." 
					);
				}
				xaDataSource = (XADataSource) driver;
				xaDataSource.setLoginTimeout ( getLoginTimeout() );
				xaDataSource.setLogWriter ( getLogWriter() );
				PropertyUtils.setProperties(xaDataSource, xaProperties );
			}
			
			JdbcTransactionalResource tr = new JdbcTransactionalResource(getUniqueResourceName() , xaDataSource);
			com.atomikos.datasource.pool.ConnectionFactory cf = new com.atomikos.jdbc.AtomikosXAConnectionFactory(xaDataSource, tr, this);
			Configuration.addResource ( tr );
			
			return cf;
	}
	*/
	
	protected com.atomikos.datasource.pool.ConnectionFactory doInit() throws Exception {
        if (xaDataSource == null) {
            if (xaDataSourceClassName == null)
                throwAtomikosSQLException("Property 'xaDataSourceClassName' cannot be null");
            if (xaProperties == null)
                throwAtomikosSQLException("Property 'xaProperties' cannot be null");
        }

        if (LOGGER.isInfoEnabled())
            LOGGER.logInfo(this + ": initializing with [" + " xaDataSourceClassName=" + xaDataSourceClassName
                    + "," + " uniqueResourceName=" + getUniqueResourceName() + "," + " maxPoolSize="
                    + getMaxPoolSize() + "," + " minPoolSize=" + getMinPoolSize() + ","
                    + " borrowConnectionTimeout=" + getBorrowConnectionTimeout() + "," + " maxIdleTime="
                    + getMaxIdleTime() + "," + " reapTimeout=" + getReapTimeout() + ","
                    + " maintenanceInterval=" + getMaintenanceInterval() + "," + " testQuery="
                    + getTestQuery() + "," + " xaProperties=" + printXaProperties() + "," + " loginTimeout="
                    + getLoginTimeout() + "," + " maxLifetime=" + getMaxLifetime() + "]");

        if (xaDataSource == null) {
            Class xadsClass = null;
            try {
                xadsClass = ClassLoadingHelper.loadClass(getXaDataSourceClassName());
            } catch (ClassNotFoundException nf) {
                AtomikosSQLException
                        .throwAtomikosSQLException(
                                "The class '"
                                        + getXaDataSourceClassName()
                                        + "' specified by property 'xaDataSourceClassName' could not be found in the classpath. Please make sure the spelling is correct, and that the required jar(s) are in the classpath.",
                                nf);

            }
            Object driver = xadsClass.newInstance();
            if (!(driver instanceof XADataSource)) {
                AtomikosSQLException
                        .throwAtomikosSQLException("The class '"
                                + getXaDataSourceClassName()
                                + "' specified by property 'xaDataSourceClassName' does not implement the required interface javax.jdbc.XADataSource. Please make sure the spelling is correct, and check your JDBC driver vendor's documentation.");
            }
            xaDataSource = (XADataSource) driver;
            PropertyUtils.setProperties(xaDataSource, xaProperties);
            XADataSourceProxy proxy = (XADataSourceProxy) xaDataSource;
            XADataSource targetDs = (XADataSource) Reflections.getFieldValue(proxy, "targetDS");
            targetDs.setLoginTimeout(getLoginTimeout());
            targetDs.setLogWriter(getLogWriter());
        }

        JdbcTransactionalResource tr = new JdbcTransactionalResource(getUniqueResourceName(), xaDataSource);
        com.atomikos.datasource.pool.ConnectionFactory cf = new MyAtomikosXAConnectionFactory(xaDataSource, tr, this);
        Configuration.addResource(tr);

        return cf;
    }
	
	protected void doClose() 
	{
		RecoverableResource res = Configuration.getResource ( getUniqueResourceName() );
		if ( res != null ) {
			Configuration.removeResource ( getUniqueResourceName() );
			//fix for case 26005
			res.close();
		}
	}	

	public String toString() 
	{
		String ret = "AtomikosDataSoureBean";
		String name = getUniqueResourceName();
		if ( name != null ) {
			ret = ret + " '" + name + "'";
		}
		return ret;
	}


	protected boolean isAssignableFromWrappedVendorClass(Class<?> iface) {
		boolean ret = false;
		 if (xaDataSource != null ) { 
			 ret = iface.isAssignableFrom(xaDataSource.getClass());
		 }
		 return ret;
	}

	@Override
	protected Object unwrapVendorInstance() {
		return xaDataSource;
	}


}

