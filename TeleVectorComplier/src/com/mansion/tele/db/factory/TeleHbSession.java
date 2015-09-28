package com.mansion.tele.db.factory;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import com.mansion.tele.business.common.TeleContext;
import com.mansion.tele.config.project.DataBaseInfo;

/**
 * Configures and provides access to Hibernate sessions, tied to the
 * current thread of execution.  Follows the Thread Local Session
 * pattern, see {@link http://hibernate.org/42.html }.
 */
public class TeleHbSession {

    /** 
     * Location of hibernate.cfg.xml file.
     * Location should be on the classpath as Hibernate uses  
     * #resourceAsStream style lookup for its configuration file. 
     * The default classpath location of the hibernate config file is 
     * in the default package. Use #setConfigFile() to update 
     * the location of the configuration file for the current session.   
     */
	private static final ThreadLocal<Session> THREADLOCAL = new ThreadLocal<Session>();
	/**
	 * 配置对象 
	 */
	private  Configuration configuration = new Configuration();    
	/**
	 * 获得session的工厂
	 */
    private org.hibernate.SessionFactory sessionFactory;
    /**
     * jndi名
     */
    private String jndi;

    /**
     * 构造方法
     * @param jndi String
     */
    public TeleHbSession(String jndi) {
    	
		this.jndi = jndi;
		this.sessionFactory = null;

    		//构建hibernateSessionFactory
    		this.rebuildSessionFactory();
    		
    }
	
	/**
     * Returns the ThreadLocal Session instance.  Lazy initialize
     * the <code>SessionFactory</code> if needed.
     *
     *  @return Session
     */
    public Session getSession()  {
    	
        Session session = (Session) THREADLOCAL.get();
    	

		if (session == null || !session.isOpen()) {
			if (this.sessionFactory == null) {
				this.rebuildSessionFactory();
			}
			if(this.sessionFactory != null) {
				
				session = this.sessionFactory.openSession();
			}else{
				session = null;
			}
			THREADLOCAL.set(session);
		}
        return session;
    }

	/**
     *  Rebuild hibernate session factory
     *
     */
	public void rebuildSessionFactory() {
		try {
			this.configuration.configure("hibernate.cfg.xml");
			Map<String,DataBaseInfo> map = 
				TeleContext.get().getMap();
			this.configuration.setProperty("hibernate.connection.url",
					map.get(this.jndi).getStrURL() );
			this.configuration.setProperty("hibernate.connection.username",
					map.get(this.jndi).getStrUserName());
			this.configuration.setProperty("hibernate.connection.password",
					map.get(this.jndi).getStrPassword());
			this.configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
			this.configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
			this.sessionFactory = this.configuration.buildSessionFactory();
		} catch (HibernateException e) {
			System.err.println("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}

	/**
     *  Close the single hibernate session instance.
     *
     */
    public void closeSession()  {
        Session session = (Session) THREADLOCAL.get();
        THREADLOCAL.set(null);

        if (session != null && session.isOpen()) {
            session.close();
        }
    }

	/**
     *  @return SessionFactory
     *
     */
	public org.hibernate.SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
     *  @return Configuration
     *
     */
	public Configuration getConfiguration() {
		return this.configuration;
	}
}