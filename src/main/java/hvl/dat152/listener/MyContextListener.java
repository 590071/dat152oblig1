package hvl.dat152.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import hvl.dat152.dataaccess.TaskDAO;
import hvl.dat152.dataaccess.jdbc.TaskDAOImpl;

public class MyContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Application is started. The database will be initialized.");
		TaskDAO taskDAO = new TaskDAOImpl();
		
		taskDAO.setupDB();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Application was stopped.");
	}
}
